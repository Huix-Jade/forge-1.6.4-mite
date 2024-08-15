package net.minecraft.item.crafting;

import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IRecipe {
   boolean matches(InventoryCrafting var1, World var2);

   CraftingResult getCraftingResult(InventoryCrafting var1);

   int getRecipeSize();

   ItemStack getRecipeOutput();

   ItemStack[] getComponents();

   IRecipe setDifficulty(float var1);

   IRecipe scaleDifficulty(float var1);

   float getUnmodifiedDifficulty();

   void setIncludeInLowestCraftingDifficultyDetermination();

   boolean getIncludeInLowestCraftingDifficultyDetermination();

   void setSkillsets(int[] var1);

   void setSkillset(int var1);

   int[] getSkillsets();

   void setMaterialToCheckToolBenchHardnessAgainst(Material var1);

   Material getMaterialToCheckToolBenchHardnessAgainst();
}
