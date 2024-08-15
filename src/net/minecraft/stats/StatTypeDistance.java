package net.minecraft.stats;

final class StatTypeDistance implements IStatType {
   public String format(long par1) {
      double var2 = (double)par1 / 100.0;
      double var4 = var2 / 1000.0;
      return var4 > 0.5 ? StatBase.getDecimalFormat().format(var4) + " km" : (var2 > 0.5 ? StatBase.getDecimalFormat().format(var2) + " m" : par1 + " cm");
   }
}
