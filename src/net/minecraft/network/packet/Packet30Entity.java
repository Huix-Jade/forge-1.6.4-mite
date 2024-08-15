package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.Entity;

public class Packet30Entity extends Packet {
   public int entityId;
   public byte xPosition;
   public byte yPosition;
   public byte zPosition;
   public byte yaw;
   public byte pitch;
   public boolean rotating;
   public boolean sync_last_tick_pos;

   public Packet30Entity() {
   }

   public Packet30Entity(Entity entity) {
      this.entityId = entity.entityId;
      this.sync_last_tick_pos = entity.sync_last_tick_pos_on_next_update;
      entity.sync_last_tick_pos_on_next_update = false;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entityId = par1DataInput.readInt();
      this.sync_last_tick_pos = par1DataInput.readBoolean();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entityId);
      par1DataOutput.writeBoolean(this.sync_last_tick_pos);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleEntity(this);
   }

   public int getPacketSize() {
      return 5;
   }

   public String toString() {
      return "Entity_" + super.toString();
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet par1Packet) {
      Packet30Entity var2 = (Packet30Entity)par1Packet;
      return var2.entityId == this.entityId;
   }
}
