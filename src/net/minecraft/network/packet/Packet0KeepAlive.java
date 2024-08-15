package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet0KeepAlive extends Packet {
   public int randomId;

   public Packet0KeepAlive() {
   }

   public Packet0KeepAlive(int var1) {
      this.randomId = var1;
   }

   public void processPacket(NetHandler var1) {
      var1.handleKeepAlive(this);
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.randomId = var1.readInt();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.randomId);
   }

   public int getPacketSize() {
      return 4;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet var1) {
      return true;
   }

   public boolean canProcessAsync() {
      return true;
   }
}
