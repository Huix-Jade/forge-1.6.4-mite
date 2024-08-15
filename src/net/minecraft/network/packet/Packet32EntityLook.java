package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.util.SpatialScaler;

public class Packet32EntityLook extends Packet {
   public int entity_id;
   public byte scaled_yaw;
   public byte scaled_pitch;

   public Packet32EntityLook() {
   }

   public Packet32EntityLook(Entity entity) {
      this.entity_id = entity.entityId;
      this.scaled_yaw = (byte)SpatialScaler.getScaledRotation(entity.rotationYaw);
      this.scaled_pitch = (byte)SpatialScaler.getScaledRotation(entity.rotationPitch);
   }

   public void applyToEntity(Entity entity) {
      entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ, SpatialScaler.getRotation(this.scaled_yaw), SpatialScaler.getRotation(this.scaled_pitch), 3);
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entity_id = par1DataInput.readInt();
      this.scaled_yaw = par1DataInput.readByte();
      this.scaled_pitch = par1DataInput.readByte();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entity_id);
      par1DataOutput.writeByte(this.scaled_yaw);
      par1DataOutput.writeByte(this.scaled_pitch);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleEntityLook(this);
   }

   public int getPacketSize() {
      return 6;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet packet) {
      Packet32EntityLook packet32 = (Packet32EntityLook)packet;
      return packet32.entity_id == this.entity_id;
   }
}
