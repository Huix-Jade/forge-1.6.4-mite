package net.minecraft.item.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumQuality;

public class CraftingResult {
   public ItemStack item_stack;
   private final float difficulty;
   public boolean is_experience_cost_exempt;
   public EnumQuality quality_override;
   public final int[] applicable_skillsets;
   public final IRecipe recipe;
   public int consumption = 1;
   private boolean is_repair;

   public CraftingResult(ItemStack item_stack, float difficulty, int[] applicable_skillsets, IRecipe recipe) {
      this.item_stack = item_stack;
      this.difficulty = difficulty;
      this.applicable_skillsets = applicable_skillsets;
      this.recipe = recipe;
   }

   public static boolean haveEquivalentItemStacks(CraftingResult first, CraftingResult second) {
      return ItemStack.areItemStacksEqual(first == null ? null : first.item_stack, second == null ? null : second.item_stack);
   }

   public float getUnmodifiedDifficulty() {
      return this.difficulty;
   }

   public float getQualityAdjustedDifficulty(EnumQuality quality) {
      if (this.quality_override != null) {
         quality = this.quality_override;
      }

      return getQualityAdjustedDifficulty(this.difficulty, quality);
   }

   public static float getQualityAdjustedDifficulty(float difficulty, EnumQuality quality) {
      if (quality == null) {
         return difficulty;
      } else {
         int quality_levels_above_average = quality.ordinal() - EnumQuality.average.ordinal();
         float modified_difficulty = difficulty;

         for(int i = 0; i < quality_levels_above_average; ++i) {
            modified_difficulty *= 2.0F;
         }

         return modified_difficulty;
      }
   }

   public CraftingResult setExperienceCostExempt() {
      this.is_experience_cost_exempt = true;
      return this;
   }

   public CraftingResult setQualityOverride(EnumQuality quality_override) {
      this.quality_override = quality_override;
      return this;
   }

   public CraftingResult setConsumption(int consumption) {
      this.consumption = consumption;
      return this;
   }

   public CraftingResult setRepair() {
      this.is_repair = true;
      return this;
   }

   public boolean isRepair() {
      return this.is_repair;
   }
}
