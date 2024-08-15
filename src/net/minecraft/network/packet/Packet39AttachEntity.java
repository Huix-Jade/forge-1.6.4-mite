package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.Entity;

public class Packet39AttachEntity extends Packet {
   public int attachState;
   public int ridingEntityId;
   public int vehicleEntityId;

   public Packet39AttachEntity() {
   }

   public Packet39AttachEntity(int var1, Entity var2, Entity var3) {
      this.attachState = var1;
      this.ridingEntityId = var2.entityId;
      this.vehicleEntityId = var3 != null ? var3.entityId : -1;
   }

   public int getPacketSize() {
      return 8;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.ridingEntityId = var1.readInt();
      this.vehicleEntityId = var1.readInt();
      this.attachState = var1.readUnsignedByte();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.ridingEntityId);
      var1.writeInt(this.vehicleEntityId);
      var1.writeByte(this.attachState);
   }

   public void processPacket(NetHandler var1) {
      var1.handleAttachEntity(this);
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet var1) {
      Packet39AttachEntity var2 = (Packet39AttachEntity)var1;
      return var2.ridingEntityId == this.ridingEntityId;
   }
}
