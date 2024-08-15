package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.potion.PotionEffect;

public class Packet42RemoveEntityEffect extends Packet {
   public int entityId;
   public byte effectId;

   public Packet42RemoveEntityEffect() {
   }

   public Packet42RemoveEntityEffect(int var1, PotionEffect var2) {
      this.entityId = var1;
      this.effectId = (byte)(var2.getPotionID() & 255);
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.entityId = var1.readInt();
      this.effectId = var1.readByte();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.entityId);
      var1.writeByte(this.effectId);
   }

   public void processPacket(NetHandler var1) {
      var1.handleRemoveEntityEffect(this);
   }

   public int getPacketSize() {
      return 5;
   }
}
