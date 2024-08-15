package net.minecraft.client.gui.achievement;

import java.util.Comparator;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;

class SorterStatsItem implements Comparator {
   // $FF: synthetic field
   final GuiStats statsGUI;
   // $FF: synthetic field
   final GuiSlotStatsItem slotStatsItemGUI;

   SorterStatsItem(GuiSlotStatsItem var1, GuiStats var2) {
      this.slotStatsItemGUI = var1;
      this.statsGUI = var2;
   }

   public int func_78337_a(StatCrafting var1, StatCrafting var2) {
      int var3 = var1.getItemID();
      int var4 = var2.getItemID();
      StatBase var5 = null;
      StatBase var6 = null;
      if (this.slotStatsItemGUI.field_77264_j == 0) {
         var5 = StatList.objectBreakStats[var3];
         var6 = StatList.objectBreakStats[var4];
      } else if (this.slotStatsItemGUI.field_77264_j == 1) {
         var5 = StatList.objectCraftStats[var3];
         var6 = StatList.objectCraftStats[var4];
      } else if (this.slotStatsItemGUI.field_77264_j == 2) {
         var5 = StatList.objectUseStats[var3];
         var6 = StatList.objectUseStats[var4];
      }

      if (var5 != null || var6 != null) {
         if (var5 == null) {
            return 1;
         }

         if (var6 == null) {
            return -1;
         }

         int var7 = GuiStats.getStatsFileWriter(this.slotStatsItemGUI.slotGuiStats).writeStat(var5);
         int var8 = GuiStats.getStatsFileWriter(this.slotStatsItemGUI.slotGuiStats).writeStat(var6);
         if (var7 != var8) {
            return (var7 - var8) * this.slotStatsItemGUI.field_77265_k;
         }
      }

      return var3 - var4;
   }

   // $FF: synthetic method
   public int compare(Object var1, Object var2) {
      return this.func_78337_a((StatCrafting)var1, (StatCrafting)var2);
   }
}
