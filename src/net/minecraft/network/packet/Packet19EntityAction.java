package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.Entity;

public class Packet19EntityAction extends Packet {
   public int entityId;
   public int action;
   public int auxData;

   public Packet19EntityAction() {
   }

   public Packet19EntityAction(Entity var1, int var2) {
      this(var1, var2, 0);
   }

   public Packet19EntityAction(Entity var1, int var2, int var3) {
      this.entityId = var1.entityId;
      this.action = var2;
      this.auxData = var3;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.entityId = var1.readInt();
      this.action = var1.readByte();
      this.auxData = var1.readInt();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.entityId);
      var1.writeByte(this.action);
      var1.writeInt(this.auxData);
   }

   public void processPacket(NetHandler var1) {
      var1.handleEntityAction(this);
   }

   public int getPacketSize() {
      return 9;
   }
}
