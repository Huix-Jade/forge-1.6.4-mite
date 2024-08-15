package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;

public class Packet17Sleep extends Packet {
   public int entityID;
   public int bedX;
   public int bedY;
   public int bedZ;
   public int field_73622_e;
   public int direction;
   public double pos_x_before_bed;
   public double pos_y_before_bed;
   public double pos_z_before_bed;

   public Packet17Sleep() {
   }

   public Packet17Sleep(EntityPlayer par1Entity, int par2, int par3, int par4, int par5, int direction) {
      this.field_73622_e = par2;
      this.bedX = par3;
      this.bedY = par4;
      this.bedZ = par5;
      this.entityID = par1Entity.entityId;
      this.direction = direction;
      this.pos_x_before_bed = par1Entity.pos_x_before_bed;
      this.pos_y_before_bed = par1Entity.pos_y_before_bed;
      this.pos_z_before_bed = par1Entity.pos_z_before_bed;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entityID = par1DataInput.readInt();
      this.field_73622_e = par1DataInput.readByte();
      this.bedX = par1DataInput.readInt();
      this.bedY = par1DataInput.readByte();
      this.bedZ = par1DataInput.readInt();
      this.direction = par1DataInput.readByte();
      this.pos_x_before_bed = par1DataInput.readDouble();
      this.pos_y_before_bed = par1DataInput.readDouble();
      this.pos_z_before_bed = par1DataInput.readDouble();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entityID);
      par1DataOutput.writeByte(this.field_73622_e);
      par1DataOutput.writeInt(this.bedX);
      par1DataOutput.writeByte(this.bedY);
      par1DataOutput.writeInt(this.bedZ);
      par1DataOutput.writeByte(this.direction);
      par1DataOutput.writeDouble(this.pos_x_before_bed);
      par1DataOutput.writeDouble(this.pos_y_before_bed);
      par1DataOutput.writeDouble(this.pos_z_before_bed);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleSleep(this);
   }

   public int getPacketSize() {
      return 39;
   }
}
