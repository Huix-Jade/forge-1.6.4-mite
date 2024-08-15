package net.minecraft.stats;

final class StatTypeSimple implements IStatType {
   public String format(long par1) {
      return StatBase.getNumberFormat().format(par1);
   }
}
