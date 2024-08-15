package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatList;

public final class WorldAchievement {
   public String username;
   public Achievement achievement;
   public int day;

   public WorldAchievement(Achievement achievement, String username, int day) {
      this.achievement = achievement;
      this.username = username;
      this.day = day;
   }

   public WorldAchievement(int stat_id, String username, int day) {
      this((Achievement)StatList.getStat(stat_id), username, day);
   }

   public WorldAchievement(NBTTagCompound nbt) {
      this(nbt.getInteger("stat_id"), nbt.getString("username"), nbt.getInteger("day"));
   }

   public NBTTagCompound getAsNBTTagCompound() {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setString("username", this.username);
      nbt.setInteger("stat_id", this.achievement.statId);
      nbt.setInteger("day", this.day);
      return nbt;
   }
}
