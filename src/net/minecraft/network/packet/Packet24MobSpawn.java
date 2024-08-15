package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.SpatialScaler;

public class Packet24MobSpawn extends Packet {
   public int entity_id;
   public int type;
   public int scaled_pos_x;
   public int scaled_pos_y;
   public int scaled_pos_z;
   public int scaled_motion_x;
   public int scaled_motion_y;
   public int scaled_motion_z;
   public byte scaled_yaw;
   public byte scaled_pitch;
   public byte scaled_head_yaw;
   private DataWatcher metaData;
   private List metadata;
   public boolean is_decoy;

   public Packet24MobSpawn() {
   }

   public Packet24MobSpawn(EntityLiving par1EntityLivingBase) {
      this.entity_id = par1EntityLivingBase.entityId;
      this.type = (short)EntityList.getEntityID((Entity)par1EntityLivingBase);
      this.scaled_pos_x = SpatialScaler.getScaledPosX(par1EntityLivingBase);
      this.scaled_pos_y = SpatialScaler.getScaledPosY(par1EntityLivingBase);
      this.scaled_pos_z = SpatialScaler.getScaledPosZ(par1EntityLivingBase);
      this.scaled_motion_x = SpatialScaler.getScaledMotion(par1EntityLivingBase.motionX);
      this.scaled_motion_y = SpatialScaler.getScaledMotion(par1EntityLivingBase.motionY);
      this.scaled_motion_z = SpatialScaler.getScaledMotion(par1EntityLivingBase.motionZ);
      this.scaled_yaw = (byte)SpatialScaler.getScaledRotation(par1EntityLivingBase.rotationYaw);
      this.scaled_pitch = (byte)SpatialScaler.getScaledRotation(par1EntityLivingBase.rotationPitch);
      this.scaled_head_yaw = (byte)SpatialScaler.getScaledRotation(par1EntityLivingBase.getRotationYawHead());
      this.metaData = par1EntityLivingBase.getDataWatcher();
      this.is_decoy = par1EntityLivingBase.isDecoy();
      par1EntityLivingBase.onSendToClient(this);
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entity_id = par1DataInput.readInt();
      this.type = par1DataInput.readShort() & '\uffff';
      this.scaled_pos_x = par1DataInput.readInt();
      this.scaled_pos_y = par1DataInput.readInt();
      this.scaled_pos_z = par1DataInput.readInt();
      this.scaled_yaw = par1DataInput.readByte();
      this.scaled_pitch = par1DataInput.readByte();
      this.scaled_head_yaw = par1DataInput.readByte();
      this.scaled_motion_x = par1DataInput.readShort();
      this.scaled_motion_y = par1DataInput.readShort();
      this.scaled_motion_z = par1DataInput.readShort();
      this.metadata = DataWatcher.readWatchableObjects(par1DataInput);
      this.is_decoy = par1DataInput.readBoolean();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entity_id);
      par1DataOutput.writeShort(this.type & '\uffff');
      par1DataOutput.writeInt(this.scaled_pos_x);
      par1DataOutput.writeInt(this.scaled_pos_y);
      par1DataOutput.writeInt(this.scaled_pos_z);
      par1DataOutput.writeByte(this.scaled_yaw);
      par1DataOutput.writeByte(this.scaled_pitch);
      par1DataOutput.writeByte(this.scaled_head_yaw);
      par1DataOutput.writeShort(this.scaled_motion_x);
      par1DataOutput.writeShort(this.scaled_motion_y);
      par1DataOutput.writeShort(this.scaled_motion_z);
      this.metaData.writeWatchableObjects(par1DataOutput);
      par1DataOutput.writeBoolean(this.is_decoy);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleMobSpawn(this);
   }

   private int getPacketSizeOfWatchableObjects() {
      if (this.metaData != null) {
         return this.metaData.getPacketSizeOfWatchableObjects();
      } else if (this.metadata != null) {
         return DataWatcher.getPacketSizeOfObjectsInListToStream(this.metadata);
      } else {
         Minecraft.setErrorMessage("getPacketSizeOfWatchableObjects: both metadata and metadataWatchableObjects are null " + this);
         return 0;
      }
   }

   public int getPacketSize() {
      return 28 + this.getPacketSizeOfWatchableObjects();
   }

   public List getMetadata() {
      if (this.metadata == null) {
         this.metadata = this.metaData.getAllWatched();
      }

      return this.metadata;
   }
}
