package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatList;

public class Packet93WorldAchievement extends Packet {
   public Achievement achievement;
   public String username;
   public int day;

   public Packet93WorldAchievement() {
   }

   public Packet93WorldAchievement(Achievement achievement, String username, int day) {
      this.achievement = achievement;
      this.username = username;
      this.day = day;
   }

   public Packet93WorldAchievement(Achievement achievement, EntityPlayer player) {
      this(achievement, player.username, player.worldObj.getDayOfWorld());
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.achievement = (Achievement)StatList.getStat(par1DataInput.readInt());
      this.username = readString(par1DataInput, 16);
      this.day = par1DataInput.readInt();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.achievement.statId);
      writeString(this.username, par1DataOutput);
      par1DataOutput.writeInt(this.day);
   }

   public void processPacket(NetHandler net_handler) {
      net_handler.handleWorldAchievement(this);
   }

   public int getPacketSize() {
      return 4 + 2 * this.username.length() + 4;
   }
}
