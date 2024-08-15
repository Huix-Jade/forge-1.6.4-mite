package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.RightClickFilter;
import net.minecraft.util.AxisAlignedBB;

public class Packet81RightClick extends Packet {
   public int slot_index;
   public int item_id;
   public int entity_id;
   public double pos_x;
   public double pos_y;
   public double pos_z;
   public float rotation_yaw;
   public float rotation_pitch;
   private boolean prev_values_are_identical;
   public double prev_pos_x;
   public double prev_pos_y;
   public double prev_pos_z;
   public float prev_rotation_yaw;
   public float prev_rotation_pitch;
   public float y_size;
   public AxisAlignedBB bb;
   public float partial_tick;
   public boolean ctrl_is_down;
   public RightClickFilter filter;

   public Packet81RightClick() {
   }

   public Packet81RightClick(EntityPlayer player, float partial_tick, RightClickFilter filter) {
      if (filter.allowsEntityInteraction()) {
         Minecraft.setErrorMessage("Packet81RightClick: this constructor doesn't allow entity interaction");
      }

      this.slot_index = player.inventory.currentItem;
      this.item_id = player.getHeldItemID();
      this.pos_x = player.posX;
      this.pos_y = player.posY - (double)EntityPlayer.y_offset_on_client_and_eye_height_on_server;
      this.pos_z = player.posZ;
      this.rotation_yaw = player.rotationYaw;
      this.rotation_pitch = player.rotationPitch;
      this.prev_pos_x = player.prevPosX;
      this.prev_pos_y = player.prevPosY - (double)EntityPlayer.y_offset_on_client_and_eye_height_on_server;
      this.prev_pos_z = player.prevPosZ;
      this.prev_rotation_yaw = player.prevRotationYaw;
      this.prev_rotation_pitch = player.prevRotationPitch;
      this.prev_values_are_identical = this.prev_pos_x == this.pos_x && this.prev_pos_y == this.pos_y && this.prev_pos_z == this.pos_z && this.prev_rotation_yaw == this.rotation_yaw && this.prev_rotation_pitch == this.rotation_pitch;
      this.y_size = player.ySize;
      this.bb = new AxisAlignedBB(player.boundingBox);
      this.partial_tick = partial_tick;
      this.ctrl_is_down = GuiScreen.isCtrlKeyDown();
      this.filter = filter;
      if (player.getFootPosY() != player.posY - (double)EntityPlayer.y_offset_on_client_and_eye_height_on_server) {
         Minecraft.setErrorMessage("Packet81RightClick: y offset discrepency detected");
      }

   }

   public Packet81RightClick(EntityPlayer player, Entity entity) {
      this.slot_index = player.inventory.currentItem;
      this.item_id = player.getHeldItemID();
      this.entity_id = entity.entityId;
      this.ctrl_is_down = GuiScreen.isCtrlKeyDown();
      this.filter = (new RightClickFilter()).setExclusive(2);
   }

   public boolean requiresRaycasting() {
      return !this.filter.allowsEntityInteractionOnly() && !this.filter.allowsIngestionOnly();
   }

   public boolean prevValuesAreIdentical() {
      return this.prev_values_are_identical;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.slot_index = par1DataInput.readByte();
      this.item_id = par1DataInput.readShort();
      this.ctrl_is_down = par1DataInput.readBoolean();
      this.filter = new RightClickFilter(par1DataInput.readByte());
      if (this.filter.allowsEntityInteractionOnly()) {
         this.entity_id = par1DataInput.readInt();
      } else if (this.requiresRaycasting()) {
         this.pos_x = par1DataInput.readDouble();
         this.pos_y = par1DataInput.readDouble();
         this.pos_z = par1DataInput.readDouble();
         this.rotation_yaw = par1DataInput.readFloat();
         this.rotation_pitch = par1DataInput.readFloat();
         this.prev_values_are_identical = par1DataInput.readBoolean();
         if (this.prev_values_are_identical) {
            this.prev_pos_x = this.pos_x;
            this.prev_pos_y = this.pos_y;
            this.prev_pos_z = this.pos_z;
            this.prev_rotation_yaw = this.rotation_yaw;
            this.prev_rotation_pitch = this.rotation_pitch;
         } else {
            this.prev_pos_x = par1DataInput.readDouble();
            this.prev_pos_y = par1DataInput.readDouble();
            this.prev_pos_z = par1DataInput.readDouble();
            this.prev_rotation_yaw = par1DataInput.readFloat();
            this.prev_rotation_pitch = par1DataInput.readFloat();
         }

         this.y_size = par1DataInput.readFloat();
         this.bb = new AxisAlignedBB(par1DataInput.readDouble(), par1DataInput.readDouble(), par1DataInput.readDouble(), par1DataInput.readDouble(), par1DataInput.readDouble(), par1DataInput.readDouble());
         this.partial_tick = par1DataInput.readFloat();
      }
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeByte(this.slot_index);
      par1DataOutput.writeShort(this.item_id);
      par1DataOutput.writeBoolean(this.ctrl_is_down);
      par1DataOutput.writeByte(this.filter.getAllowedActions());
      if (this.filter.allowsEntityInteractionOnly()) {
         par1DataOutput.writeInt(this.entity_id);
      } else if (this.requiresRaycasting()) {
         par1DataOutput.writeDouble(this.pos_x);
         par1DataOutput.writeDouble(this.pos_y);
         par1DataOutput.writeDouble(this.pos_z);
         par1DataOutput.writeFloat(this.rotation_yaw);
         par1DataOutput.writeFloat(this.rotation_pitch);
         par1DataOutput.writeBoolean(this.prev_values_are_identical);
         if (!this.prevValuesAreIdentical()) {
            par1DataOutput.writeDouble(this.prev_pos_x);
            par1DataOutput.writeDouble(this.prev_pos_y);
            par1DataOutput.writeDouble(this.prev_pos_z);
            par1DataOutput.writeFloat(this.prev_rotation_yaw);
            par1DataOutput.writeFloat(this.prev_rotation_pitch);
         }

         par1DataOutput.writeFloat(this.y_size);
         par1DataOutput.writeDouble(this.bb.minX);
         par1DataOutput.writeDouble(this.bb.minY);
         par1DataOutput.writeDouble(this.bb.minZ);
         par1DataOutput.writeDouble(this.bb.maxX);
         par1DataOutput.writeDouble(this.bb.maxY);
         par1DataOutput.writeDouble(this.bb.maxZ);
         par1DataOutput.writeFloat(this.partial_tick);
      }
   }

   public void processPacket(NetHandler net_handler) {
      net_handler.handleRightClick(this);
   }

   public int getPacketSize() {
      if (this.filter.allowsEntityInteractionOnly()) {
         return 9;
      } else if (this.requiresRaycasting()) {
         return this.prevValuesAreIdentical() ? 94 : 126;
      } else {
         return 5;
      }
   }
}
