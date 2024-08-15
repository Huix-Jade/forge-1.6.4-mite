package net.minecraft.mite;

import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class PlayerStatsHelper {
   public static long getValueAsByte(int id, HashMap stats) {
      StatBase stat = StatList.getStat(id);
      if (!StatList.isEitherZeroOrOne(stat)) {
         Minecraft.setErrorMessage("getValueAsByte: stat isn't supposed to have byte value");
         return 0L;
      } else {
         return stats.containsKey(id) ? (long)(Byte)stats.get(id) : 0L;
      }
   }

   public static long getValueAsInt(int id, HashMap stats) {
      StatBase stat = StatList.getStat(id);
      if (!StatList.isEitherZeroOrOne(stat) && !StatList.hasLongValue(stat)) {
         return stats.containsKey(id) ? (long)(Integer)stats.get(id) : 0L;
      } else {
         Minecraft.setErrorMessage("getValueAsInt: stat isn't supposed to have int value");
         return 0L;
      }
   }

   public static long getValueAsLong(int id, HashMap stats) {
      StatBase stat = StatList.getStat(id);
      if (!StatList.hasLongValue(stat)) {
         Minecraft.setErrorMessage("getValueAsLong: stat isn't supposed to have long value");
         return 0L;
      } else {
         return stats.containsKey(id) ? (Long)stats.get(id) : 0L;
      }
   }

   public static long getValue(int id, HashMap stats) {
      StatBase stat = StatList.getStat(id);
      if (stats.containsKey(id)) {
         if (StatList.isEitherZeroOrOne(stat)) {
            return (long)(Byte)stats.get(id);
         } else {
            return StatList.hasLongValue(stat) ? (Long)stats.get(id) : (long)(Integer)stats.get(id);
         }
      } else {
         return 0L;
      }
   }

   public static long getValueOnClientAsByte(int id) {
      return getValueAsByte(id, Minecraft.theMinecraft.thePlayer.stats);
   }

   public static long getValueOnClientAsInt(int id) {
      return getValueAsInt(id, Minecraft.theMinecraft.thePlayer.stats);
   }

   public static long getValueOnClientAsLong(int id) {
      return getValueAsLong(id, Minecraft.theMinecraft.thePlayer.stats);
   }

   public static long getValueOnClient(int id) {
      return getValue(id, Minecraft.theMinecraft.thePlayer.stats);
   }

   public static long getValueOnClient(StatBase stat) {
      return getValue(stat.statId, Minecraft.theMinecraft.thePlayer.stats);
   }

   public static boolean hasAchievementUnlocked(Achievement achievement) {
      return getValueOnClient(achievement) == 1L;
   }
}
