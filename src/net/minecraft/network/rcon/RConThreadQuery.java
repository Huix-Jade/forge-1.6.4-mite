package net.minecraft.network.rcon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.server.MinecraftServer;

public class RConThreadQuery extends RConThreadBase {
   private long lastAuthCheckTime;
   private int queryPort;
   private int serverPort;
   private int maxPlayers;
   private String serverMotd;
   private String worldName;
   private DatagramSocket querySocket;
   private byte[] buffer = new byte[1460];
   private DatagramPacket incomingPacket;
   private Map field_72644_p;
   private String queryHostname;
   private String serverHostname;
   private Map queryClients;
   private long time;
   private RConOutputStream output;
   private long lastQueryResponseTime;

   public RConThreadQuery(IServer var1) {
      super(var1);
      this.queryPort = var1.getIntProperty("query.port", 0);
      this.serverHostname = var1.getHostname();
      this.serverPort = var1.getPort();
      this.serverMotd = var1.getServerMOTD();
      this.maxPlayers = var1.getMaxPlayers();
      this.worldName = var1.getFolderName();
      this.lastQueryResponseTime = 0L;
      this.queryHostname = "0.0.0.0";
      if (0 != this.serverHostname.length() && !this.queryHostname.equals(this.serverHostname)) {
         this.queryHostname = this.serverHostname;
      } else {
         this.serverHostname = "0.0.0.0";

         try {
            InetAddress var2 = InetAddress.getLocalHost();
            this.queryHostname = var2.getHostAddress();
         } catch (UnknownHostException var3) {
            this.logWarning("Unable to determine local host IP, please set server-ip in '" + var1.getSettingsFilename() + "' : " + var3.getMessage());
         }
      }

      if (0 == this.queryPort) {
         this.queryPort = this.serverPort;
         this.logInfo("Setting default query port to " + this.queryPort);
         var1.setProperty("query.port", this.queryPort);
         var1.setProperty("debug", false);
         var1.saveProperties();
      }

      this.field_72644_p = new HashMap();
      this.output = new RConOutputStream(1460);
      this.queryClients = new HashMap();
      this.time = (new Date()).getTime();
   }

   private void sendResponsePacket(byte[] var1, DatagramPacket var2) throws IOException {
      this.querySocket.send(new DatagramPacket(var1, var1.length, var2.getSocketAddress()));
   }

   private boolean parseIncomingPacket(DatagramPacket var1) throws IOException {
      byte[] var2 = var1.getData();
      int var3 = var1.getLength();
      SocketAddress var4 = var1.getSocketAddress();
      this.logDebug("Packet len " + var3 + " [" + var4 + "]");
      if (3 <= var3 && -2 == var2[0] && -3 == var2[1]) {
         this.logDebug("Packet '" + RConUtils.getByteAsHexString(var2[2]) + "' [" + var4 + "]");
         switch (var2[2]) {
            case 0:
               if (!this.verifyClientAuth(var1)) {
                  this.logDebug("Invalid challenge [" + var4 + "]");
                  return false;
               } else if (15 == var3) {
                  this.sendResponsePacket(this.createQueryResponse(var1), var1);
                  this.logDebug("Rules [" + var4 + "]");
               } else {
                  RConOutputStream var5 = new RConOutputStream(1460);
                  var5.writeInt(0);
                  var5.writeByteArray(this.getRequestID(var1.getSocketAddress()));
                  var5.writeString(this.serverMotd);
                  var5.writeString("SMP");
                  var5.writeString(this.worldName);
                  var5.writeString(Integer.toString(this.getNumberOfPlayers()));
                  var5.writeString(Integer.toString(this.maxPlayers));
                  var5.writeShort((short)this.serverPort);
                  var5.writeString(this.queryHostname);
                  this.sendResponsePacket(var5.toByteArray(), var1);
                  this.logDebug("Status [" + var4 + "]");
               }
            default:
               return true;
            case 9:
               this.sendAuthChallenge(var1);
               this.logDebug("Challenge [" + var4 + "]");
               return true;
         }
      } else {
         this.logDebug("Invalid packet [" + var4 + "]");
         return false;
      }
   }

