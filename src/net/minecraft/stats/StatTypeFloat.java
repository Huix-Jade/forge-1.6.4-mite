package net.minecraft.stats;

final class StatTypeFloat implements IStatType {
   public String format(long par1) {
      return StatBase.getDecimalFormat().format((double)par1 * 0.1);
   }
}
