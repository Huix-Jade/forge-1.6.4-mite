package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SpatialScaler;

public class Packet34EntityTeleport extends Packet {
   public int entity_id;
   protected int scaled_pos_x;
   protected int scaled_pos_y;
   protected int scaled_pos_z;
   protected byte scaled_yaw;
   protected byte scaled_pitch;
   protected boolean quantum_leap;

   public Packet34EntityTeleport() {
   }

   public Packet34EntityTeleport(Entity entity) {
      this.entity_id = entity.entityId;
      this.scaled_pos_x = SpatialScaler.getScaledPosX(entity);
      this.scaled_pos_y = SpatialScaler.getScaledPosY(entity);
      this.scaled_pos_z = SpatialScaler.getScaledPosZ(entity);
      this.scaled_yaw = (byte)SpatialScaler.getScaledRotation(entity.rotationYaw);
      this.scaled_pitch = (byte)SpatialScaler.getScaledRotation(entity.rotationPitch);
      this.quantum_leap = entity.sync_last_tick_pos_on_next_update;
      entity.sync_last_tick_pos_on_next_update = false;
   }

   public void applyToEntity(Entity entity) {
      entity.setPositionAndRotation2(SpatialScaler.getPosX(this.scaled_pos_x), SpatialScaler.getPosY(this.scaled_pos_y), SpatialScaler.getPosZ(this.scaled_pos_z), SpatialScaler.getRotation(this.scaled_yaw), SpatialScaler.getRotation(this.scaled_pitch), 3);
      if (this.quantum_leap) {
         if (entity instanceof EntityLivingBase) {
            EntityLivingBase entity_living_base = (EntityLivingBase)entity;
            entity.posX = entity_living_base.newPosX;
            entity.posY = entity_living_base.newPosY;
            entity.posZ = entity_living_base.newPosZ;
         }

         entity.lastTickPosX = entity.posX;
         entity.lastTickPosY = entity.posY;
         entity.lastTickPosZ = entity.posZ;
      }

   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entity_id = par1DataInput.readInt();
      this.scaled_pos_x = par1DataInput.readInt();
      this.scaled_pos_y = par1DataInput.readShort();
      this.scaled_pos_z = par1DataInput.readInt();
      this.scaled_yaw = par1DataInput.readByte();
      this.scaled_pitch = par1DataInput.readByte();
      this.quantum_leap = par1DataInput.readBoolean();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entity_id);
      par1DataOutput.writeInt(this.scaled_pos_x);
      par1DataOutput.writeShort(this.scaled_pos_y);
      par1DataOutput.writeInt(this.scaled_pos_z);
      par1DataOutput.write(this.scaled_yaw);
      par1DataOutput.write(this.scaled_pitch);
      par1DataOutput.writeBoolean(this.quantum_leap);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleEntityTeleport(this);
   }

   public int getPacketSize() {
      return 17;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet par1Packet) {
      Packet34EntityTeleport var2 = (Packet34EntityTeleport)par1Packet;
      return var2.entity_id == this.entity_id;
   }
}
