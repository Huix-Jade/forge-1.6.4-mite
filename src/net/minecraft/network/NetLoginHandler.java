package net.minecraft.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.crypto.SecretKey;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerListenThread;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class NetLoginHandler extends NetHandler {
   private static Random rand = new Random();
   private byte[] verifyToken;
   private final MinecraftServer mcServer;
   public final TcpConnection myTCPConnection;
   public boolean connectionComplete;
   private int connectionTimer;
   private String clientUsername;
   private volatile boolean field_72544_i;
   private String loginServerId = "";
   private boolean field_92079_k;
   private SecretKey sharedKey;

   public NetLoginHandler(MinecraftServer par1MinecraftServer, Socket par2Socket, String par3Str) throws IOException {
      this.mcServer = par1MinecraftServer;
      this.myTCPConnection = new TcpConnection(par1MinecraftServer.getLogAgent(), par2Socket, par3Str, this, par1MinecraftServer.getKeyPair().getPrivate());
      this.myTCPConnection.field_74468_e = 0;
   }

   public void tryLogin() {
      if (this.field_72544_i) {
         this.initializePlayerConnection();
      }

      if (this.connectionTimer++ == 6000) {
         this.raiseErrorAndDisconnect("Took too long to log in");
      } else {
         this.myTCPConnection.processReadPackets();
      }

   }

   public void raiseErrorAndDisconnect(String par1Str) {
      try {
         this.mcServer.getLogAgent().logInfo("Disconnecting " + this.getUsernameAndAddress() + ": " + par1Str);
         this.myTCPConnection.addToSendQueue(new Packet255KickDisconnect(par1Str, false));
         this.myTCPConnection.serverShutdown();
         this.connectionComplete = true;
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public void handleClientProtocol(Packet2ClientProtocol par1Packet2ClientProtocol) {
      if (this.clientUsername != null) {
         this.raiseErrorAndDisconnect("Quit repeating yourself!");
      } else {
         this.clientUsername = par1Packet2ClientProtocol.getUsername();
         if (!this.clientUsername.equals(StringUtils.stripControlCodes(this.clientUsername))) {
            this.raiseErrorAndDisconnect("Invalid username!");
         } else {
            PublicKey var2 = this.mcServer.getKeyPair().getPublic();
            if (par1Packet2ClientProtocol.getProtocolVersion() != 78) {
               if (par1Packet2ClientProtocol.getProtocolVersion() > 78) {
                  this.raiseErrorAndDisconnect("Outdated server!");
               } else {
                  this.raiseErrorAndDisconnect("Outdated client!");
               }

            } else if ("1.6.4".equals(par1Packet2ClientProtocol.MC_version) && "R196".equals(par1Packet2ClientProtocol.MITE_release_number)) {
               if (!this.mcServer.getConfigurationManager().isAllowedToLogin(this.clientUsername)) {
                  this.raiseErrorAndDisconnect("You are not white-listed on this server!");
               } else {
                  if (DedicatedServer.isTournamentThatUsesAllottedTimes()) {
                     Long tick_of_disconnection = (Long)DedicatedServer.players_kicked_for_depleted_time_shares.get(this.clientUsername);
                     if (tick_of_disconnection != null) {
                        long current_tick = DedicatedServer.getServer().worldServerForDimension(0).getTotalWorldTime();
                        if (current_tick - tick_of_disconnection < 72000L) {
                           this.raiseErrorAndDisconnect("Please wait at least an hour for your time share to replenish");
                           return;
                        }
                     }
                  }

                  if (DedicatedServer.disconnection_penalty_enabled) {
                     SoonestReconnectionTime srt = DedicatedServer.getSoonestReconnectionTime(this.clientUsername);
                     if (srt != null) {
                        World world = DedicatedServer.getServer().worldServerForDimension(0);
                        long current_tick = world.getTotalWorldTime();
                        srt.ticks_disconnected += Math.max(current_tick - srt.tick_of_disconnection, 0L);
                        boolean reconnection_prevented = true;
                        int hour_of_latest_reconnection = World.getHourOfLatestReconnection();
                        if (world.getHourOfDay() == hour_of_latest_reconnection) {
                           reconnection_prevented = false;
                           srt.ticks_disconnected = 0L;
                        } else if (srt.ticks_disconnected <= 600L) {
                           reconnection_prevented = false;
                        } else if (current_tick >= srt.soonest_reconnection_tick) {
                           reconnection_prevented = world.getHourOfDay() < srt.adjusted_hour_of_disconnection || world.getHourOfDay() > hour_of_latest_reconnection;
                           if (!reconnection_prevented) {
                              srt.ticks_disconnected = 0L;
                           }
                        }

                        if (reconnection_prevented) {
                           int message_type = 1;

                           int ticks_to_wait;
                           for(ticks_to_wait = (int)(srt.soonest_reconnection_tick - current_tick); ticks_to_wait <= 0; ticks_to_wait += 24000) {
                           }

                           int ticks_until_hour_of_latest_reconnection = hour_of_latest_reconnection * 1000 - world.getAdjustedTimeOfDay();
                           if (ticks_until_hour_of_latest_reconnection < 0) {
                              ticks_until_hour_of_latest_reconnection += 24000;
                           }

                           if (ticks_until_hour_of_latest_reconnection < ticks_to_wait) {
                              ticks_to_wait = ticks_until_hour_of_latest_reconnection;
                              message_type = 2;
                           }

                           int seconds_delay = ticks_to_wait / 20;
                           this.myTCPConnection.addToSendQueue((new Packet85SimpleSignal(EnumSignal.reconnection_delay)).setByte(message_type).setShort(srt.adjusted_hour_of_disconnection).setInteger(seconds_delay));
                           this.raiseErrorAndDisconnect("");
                           return;
                        }
                     }
                  }

                  this.loginServerId = this.mcServer.isServerInOnlineMode() ? Long.toString(rand.nextLong(), 16) : "-";
                  this.verifyToken = new byte[4];
                  rand.nextBytes(this.verifyToken);
                  this.myTCPConnection.addToSendQueue(new Packet253ServerAuthData(this.loginServerId, var2, this.verifyToken));
               }
            } else {
               this.raiseErrorAndDisconnect("This server requires a 1.6.4-MITE R196 client.");
            }
         }
      }
   }

   public void handleSharedKey(Packet252SharedKey par1Packet252SharedKey) {
      PrivateKey var2 = this.mcServer.getKeyPair().getPrivate();
      this.sharedKey = par1Packet252SharedKey.getSharedKey(var2);
      if (!Arrays.equals(this.verifyToken, par1Packet252SharedKey.getVerifyToken(var2))) {
         this.raiseErrorAndDisconnect("Invalid client reply");
      }

      this.myTCPConnection.addToSendQueue(new Packet252SharedKey());
   }

   public void handleClientCommand(Packet205ClientCommand par1Packet205ClientCommand) {
      if (par1Packet205ClientCommand.forceRespawn == 0) {
         if (this.field_92079_k) {
            this.raiseErrorAndDisconnect("Duplicate login");
            return;
         }

         this.field_92079_k = true;
         if (this.mcServer.isServerInOnlineMode()) {
            (new ThreadLoginVerifier(this)).start();
         } else {
            this.field_72544_i = true;
         }
      }

   }

   public void handleLogin(Packet1Login par1Packet1Login) {
      FMLNetworkHandler.handleLoginPacketOnServer(this, par1Packet1Login);
   }

   public void initializePlayerConnection() {
      FMLNetworkHandler.onConnectionReceivedFromClient(this, this.mcServer, this.myTCPConnection.getSocketAddress(), this.clientUsername);
   }

   public void completeConnection(String var1) {
      if (var1 != null) {
         this.raiseErrorAndDisconnect(var1);
      } else {
         EntityPlayerMP var2 = this.mcServer.getConfigurationManager().createPlayerForUser(this.clientUsername);
         if (var2 != null) {
            this.mcServer.getConfigurationManager().initializeConnectionToPlayer(this.myTCPConnection, var2);
         }
      }

      this.connectionComplete = true;
   }

   public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj) {
      this.mcServer.getLogAgent().logInfo(this.getUsernameAndAddress() + " lost connection");
      this.connectionComplete = true;
   }

   public void handleServerPing(Packet254ServerPing par1Packet254ServerPing) {
      try {
         ServerConfigurationManager var2 = this.mcServer.getConfigurationManager();
         String var3 = null;
         int current_player_count = var2.getCurrentPlayerCount();
         if (DedicatedServer.isTournament() && this.mcServer.isZevimrgvOnServer()) {
            --current_player_count;
         }

         if (par1Packet254ServerPing.func_140050_d()) {
            var3 = this.mcServer.getMOTD() + "§" + current_player_count + "§" + var2.getMaxPlayers();
         } else {
            List var4 = Arrays.asList(1, 78, this.mcServer.getMinecraftVersion(), this.mcServer.getMOTD(), current_player_count, var2.getMaxPlayers());

            Object var6;
            for(Iterator var5 = var4.iterator(); var5.hasNext(); var3 = var3 + var6.toString().replaceAll("\u0000", "")) {
               var6 = var5.next();
               if (var3 == null) {
                  var3 = "§";
               } else {
                  var3 = var3 + "\u0000";
               }
            }
         }

         InetAddress var8 = null;
         if (this.myTCPConnection.getSocket() != null) {
            var8 = this.myTCPConnection.getSocket().getInetAddress();
         }

         this.myTCPConnection.addToSendQueue(new Packet255KickDisconnect(var3, false));
         this.myTCPConnection.serverShutdown();
         if (var8 != null && this.mcServer.getNetworkThread() instanceof DedicatedServerListenThread) {
            ((DedicatedServerListenThread)this.mcServer.getNetworkThread()).func_71761_a(var8);
         }

         this.connectionComplete = true;
      } catch (Exception var8) {
         Exception var7 = var8;
         var7.printStackTrace();
      }

   }

   public void unexpectedPacket(Packet par1Packet) {
      this.raiseErrorAndDisconnect("Protocol error");
   }

   public String getUsernameAndAddress() {
      return this.clientUsername != null ? this.clientUsername + " [" + this.myTCPConnection.getSocketAddress().toString() + "]" : this.myTCPConnection.getSocketAddress().toString();
   }

   public boolean isServerHandler() {
      return true;
   }

   public boolean isConnectionClosed() {
      return this.connectionComplete;
   }

   static String getServerId(NetLoginHandler par0NetLoginHandler) {
      return par0NetLoginHandler.loginServerId;
   }

   static MinecraftServer getLoginMinecraftServer(NetLoginHandler par0NetLoginHandler) {
      return par0NetLoginHandler.mcServer;
   }

   static SecretKey getSharedKey(NetLoginHandler par0NetLoginHandler) {
      return par0NetLoginHandler.sharedKey;
   }

   public static String getClientUsername(NetLoginHandler par0NetLoginHandler) {
      return par0NetLoginHandler.clientUsername;
   }

   public static boolean func_72531_a(NetLoginHandler par0NetLoginHandler, boolean par1) {
      return par0NetLoginHandler.field_72544_i = par1;
   }

   public INetworkManager getNetManager() {
      return this.myTCPConnection;
   }

   @Override
   public void handleCustomPayload(Packet250CustomPayload p_72501_1_)
   {
      FMLNetworkHandler.handlePacket250Packet(p_72501_1_, this.getNetManager(), this);
   }

   @Override
   public void handleVanilla250Packet(Packet250CustomPayload payload)
   {
      // NOOP for login
   }

   @Override
   public EntityPlayer getPlayer()
   {
      return null;
   }
}
