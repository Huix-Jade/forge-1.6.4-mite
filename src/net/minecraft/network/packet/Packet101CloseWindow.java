package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet101CloseWindow extends Packet {
   public int windowId;

   public Packet101CloseWindow() {
   }

   public Packet101CloseWindow(int var1) {
      this.windowId = var1;
   }

   public void processPacket(NetHandler var1) {
      var1.handleCloseWindow(this);
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.windowId = var1.readByte();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeByte(this.windowId);
   }

   public int getPacketSize() {
      return 1;
   }
}
