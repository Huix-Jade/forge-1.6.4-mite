package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet54PlayNoteBlock extends Packet {
   public int xLocation;
   public int yLocation;
   public int zLocation;
   public int instrumentType;
   public int pitch;
   public int blockId;

   public Packet54PlayNoteBlock() {
   }

   public Packet54PlayNoteBlock(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.xLocation = var1;
      this.yLocation = var2;
      this.zLocation = var3;
      this.instrumentType = var5;
      this.pitch = var6;
      this.blockId = var4;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.xLocation = var1.readInt();
      this.yLocation = var1.readShort();
      this.zLocation = var1.readInt();
      this.instrumentType = var1.readUnsignedByte();
      this.pitch = var1.readUnsignedByte();
      this.blockId = var1.readShort() & 4095;
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.xLocation);
      var1.writeShort(this.yLocation);
      var1.writeInt(this.zLocation);
      var1.write(this.instrumentType);
      var1.write(this.pitch);
      var1.writeShort(this.blockId & 4095);
   }

   public void processPacket(NetHandler var1) {
      var1.handleBlockEvent(this);
   }

   public int getPacketSize() {
      return 14;
   }
}
