package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.SpatialScaler;

public class Packet26EntityExpOrb extends Packet {
   public int entity_id;
   public int scaled_pos_x;
   public int scaled_pos_y;
   public int scaled_pos_z;
   public int scaled_motion_x;
   public int scaled_motion_y;
   public int scaled_motion_z;
   public int xp_value;
   public String player_this_belongs_to;

   public Packet26EntityExpOrb() {
   }

   public Packet26EntityExpOrb(EntityXPOrb par1EntityXPOrb) {
      this.entity_id = par1EntityXPOrb.entityId;
      this.scaled_pos_x = SpatialScaler.getScaledPosX(par1EntityXPOrb);
      this.scaled_pos_y = SpatialScaler.getScaledPosY(par1EntityXPOrb);
      this.scaled_pos_z = SpatialScaler.getScaledPosZ(par1EntityXPOrb);
      this.scaled_motion_x = SpatialScaler.getScaledMotion(par1EntityXPOrb.motionX);
      this.scaled_motion_y = SpatialScaler.getScaledMotion(par1EntityXPOrb.motionY);
      this.scaled_motion_z = SpatialScaler.getScaledMotion(par1EntityXPOrb.motionZ);
      this.xp_value = par1EntityXPOrb.getXpValue();
      this.player_this_belongs_to = par1EntityXPOrb.player_this_belongs_to == null ? "" : par1EntityXPOrb.player_this_belongs_to;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entity_id = par1DataInput.readInt();
      this.scaled_pos_x = par1DataInput.readInt();
      this.scaled_pos_y = par1DataInput.readInt();
      this.scaled_pos_z = par1DataInput.readInt();
      this.scaled_motion_x = par1DataInput.readShort();
      this.scaled_motion_y = par1DataInput.readShort();
      this.scaled_motion_z = par1DataInput.readShort();
      this.xp_value = par1DataInput.readShort();
      this.player_this_belongs_to = readString(par1DataInput, 32);
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entity_id);
      par1DataOutput.writeInt(this.scaled_pos_x);
      par1DataOutput.writeInt(this.scaled_pos_y);
      par1DataOutput.writeInt(this.scaled_pos_z);
      par1DataOutput.writeShort(this.scaled_motion_x);
      par1DataOutput.writeShort(this.scaled_motion_y);
      par1DataOutput.writeShort(this.scaled_motion_z);
      par1DataOutput.writeShort(this.xp_value);
      writeString(this.player_this_belongs_to, par1DataOutput);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleEntityExpOrb(this);
   }

   public int getPacketSize() {
      return 24 + this.player_this_belongs_to.length();
   }
}
