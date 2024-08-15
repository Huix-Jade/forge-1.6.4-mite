package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.SpatialScaler;

public class Packet23VehicleSpawn extends Packet {
   public int entityId;
   public int scaled_pos_x;
   public int scaled_pos_y;
   public int scaled_pos_z;
   public int scaled_pitch;
   public int scaled_yaw;
   public int type;
   public int throwerEntityId;
   public int arrow_item_id;
   public boolean launcher_was_enchanted;
   public boolean arrow_stuck_in_block;
   public int arrow_tile_x;
   public int arrow_tile_y;
   public int arrow_tile_z;
   public int arrow_in_tile;
   public int arrow_in_data;
   public double exact_pos_x;
   public double exact_pos_y;
   public double exact_pos_z;
   public double exact_motion_x;
   public double exact_motion_y;
   public double exact_motion_z;
   public float approx_motion_x;
   public float approx_motion_y;
   public float approx_motion_z;
   public int unscaled_pos_x;
   public int unscaled_pos_y;
   public int unscaled_pos_z;
   public boolean position_set_using_unscaled_integers;

   public Packet23VehicleSpawn() {
      this.arrow_item_id = -1;
   }

   public Packet23VehicleSpawn(Entity par1Entity, int par2) {
      this(par1Entity, par2, 0);
   }

   public Packet23VehicleSpawn(Entity entity, int type, int thrower_entity_id) {
      this.arrow_item_id = -1;
      this.entityId = entity.entityId;
      this.scaled_pos_x = SpatialScaler.getScaledPosX(entity);
      this.scaled_pos_y = SpatialScaler.getScaledPosY(entity);
      this.scaled_pos_z = SpatialScaler.getScaledPosZ(entity);
      this.scaled_yaw = (byte)SpatialScaler.getScaledRotation(entity.rotationYaw);
      this.scaled_pitch = (byte)SpatialScaler.getScaledRotation(entity.rotationPitch);
      this.type = type;
      this.throwerEntityId = thrower_entity_id;
      if (entity instanceof EntityArrow && thrower_entity_id < 1) {
         Minecraft.setErrorMessage("WARNING: motion not sent for arrow!");
      }

      if (thrower_entity_id > 0) {
         this.approx_motion_x = (float)entity.motionX;
         this.approx_motion_y = (float)entity.motionY;
         this.approx_motion_z = (float)entity.motionZ;
      }

      if (entity instanceof EntityArrow) {
         EntityArrow arrow = (EntityArrow)entity;
         this.arrow_tile_x = arrow.xTile;
         this.arrow_tile_y = arrow.yTile;
         this.arrow_tile_z = arrow.zTile;
         this.arrow_in_tile = arrow.getInTile();
         this.arrow_in_data = arrow.getInData();
         this.exact_pos_x = arrow.posX;
         this.exact_pos_y = arrow.posY;
         this.exact_pos_z = arrow.posZ;
         this.exact_motion_x = arrow.motionX;
         this.exact_motion_y = arrow.motionY;
         this.exact_motion_z = arrow.motionZ;
      }

   }

