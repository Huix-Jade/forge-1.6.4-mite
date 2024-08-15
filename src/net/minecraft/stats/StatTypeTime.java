package net.minecraft.stats;

import net.minecraft.util.StringHelper;

final class StatTypeTime implements IStatType {
   public String format(long par1) {
      long years = par1 / 630720000L;
      par1 %= 630720000L;
      long days = par1 / 1728000L;
      par1 %= 1728000L;
      long hours = par1 / 72000L;
      par1 %= 72000L;
      long minutes = par1 / 1200L;
      par1 %= 1200L;
      long seconds = par1 / 20L;
      StringBuffer sb = new StringBuffer();
      if (years > 0L) {
         sb.append(years + " y ");
      }

      if (days > 0L) {
         sb.append(days + " d ");
      }

      if (hours > 0L) {
         sb.append(hours + " h ");
      }

      if (minutes > 0L) {
         sb.append(minutes + " m ");
      }

      if (seconds > 0L) {
         sb.append(seconds + " s ");
      }

      return StringHelper.left(sb.toString(), sb.toString().length() - 1);
   }
}
