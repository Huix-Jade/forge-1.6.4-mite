package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.SpatialScaler;

public class Packet71Weather extends Packet {
   public int entityID;
   public int scaled_pos_x;
   public int scaled_pos_y;
   public int scaled_pos_z;
   public int isLightningBolt;

   public Packet71Weather() {
   }

   public Packet71Weather(Entity par1Entity) {
      this.entityID = par1Entity.entityId;
      this.scaled_pos_x = SpatialScaler.getScaledPosX(par1Entity);
      this.scaled_pos_y = SpatialScaler.getScaledPosY(par1Entity);
      this.scaled_pos_z = SpatialScaler.getScaledPosZ(par1Entity);
      if (par1Entity instanceof EntityLightningBolt) {
         this.isLightningBolt = 1;
      }

   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entityID = par1DataInput.readInt();
      this.isLightningBolt = par1DataInput.readByte();
      this.scaled_pos_x = par1DataInput.readInt();
      this.scaled_pos_y = par1DataInput.readInt();
      this.scaled_pos_z = par1DataInput.readInt();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entityID);
      par1DataOutput.writeByte(this.isLightningBolt);
      par1DataOutput.writeInt(this.scaled_pos_x);
      par1DataOutput.writeInt(this.scaled_pos_y);
      par1DataOutput.writeInt(this.scaled_pos_z);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleWeather(this);
   }

   public int getPacketSize() {
      return 17;
   }
}
