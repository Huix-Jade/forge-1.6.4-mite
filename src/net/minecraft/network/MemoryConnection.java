package net.minecraft.network;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.logging.ILogAgent;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;

public final class MemoryConnection implements INetworkManager {
   private static final SocketAddress mySocketAddress = new InetSocketAddress("127.0.0.1", 0);
   private final List readPacketCache = Collections.synchronizedList(new ArrayList());
   private final ILogAgent field_98214_c;
   private MemoryConnection pairedConnection;
   private NetHandler myNetHandler;
   private boolean shuttingDown;
   private String shutdownReason = "";
   private Object[] field_74439_g;
   private boolean gamePaused;

   public MemoryConnection(ILogAgent par1ILogAgent, NetHandler par2NetHandler) {
      this.myNetHandler = par2NetHandler;
      this.field_98214_c = par1ILogAgent;
   }

   public void setNetHandler(NetHandler par1NetHandler) {
      this.myNetHandler = par1NetHandler;
   }

   public void addToSendQueue(Packet par1Packet) {
      if (!this.shuttingDown) {
         this.pairedConnection.processOrCachePacket(par1Packet);
      }

   }

   public void closeConnections() {
      this.pairedConnection = null;
      this.myNetHandler = null;
   }

   public boolean isConnectionActive() {
      return !this.shuttingDown && this.pairedConnection != null;
   }

   public void wakeThreads() {
   }

   public void processReadPackets() {
      boolean is_MITE_DS_client_player = Main.is_MITE_DS && this.myNetHandler instanceof NetClientHandler;
      int var1 = 2500;

      while(var1-- >= 0 && !this.readPacketCache.isEmpty()) {
         Packet var2 = (Packet)this.readPacketCache.remove(0);
         if (!is_MITE_DS_client_player || Main.isPacketThatMITEDSClientPlayerCanSendOrReceive(var2)) {
            long before = System.currentTimeMillis();
            var2.processPacket(this.myNetHandler);
            long delay = System.currentTimeMillis() - before;
            if (delay > 4L) {
               Minecraft.MITE_log.logInfo((this.myNetHandler instanceof NetClientHandler ? "[Client]" : "[Server]") + " Long time processing packet (delay=" + delay + "ms, packet id=" + var2.getPacketId() + ")");
            }
         }
      }

      if (this.readPacketCache.size() > var1) {
         this.field_98214_c.logWarning("Memory connection overburdened; after processing 2500 packets, we still have " + this.readPacketCache.size() + " to go!");
      }

      if (this.shuttingDown && this.readPacketCache.isEmpty()) {
         this.myNetHandler.handleErrorMessage(this.shutdownReason, this.field_74439_g);
      }

   }

   public SocketAddress getSocketAddress() {
      return mySocketAddress;
   }

   public void serverShutdown() {
      this.shuttingDown = true;
   }

   public void networkShutdown(String par1Str, Object... par2ArrayOfObj) {
      this.shuttingDown = true;
      this.shutdownReason = par1Str;
      this.field_74439_g = par2ArrayOfObj;
   }

   public int packetSize() {
      return 0;
   }

   public void pairWith(MemoryConnection par1MemoryConnection) {
      this.pairedConnection = par1MemoryConnection;
      par1MemoryConnection.pairedConnection = this;
   }

   public boolean isGamePaused() {
      return this.gamePaused;
   }

   public void setGamePaused(boolean par1) {
      this.gamePaused = par1;
   }

   public MemoryConnection getPairedConnection() {
      return this.pairedConnection;
   }

   public void processOrCachePacket(Packet par1Packet) {
      if (!Main.isPacketIgnored(this.myNetHandler, par1Packet)) {
         if (par1Packet.canProcessAsync() && this.myNetHandler.canProcessPacketsAsync()) {
            par1Packet.processPacket(this.myNetHandler);
         } else {
            this.readPacketCache.add(par1Packet);
         }

      }
   }

   public int clearReceivedPackets() {
      int num_packets = this.readPacketCache.size();
      this.readPacketCache.clear();
      return num_packets;
   }
}
