package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet35EntityHeadRotation extends Packet {
   public int entityId;
   public byte headRotationYaw;

   public Packet35EntityHeadRotation() {
   }

   public Packet35EntityHeadRotation(int var1, byte var2) {
      this.entityId = var1;
      this.headRotationYaw = var2;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.entityId = var1.readInt();
      this.headRotationYaw = var1.readByte();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.entityId);
      var1.writeByte(this.headRotationYaw);
   }

   public void processPacket(NetHandler var1) {
      var1.handleEntityHeadRotation(this);
   }

   public int getPacketSize() {
      return 5;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet var1) {
      Packet35EntityHeadRotation var2 = (Packet35EntityHeadRotation)var1;
      return var2.entityId == this.entityId;
   }

   public boolean canProcessAsync() {
      return true;
   }
}
