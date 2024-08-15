package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.util.StatCollector;

public class StatBase {
   public final int statId;
   private final String statName;
   public boolean isIndependent;
   public String statGuid;
   private final IStatType type;
   private static NumberFormat numberFormat;
   public static IStatType simpleStatType;
   private static DecimalFormat decimalFormat;
   public static IStatType timeStatType;
   public static IStatType distanceStatType;
   public static IStatType field_111202_k;

   public StatBase(int par1, String par2Str, IStatType par3IStatType) {
      this.statId = par1;
      this.statName = par2Str;
      this.type = par3IStatType;
   }

   public StatBase(int par1, String par2Str) {
      this(par1, par2Str, simpleStatType);
   }

   public StatBase initIndependentStat() {
      this.isIndependent = true;
      return this;
   }

   public StatBase registerStat() {
      if (StatList.oneShotStats.containsKey(this.statId)) {
         throw new RuntimeException("Duplicate stat id: \"" + ((StatBase)StatList.oneShotStats.get(this.statId)).statName + "\" and \"" + this.statName + "\" at id " + this.statId);
      } else {
         StatList.allStats.add(this);
         StatList.oneShotStats.put(this.statId, this);
         this.statGuid = AchievementMap.getGuid(this.statId);
         return this;
      }
   }

   public boolean isAchievement() {
      return false;
   }

   public final String func_75968_a(long par1) {
      return this.type.format(par1);
   }

   public String getName() {
      return this.statName;
   }

   public String toString() {
      return StatCollector.translateToLocal(this.statName);
   }

   static NumberFormat getNumberFormat() {
      return numberFormat;
   }

   static DecimalFormat getDecimalFormat() {
      return decimalFormat;
   }

   public IStatType getType() {
      return this.type;
   }

   static {
      numberFormat = NumberFormat.getIntegerInstance(Locale.US);
      simpleStatType = new StatTypeSimple();
      decimalFormat = new DecimalFormat("########0.00");
      timeStatType = new StatTypeTime();
      distanceStatType = new StatTypeDistance();
      field_111202_k = new StatTypeFloat();
   }
}