   public Packet23VehicleSpawn setUnscaledPositionWithIntegers(int x, int y, int z) {
      this.unscaled_pos_x = x;
      this.unscaled_pos_y = y;
      this.unscaled_pos_z = z;
      this.position_set_using_unscaled_integers = true;
      return this;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entityId = par1DataInput.readInt();
      this.type = par1DataInput.readByte();
      this.position_set_using_unscaled_integers = par1DataInput.readBoolean();
      if (this.position_set_using_unscaled_integers) {
         this.unscaled_pos_x = par1DataInput.readInt();
         this.unscaled_pos_y = par1DataInput.readInt();
         this.unscaled_pos_z = par1DataInput.readInt();
      } else {
         this.scaled_pos_x = par1DataInput.readInt();
         this.scaled_pos_y = par1DataInput.readInt();
         this.scaled_pos_z = par1DataInput.readInt();
      }

      this.scaled_pitch = par1DataInput.readByte();
      this.scaled_yaw = par1DataInput.readByte();
      this.throwerEntityId = par1DataInput.readInt();
      if (this.throwerEntityId > 0) {
         if (this.type == 60) {
            this.arrow_item_id = par1DataInput.readShort();
            this.launcher_was_enchanted = par1DataInput.readBoolean();
            this.arrow_stuck_in_block = par1DataInput.readBoolean();
            this.arrow_tile_x = par1DataInput.readInt();
            this.arrow_tile_y = par1DataInput.readInt();
            this.arrow_tile_z = par1DataInput.readInt();
            this.arrow_in_tile = par1DataInput.readUnsignedByte();
            this.arrow_in_data = par1DataInput.readUnsignedByte();
            this.exact_pos_x = par1DataInput.readDouble();
            this.exact_pos_y = par1DataInput.readDouble();
            this.exact_pos_z = par1DataInput.readDouble();
            this.exact_motion_x = par1DataInput.readDouble();
            this.exact_motion_y = par1DataInput.readDouble();
            this.exact_motion_z = par1DataInput.readDouble();
            this.approx_motion_x = (float)this.exact_motion_x;
            this.approx_motion_y = (float)this.exact_motion_y;
            this.approx_motion_z = (float)this.exact_motion_z;
         } else {
            this.approx_motion_x = par1DataInput.readFloat();
            this.approx_motion_y = par1DataInput.readFloat();
            this.approx_motion_z = par1DataInput.readFloat();
         }
      }

   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entityId);
      par1DataOutput.writeByte(this.type);
      par1DataOutput.writeBoolean(this.position_set_using_unscaled_integers);
      if (this.position_set_using_unscaled_integers) {
         par1DataOutput.writeInt(this.unscaled_pos_x);
         par1DataOutput.writeInt(this.unscaled_pos_y);
         par1DataOutput.writeInt(this.unscaled_pos_z);
      } else {
         par1DataOutput.writeInt(this.scaled_pos_x);
         par1DataOutput.writeInt(this.scaled_pos_y);
         par1DataOutput.writeInt(this.scaled_pos_z);
      }

      par1DataOutput.writeByte(this.scaled_pitch);
      par1DataOutput.writeByte(this.scaled_yaw);
      par1DataOutput.writeInt(this.throwerEntityId);
      if (this.throwerEntityId > 0) {
         if (this.type == 60) {
            par1DataOutput.writeShort(this.arrow_item_id);
            par1DataOutput.writeBoolean(this.launcher_was_enchanted);
            par1DataOutput.writeBoolean(this.arrow_stuck_in_block);
            par1DataOutput.writeInt(this.arrow_tile_x);
            par1DataOutput.writeInt(this.arrow_tile_y);
            par1DataOutput.writeInt(this.arrow_tile_z);
            par1DataOutput.writeByte(this.arrow_in_tile);
            par1DataOutput.writeByte(this.arrow_in_data);
            par1DataOutput.writeDouble(this.exact_pos_x);
            par1DataOutput.writeDouble(this.exact_pos_y);
            par1DataOutput.writeDouble(this.exact_pos_z);
            par1DataOutput.writeDouble(this.exact_motion_x);
            par1DataOutput.writeDouble(this.exact_motion_y);
            par1DataOutput.writeDouble(this.exact_motion_z);
         } else {
            par1DataOutput.writeFloat(this.approx_motion_x);
            par1DataOutput.writeFloat(this.approx_motion_y);
            par1DataOutput.writeFloat(this.approx_motion_z);
         }
      }

   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleVehicleSpawn(this);
   }

   public int getPacketSize() {
      int size = 24;
      if (this.type == 60) {
         size += 66;
      } else if (this.throwerEntityId > 0) {
         size += 12;
      }

      return size;
   }
}
