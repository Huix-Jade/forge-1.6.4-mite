package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet105UpdateProgressbar extends Packet {
   public int windowId;
   public int progressBar;
   public int progressBarValue;

   public Packet105UpdateProgressbar() {
   }

   public Packet105UpdateProgressbar(int var1, int var2, int var3) {
      this.windowId = var1;
      this.progressBar = var2;
      this.progressBarValue = var3;
   }

   public void processPacket(NetHandler var1) {
      var1.handleUpdateProgressbar(this);
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.windowId = var1.readByte();
      this.progressBar = var1.readShort();
      this.progressBarValue = var1.readShort();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeByte(this.windowId);
      var1.writeShort(this.progressBar);
      var1.writeShort(this.progressBarValue);
   }

   public int getPacketSize() {
      return 5;
   }
}
