package net.minecraft.network;

import java.util.concurrent.Callable;
import net.minecraft.network.packet.Packet;

class CallablePacketID implements Callable {
   // $FF: synthetic field
   final Packet thePacket;
   // $FF: synthetic field
   final NetServerHandler theNetServerHandler;

   CallablePacketID(NetServerHandler var1, Packet var2) {
      this.theNetServerHandler = var1;
      this.thePacket = var2;
   }

   public String callPacketID() {
      return String.valueOf(this.thePacket.getPacketId());
   }

   // $FF: synthetic method
   public Object call() {
      return this.callPacketID();
   }
}
