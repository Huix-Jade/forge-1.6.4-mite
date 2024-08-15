package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet16BlockItemSwitch extends Packet {
   public int id;

   public Packet16BlockItemSwitch() {
   }

   public Packet16BlockItemSwitch(int var1) {
      this.id = var1;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.id = var1.readShort();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeShort(this.id);
   }

   public void processPacket(NetHandler var1) {
      var1.handleBlockItemSwitch(this);
   }

   public int getPacketSize() {
      return 2;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet var1) {
      return true;
   }
}
