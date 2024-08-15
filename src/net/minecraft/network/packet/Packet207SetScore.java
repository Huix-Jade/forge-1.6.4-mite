package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.scoreboard.Score;

public class Packet207SetScore extends Packet {
   public String itemName = "";
   public String scoreName = "";
   public int value;
   public int updateOrRemove;

   public Packet207SetScore() {
   }

   public Packet207SetScore(Score var1, int var2) {
      this.itemName = var1.getPlayerName();
      this.scoreName = var1.func_96645_d().getName();
      this.value = var1.getScorePoints();
      this.updateOrRemove = var2;
   }

   public Packet207SetScore(String var1) {
      this.itemName = var1;
      this.scoreName = "";
      this.value = 0;
      this.updateOrRemove = 1;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.itemName = readString(var1, 16);
      this.updateOrRemove = var1.readByte();
      if (this.updateOrRemove != 1) {
         this.scoreName = readString(var1, 16);
         this.value = var1.readInt();
      }

   }

   public void writePacketData(DataOutput var1) throws IOException {
      writeString(this.itemName, var1);
      var1.writeByte(this.updateOrRemove);
      if (this.updateOrRemove != 1) {
         writeString(this.scoreName, var1);
         var1.writeInt(this.value);
      }

   }

   public void processPacket(NetHandler var1) {
      var1.handleSetScore(this);
   }

   public int getPacketSize() {
      return 2 + (this.itemName == null ? 0 : this.itemName.length()) + 2 + (this.scoreName == null ? 0 : this.scoreName.length()) + 4 + 1;
   }
}