   private byte[] createQueryResponse(DatagramPacket var1) throws IOException {
      long var2 = MinecraftServer.getSystemTimeMillis();
      if (var2 < this.lastQueryResponseTime + 5000L) {
         byte[] var7 = this.output.toByteArray();
         byte[] var8 = this.getRequestID(var1.getSocketAddress());
         var7[1] = var8[0];
         var7[2] = var8[1];
         var7[3] = var8[2];
         var7[4] = var8[3];
         return var7;
      } else {
         this.lastQueryResponseTime = var2;
         this.output.reset();
         this.output.writeInt(0);
         this.output.writeByteArray(this.getRequestID(var1.getSocketAddress()));
         this.output.writeString("splitnum");
         this.output.writeInt(128);
         this.output.writeInt(0);
         this.output.writeString("hostname");
         this.output.writeString(this.serverMotd);
         this.output.writeString("gametype");
         this.output.writeString("SMP");
         this.output.writeString("game_id");
         this.output.writeString("MINECRAFT");
         this.output.writeString("version");
         this.output.writeString(this.server.getMinecraftVersion());
         this.output.writeString("plugins");
         this.output.writeString(this.server.getPlugins());
         this.output.writeString("map");
         this.output.writeString(this.worldName);
         this.output.writeString("numplayers");
         this.output.writeString("" + this.getNumberOfPlayers());
         this.output.writeString("maxplayers");
         this.output.writeString("" + this.maxPlayers);
         this.output.writeString("hostport");
         this.output.writeString("" + this.serverPort);
         this.output.writeString("hostip");
         this.output.writeString(this.queryHostname);
         this.output.writeInt(0);
         this.output.writeInt(1);
         this.output.writeString("player_");
         this.output.writeInt(0);
         String[] var4 = this.server.getAllUsernames();
         byte var5 = (byte)var4.length;

         for(byte var6 = (byte)(var5 - 1); var6 >= 0; --var6) {
            this.output.writeString(var4[var6]);
         }

         this.output.writeInt(0);
         return this.output.toByteArray();
      }
   }

   private byte[] getRequestID(SocketAddress var1) {
      return ((RConThreadQueryAuth)this.queryClients.get(var1)).getRequestId();
   }

   private Boolean verifyClientAuth(DatagramPacket var1) {
      SocketAddress var2 = var1.getSocketAddress();
      if (!this.queryClients.containsKey(var2)) {
         return false;
      } else {
         byte[] var3 = var1.getData();
         return ((RConThreadQueryAuth)this.queryClients.get(var2)).getRandomChallenge() != RConUtils.getBytesAsBEint(var3, 7, var1.getLength()) ? false : true;
      }
   }

   private void sendAuthChallenge(DatagramPacket var1) throws IOException {
      RConThreadQueryAuth var2 = new RConThreadQueryAuth(this, var1);
      this.queryClients.put(var1.getSocketAddress(), var2);
      this.sendResponsePacket(var2.getChallengeValue(), var1);
   }

   private void cleanQueryClientsMap() {
      if (this.running) {
         long var1 = MinecraftServer.getSystemTimeMillis();
         if (var1 >= this.lastAuthCheckTime + 30000L) {
            this.lastAuthCheckTime = var1;
            Iterator var3 = this.queryClients.entrySet().iterator();

            while(var3.hasNext()) {
               Map.Entry var4 = (Map.Entry)var3.next();
               if (((RConThreadQueryAuth)var4.getValue()).hasExpired(var1)) {
                  var3.remove();
               }
            }

         }
      }
   }

   public void run() {
      this.logInfo("Query running on " + this.serverHostname + ":" + this.queryPort);
      this.lastAuthCheckTime = MinecraftServer.getSystemTimeMillis();
      this.incomingPacket = new DatagramPacket(this.buffer, this.buffer.length);

      try {
         while(this.running) {
            try {
               this.querySocket.receive(this.incomingPacket);
               this.cleanQueryClientsMap();
               this.parseIncomingPacket(this.incomingPacket);
            } catch (SocketTimeoutException var7) {
               this.cleanQueryClientsMap();
            } catch (PortUnreachableException var8) {
            } catch (IOException var9) {
               this.stopWithException(var9);
            }
         }
      } finally {
         this.closeAllSockets();
      }

   }

   public void startThread() {
      if (!this.running) {
         if (0 < this.queryPort && 65535 >= this.queryPort) {
            if (this.initQuerySystem()) {
               super.startThread();
            }

         } else {
            this.logWarning("Invalid query port " + this.queryPort + " found in '" + this.server.getSettingsFilename() + "' (queries disabled)");
         }
      }
   }

   private void stopWithException(Exception var1) {
      if (this.running) {
         this.logWarning("Unexpected exception, buggy JRE? (" + var1.toString() + ")");
         if (!this.initQuerySystem()) {
            this.logSevere("Failed to recover from buggy JRE, shutting down!");
            this.running = false;
         }

      }
   }

   private boolean initQuerySystem() {
      try {
         this.querySocket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.serverHostname));
         this.registerSocket(this.querySocket);
         this.querySocket.setSoTimeout(500);
         return true;
      } catch (SocketException var2) {
         this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Socket): " + var2.getMessage());
      } catch (UnknownHostException var3) {
         this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Unknown Host): " + var3.getMessage());
      } catch (Exception var4) {
         this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (E): " + var4.getMessage());
      }

      return false;
   }
}
