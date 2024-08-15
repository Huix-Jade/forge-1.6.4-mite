package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class Achievement extends StatBase {
   public final int displayColumn;
   public final int displayRow;
   public final Achievement parentAchievement;
   private final String achievementDescription;
   private IStatStringFormat statStringFormatter;
   public final ItemStack theItemStack;
   private boolean isSpecial;
   private boolean is_flipped;
   private Achievement second_parent;
   private int tooltip_width;

   public Achievement(int par1, String par2Str, int par3, int par4, Item par5Item, Achievement par6Achievement) {
      this(par1, par2Str, par3, par4, new ItemStack(par5Item), par6Achievement);
   }

   public Achievement(int par1, String par2Str, int par3, int par4, Block par5Block, Achievement par6Achievement) {
      this(par1, par2Str, par3, par4, new ItemStack(par5Block), par6Achievement);
   }

   public Achievement(int par1, String par2Str, int par3, int par4, ItemStack par5ItemStack, Achievement par6Achievement) {
      super(5242880 + par1, "achievement." + par2Str);
      this.tooltip_width = 120;
      this.theItemStack = par5ItemStack;
      this.achievementDescription = "achievement." + par2Str + ".desc";
      this.displayColumn = par3;
      this.displayRow = par4;
      if (par3 < AchievementList.minDisplayColumn) {
         AchievementList.minDisplayColumn = par3;
      }

      if (par4 < AchievementList.minDisplayRow) {
         AchievementList.minDisplayRow = par4;
      }

      if (par3 > AchievementList.maxDisplayColumn) {
         AchievementList.maxDisplayColumn = par3;
      }

      if (par4 > AchievementList.maxDisplayRow) {
         AchievementList.maxDisplayRow = par4;
      }

      this.parentAchievement = par6Achievement;
   }

   public Achievement setIndependent() {
      this.isIndependent = true;
      return this;
   }

   public Achievement setSpecial() {
      this.isSpecial = true;
      return this;
   }

   public Achievement registerAchievement() {
      super.registerStat();
      AchievementList.achievementList.add(this);
      return this;
   }

   public boolean isAchievement() {
      return true;
   }

   public String getDescription() {
      return this.statStringFormatter != null ? this.statStringFormatter.formatString(StatCollector.translateToLocal(this.achievementDescription)) : StatCollector.translateToLocal(this.achievementDescription);
   }

   public Achievement setStatStringFormatter(IStatStringFormat par1IStatStringFormat) {
      this.statStringFormatter = par1IStatStringFormat;
      return this;
   }

   public boolean getSpecial() {
      return this.isSpecial;
   }

   public StatBase registerStat() {
      return this.registerAchievement();
   }

   public StatBase initIndependentStat() {
      return this.setIndependent();
   }

   public Achievement setFlipped() {
      this.is_flipped = true;
      return this;
   }

   public boolean isFlipped() {
      return this.is_flipped;
   }

   public Achievement setSecondParent(Achievement second_parent) {
      this.second_parent = second_parent;
      return this;
   }

   public Achievement getSecondParent() {
      return this.second_parent;
   }

   public boolean hasSecondParent() {
      return this.second_parent != null;
   }

   public Achievement setTooltipWidth(int tooltip_width) {
      this.tooltip_width = tooltip_width;
      return this;
   }

   public int getTooltipWidth() {
      return this.tooltip_width;
   }
}
