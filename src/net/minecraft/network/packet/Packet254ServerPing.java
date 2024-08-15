package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet254ServerPing extends Packet {
   private static final int field_140051_d = (new Packet250CustomPayload()).getPacketId();
   public int readSuccessfully;
   public String field_140052_b;
   public int field_140053_c;

   public Packet254ServerPing() {
   }

   public Packet254ServerPing(int var1, String var2, int var3) {
      this.readSuccessfully = var1;
      this.field_140052_b = var2;
      this.field_140053_c = var3;
   }

   public void readPacketData(DataInput var1) {
      try {
         this.readSuccessfully = var1.readByte();

         try {
            var1.readByte();
            readString(var1, 255);
            var1.readShort();
            this.readSuccessfully = var1.readByte();
            if (this.readSuccessfully >= 73) {
               this.field_140052_b = readString(var1, 255);
               this.field_140053_c = var1.readInt();
            }
         } catch (Throwable var3) {
            this.field_140052_b = "";
         }
      } catch (Throwable var4) {
         this.readSuccessfully = 0;
         this.field_140052_b = "";
      }

   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeByte(1);
      var1.writeByte(field_140051_d);
      Packet.writeString("MC|PingHost", var1);
      var1.writeShort(3 + 2 * this.field_140052_b.length() + 4);
      var1.writeByte(this.readSuccessfully);
      Packet.writeString(this.field_140052_b, var1);
      var1.writeInt(this.field_140053_c);
   }

   public void processPacket(NetHandler var1) {
      var1.handleServerPing(this);
   }

   public int getPacketSize() {
      return 3 + this.field_140052_b.length() * 2 + 4;
   }

   public boolean func_140050_d() {
      return this.readSuccessfully == 0;
   }
}
