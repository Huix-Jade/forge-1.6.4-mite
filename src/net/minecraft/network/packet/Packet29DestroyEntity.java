package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet29DestroyEntity extends Packet {
   public int[] entityId;

   public Packet29DestroyEntity() {
   }

   public Packet29DestroyEntity(int... var1) {
      this.entityId = var1;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.entityId = new int[var1.readByte()];

      for(int var2 = 0; var2 < this.entityId.length; ++var2) {
         this.entityId[var2] = var1.readInt();
      }

   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeByte(this.entityId.length);

      for(int var2 = 0; var2 < this.entityId.length; ++var2) {
         var1.writeInt(this.entityId[var2]);
      }

   }

   public void processPacket(NetHandler var1) {
      var1.handleDestroyEntity(this);
   }

   public int getPacketSize() {
      return 1 + this.entityId.length * 4;
   }
}
