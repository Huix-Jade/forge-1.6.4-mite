package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet22Collect extends Packet {
   public int collectedEntityId;
   public int collectorEntityId;

   public Packet22Collect() {
   }

   public Packet22Collect(int var1, int var2) {
      this.collectedEntityId = var1;
      this.collectorEntityId = var2;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.collectedEntityId = var1.readInt();
      this.collectorEntityId = var1.readInt();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.collectedEntityId);
      var1.writeInt(this.collectorEntityId);
   }

   public void processPacket(NetHandler var1) {
      var1.handleCollect(this);
   }

   public int getPacketSize() {
      return 8;
   }
}
