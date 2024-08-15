package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet200Statistic extends Packet {
   public int statisticId;
   public int amount;

   public Packet200Statistic() {
   }

   public Packet200Statistic(int var1, int var2) {
      this.statisticId = var1;
      this.amount = var2;
   }

   public void processPacket(NetHandler var1) {
      var1.handleStatistic(this);
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.statisticId = var1.readInt();
      this.amount = var1.readInt();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.statisticId);
      var1.writeInt(this.amount);
   }

   public int getPacketSize() {
      return 6;
   }

   public boolean canProcessAsync() {
      return true;
   }
}
