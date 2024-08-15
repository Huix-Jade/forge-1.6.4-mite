package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.Entity;

public class Packet86EntityTeleportWithMotion extends Packet34EntityTeleport {
   float motion_x;
   float motion_y;
   float motion_z;

   public Packet86EntityTeleportWithMotion() {
   }

   public Packet86EntityTeleportWithMotion(Entity entity) {
      super(entity);
      this.motion_x = (float)entity.motionX;
      this.motion_y = (float)entity.motionY;
      this.motion_z = (float)entity.motionZ;
   }

   public void applyToEntity(Entity entity) {
      super.applyToEntity(entity);
      entity.motionX = (double)this.motion_x;
      entity.motionY = (double)this.motion_y;
      entity.motionZ = (double)this.motion_z;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      super.readPacketData(par1DataInput);
      this.motion_x = par1DataInput.readFloat();
      this.motion_y = par1DataInput.readFloat();
      this.motion_z = par1DataInput.readFloat();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      super.writePacketData(par1DataOutput);
      par1DataOutput.writeFloat(this.motion_x);
      par1DataOutput.writeFloat(this.motion_y);
      par1DataOutput.writeFloat(this.motion_z);
   }

   public int getPacketSize() {
      return super.getPacketSize() + 12;
   }
}
