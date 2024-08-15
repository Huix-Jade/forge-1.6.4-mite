package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;

public class Packet28EntityVelocity extends Packet {
   public int entityId;
   public int motionX;
   public int motionY;
   public int motionZ;

   public Packet28EntityVelocity() {
   }

   public Packet28EntityVelocity(EntityTrackerEntry entity_tracker_entry) {
      Entity entity = entity_tracker_entry.myEntity;
      this.entityId = entity.entityId;
      double motion_x = entity.motionX;
      double motion_y = entity.motionY;
      double motion_z = entity.motionZ;
      double var8 = 3.9;
      boolean overspeed = false;
      if (motion_x < -var8) {
         motion_x = -var8;
         overspeed = true;
      } else if (motion_x > var8) {
         motion_x = var8;
         overspeed = true;
      }

      if (motion_y < -var8) {
         motion_y = -var8;
         overspeed = true;
      } else if (motion_y > var8) {
         motion_y = var8;
         overspeed = true;
      }

      if (motion_z < -var8) {
         motion_z = -var8;
         overspeed = true;
      } else if (motion_z > var8) {
         motion_z = var8;
         overspeed = true;
      }

      this.motionX = (int)(motion_x * 8000.0);
      this.motionY = (int)(motion_y * 8000.0);
      this.motionZ = (int)(motion_z * 8000.0);
      if (!overspeed) {
         this.applyToEntity(entity);
      }

      entity_tracker_entry.motionX = entity.motionX;
      entity_tracker_entry.motionY = entity.motionY;
      entity_tracker_entry.motionZ = entity.motionZ;
   }

   public void applyToEntity(Entity entity) {
      entity.setVelocity((double)this.motionX / 8000.0, (double)this.motionY / 8000.0, (double)this.motionZ / 8000.0);
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entityId = par1DataInput.readInt();
      this.motionX = par1DataInput.readShort();
      this.motionY = par1DataInput.readShort();
      this.motionZ = par1DataInput.readShort();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entityId);
      par1DataOutput.writeShort(this.motionX);
      par1DataOutput.writeShort(this.motionY);
      par1DataOutput.writeShort(this.motionZ);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleEntityVelocity(this);
   }

   public int getPacketSize() {
      return 10;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet par1Packet) {
      Packet28EntityVelocity var2 = (Packet28EntityVelocity)par1Packet;
      return var2.entityId == this.entityId;
   }
}
