package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.Entity;

public class Packet18Animation extends Packet {
   public int entityId;
   public int animate;
   public int other_entity_id;

   public Packet18Animation() {
   }

   public Packet18Animation(Entity par1Entity, int par2) {
      this(par1Entity, par2, (Entity)null);
   }

   public Packet18Animation(Entity par1Entity, int par2, Entity other_entity) {
      this.entityId = par1Entity.entityId;
      this.animate = par2;
      this.other_entity_id = other_entity == null ? -1 : other_entity.entityId;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entityId = par1DataInput.readInt();
      this.animate = par1DataInput.readByte();
      if (this.animate == 3) {
         this.other_entity_id = par1DataInput.readInt();
      }

   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entityId);
      par1DataOutput.writeByte(this.animate);
      if (this.animate == 3) {
         par1DataOutput.writeInt(this.other_entity_id);
      }

   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleAnimation(this);
   }

   public int getPacketSize() {
      return this.animate == 3 ? 9 : 5;
   }
}
