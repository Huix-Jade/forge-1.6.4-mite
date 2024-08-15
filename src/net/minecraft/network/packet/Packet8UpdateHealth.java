package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet8UpdateHealth extends Packet {
   public float healthMP;
   public int satiation;
   public int nutrition;
   public float vision_dimming;

   public Packet8UpdateHealth() {
   }

   public Packet8UpdateHealth(float health, int satiation, int nutrition, float vision_dimming) {
      this.healthMP = health;
      this.satiation = satiation;
      this.nutrition = nutrition;
      this.vision_dimming = vision_dimming;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.healthMP = par1DataInput.readFloat();
      this.satiation = par1DataInput.readByte();
      this.nutrition = par1DataInput.readByte();
      this.vision_dimming = par1DataInput.readFloat();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeFloat(this.healthMP);
      par1DataOutput.writeByte(this.satiation);
      par1DataOutput.writeByte(this.nutrition);
      par1DataOutput.writeFloat(this.vision_dimming);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleUpdateHealth(this);
   }

   public int getPacketSize() {
      return 10;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet par1Packet) {
      return true;
   }
}
