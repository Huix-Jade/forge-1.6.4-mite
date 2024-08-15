package net.minecraft.network;

import java.util.concurrent.Callable;
import net.minecraft.network.packet.Packet;

class CallablePacketClass implements Callable {
   // $FF: synthetic field
   final Packet thePacket;
   // $FF: synthetic field
   final NetServerHandler theNetServerHandler;

   CallablePacketClass(NetServerHandler var1, Packet var2) {
      this.theNetServerHandler = var1;
      this.thePacket = var2;
   }

   public String getPacketClass() {
      return this.thePacket.getClass().getCanonicalName();
   }

   // $FF: synthetic method
   public Object call() {
      return this.getPacketClass();
   }
}
