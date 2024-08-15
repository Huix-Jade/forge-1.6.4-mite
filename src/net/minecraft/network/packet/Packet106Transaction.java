package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet106Transaction extends Packet {
   public int windowId;
   public short shortWindowId;
   public boolean accepted;

   public Packet106Transaction() {
   }

   public Packet106Transaction(int var1, short var2, boolean var3) {
      this.windowId = var1;
      this.shortWindowId = var2;
      this.accepted = var3;
   }

   public void processPacket(NetHandler var1) {
      var1.handleTransaction(this);
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.windowId = var1.readByte();
      this.shortWindowId = var1.readShort();
      this.accepted = var1.readByte() != 0;
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeByte(this.windowId);
      var1.writeShort(this.shortWindowId);
      var1.writeByte(this.accepted ? 1 : 0);
   }

   public int getPacketSize() {
      return 4;
   }
}
