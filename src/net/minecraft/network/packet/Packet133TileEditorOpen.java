package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet133TileEditorOpen extends Packet {
   public int field_142037_a;
   public int field_142035_b;
   public int field_142036_c;
   public int field_142034_d;

   public Packet133TileEditorOpen() {
   }

   public Packet133TileEditorOpen(int var1, int var2, int var3, int var4) {
      this.field_142037_a = var1;
      this.field_142035_b = var2;
      this.field_142036_c = var3;
      this.field_142034_d = var4;
   }

   public void processPacket(NetHandler var1) {
      var1.func_142031_a(this);
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.field_142037_a = var1.readByte();
      this.field_142035_b = var1.readInt();
      this.field_142036_c = var1.readInt();
      this.field_142034_d = var1.readInt();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeByte(this.field_142037_a);
      var1.writeInt(this.field_142035_b);
      var1.writeInt(this.field_142036_c);
      var1.writeInt(this.field_142034_d);
   }

   public int getPacketSize() {
      return 13;
   }
}
