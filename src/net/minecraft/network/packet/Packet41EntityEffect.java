package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.potion.PotionEffect;

public class Packet41EntityEffect extends Packet {
   public int entityId;
   public byte effectId;
   public byte effectAmplifier;
   public short duration;

   public Packet41EntityEffect() {
   }

   public Packet41EntityEffect(int var1, PotionEffect var2) {
      this.entityId = var1;
      this.effectId = (byte)(var2.getPotionID() & 255);
      this.effectAmplifier = (byte)(var2.getAmplifier() & 255);
      if (var2.getDuration() > 32767) {
         this.duration = 32767;
      } else {
         this.duration = (short)var2.getDuration();
      }

   }

   public void readPacketData(DataInput var1) throws IOException {
      this.entityId = var1.readInt();
      this.effectId = var1.readByte();
      this.effectAmplifier = var1.readByte();
      this.duration = var1.readShort();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.entityId);
      var1.writeByte(this.effectId);
      var1.writeByte(this.effectAmplifier);
      var1.writeShort(this.duration);
   }

   public boolean isDurationMax() {
      return this.duration == 32767;
   }

   public void processPacket(NetHandler var1) {
      var1.handleEntityEffect(this);
   }

   public int getPacketSize() {
      return 8;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet var1) {
      Packet41EntityEffect var2 = (Packet41EntityEffect)var1;
      return var2.entityId == this.entityId && var2.effectId == this.effectId;
   }
}
