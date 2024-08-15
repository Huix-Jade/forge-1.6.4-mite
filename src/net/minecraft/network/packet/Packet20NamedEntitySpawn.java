package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SpatialScaler;

public class Packet20NamedEntitySpawn extends Packet {
   public int entityId;
   public String name;
   public int scaled_pos_x;
   public int scaled_pos_y;
   public int scaled_pos_z;
   public byte scaled_yaw;
   public byte scaled_pitch;
   public int currentItem;
   private DataWatcher metadata;
   private List metadataWatchableObjects;

   public Packet20NamedEntitySpawn() {
   }

   public Packet20NamedEntitySpawn(EntityPlayer par1EntityPlayer) {
      this.entityId = par1EntityPlayer.entityId;
      this.name = par1EntityPlayer.getCommandSenderName();
      this.scaled_pos_x = SpatialScaler.getScaledPosX(par1EntityPlayer);
      this.scaled_pos_y = SpatialScaler.getScaledPosY(par1EntityPlayer);
      this.scaled_pos_z = SpatialScaler.getScaledPosZ(par1EntityPlayer);
      this.scaled_yaw = (byte)SpatialScaler.getScaledRotation(par1EntityPlayer.rotationYaw);
      this.scaled_pitch = (byte)SpatialScaler.getScaledRotation(par1EntityPlayer.rotationPitch);
      ItemStack var2 = par1EntityPlayer.inventory.getCurrentItemStack();
      this.currentItem = var2 == null ? 0 : var2.itemID;
      this.metadata = par1EntityPlayer.getDataWatcher();
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entityId = par1DataInput.readInt();
      this.name = readString(par1DataInput, 16);
      this.scaled_pos_x = par1DataInput.readInt();
      this.scaled_pos_y = par1DataInput.readInt();
      this.scaled_pos_z = par1DataInput.readInt();
      this.scaled_yaw = par1DataInput.readByte();
      this.scaled_pitch = par1DataInput.readByte();
      this.currentItem = par1DataInput.readShort();
      this.metadataWatchableObjects = DataWatcher.readWatchableObjects(par1DataInput);
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entityId);
      writeString(this.name, par1DataOutput);
      par1DataOutput.writeInt(this.scaled_pos_x);
      par1DataOutput.writeInt(this.scaled_pos_y);
      par1DataOutput.writeInt(this.scaled_pos_z);
      par1DataOutput.writeByte(this.scaled_yaw);
      par1DataOutput.writeByte(this.scaled_pitch);
      par1DataOutput.writeShort(this.currentItem);
      this.metadata.writeWatchableObjects(par1DataOutput);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleNamedEntitySpawn(this);
   }

   private int getPacketSizeOfWatchableObjects() {
      if (this.metadata != null) {
         return this.metadata.getPacketSizeOfWatchableObjects();
      } else if (this.metadataWatchableObjects != null) {
         return DataWatcher.getPacketSizeOfObjectsInListToStream(this.metadataWatchableObjects);
      } else {
         Minecraft.setErrorMessage("getPacketSizeOfWatchableObjects: both metadata and metadataWatchableObjects are null " + this);
         return 0;
      }
   }

   public int getPacketSize() {
      return 4 + Packet.getPacketSizeOfString(this.name) + 16 + this.getPacketSizeOfWatchableObjects();
   }

   public List getWatchedMetadata() {
      if (this.metadataWatchableObjects == null) {
         this.metadataWatchableObjects = this.metadata.getAllWatched();
      }

      return this.metadataWatchableObjects;
   }
}
