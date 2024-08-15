package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet43Experience extends Packet {
   public int experience;

   public Packet43Experience() {
   }

   public Packet43Experience(int experience) {
      this.experience = experience;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.experience = par1DataInput.readInt();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.experience);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleExperience(this);
   }

   public int getPacketSize() {
      return 4;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet par1Packet) {
      return true;
   }
}
