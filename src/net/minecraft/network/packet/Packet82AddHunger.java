package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet82AddHunger extends Packet {
   public float hunger;

   public Packet82AddHunger() {
   }

   public Packet82AddHunger(float hunger) {
      this.hunger = hunger;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.hunger = par1DataInput.readFloat();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeFloat(this.hunger);
   }

   public void processPacket(NetHandler net_handler) {
      net_handler.handleAddHunger(this);
   }

   public int getPacketSize() {
      return 4;
   }
}
