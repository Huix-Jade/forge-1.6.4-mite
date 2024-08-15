package net.minecraft.network;

import java.net.SocketAddress;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;

public interface INetworkManager {
   void setNetHandler(NetHandler var1);

   void addToSendQueue(Packet var1);

   void wakeThreads();

   void processReadPackets();

   int clearReceivedPackets();

   SocketAddress getSocketAddress();

   void serverShutdown();

   int packetSize();

   void networkShutdown(String var1, Object... var2);

   void closeConnections();
}
