package net.minecraft.item.crafting;

import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;
import net.minecraft.world.World;

public class RecipesMapCloning implements IRecipe {
   private float difficulty = 100.0F;
   private int[] skillsets;
   private Material material_to_check_tool_bench_hardness_against;

   public RecipesMapCloning() {
      this.skillsets = new int[]{Skill.FINE_ARTS.id};
   }

   public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World) {
      int var3 = 0;
      ItemStack var4 = null;

      for(int var5 = 0; var5 < par1InventoryCrafting.getSizeInventory(); ++var5) {
         ItemStack var6 = par1InventoryCrafting.getStackInSlot(var5);
         if (var6 != null) {
            if (var6.itemID == Item.map.itemID) {
               if (var4 != null) {
                  return false;
               }

               var4 = var6;
            } else {
               if (var6.itemID != Item.emptyMap.itemID) {
                  return false;
               }

               ++var3;
            }
         }
      }

      return var4 != null && var3 > 0;
   }

   public CraftingResult getCraftingResult(InventoryCrafting par1InventoryCrafting) {
      int var2 = 0;
      ItemStack var3 = null;

      for(int var4 = 0; var4 < par1InventoryCrafting.getSizeInventory(); ++var4) {
         ItemStack var5 = par1InventoryCrafting.getStackInSlot(var4);
         if (var5 != null) {
            if (var5.itemID == Item.map.itemID) {
               if (var3 != null) {
                  return null;
               }

               var3 = var5;
            } else {
               if (var5.itemID != Item.emptyMap.itemID) {
                  return null;
               }

               ++var2;
            }
         }
      }

      if (var3 != null && var2 >= 1) {
         ItemStack var6 = new ItemStack(Item.map, var2 + 1, var3.getItemSubtype());
         if (var3.isItemDamaged()) {
            var6.setItemDamage(var3.getItemDamage());
         }

         if (var3.hasDisplayName()) {
            var6.setItemName(var3.getDisplayName());
         }

         return new CraftingResult(var6, this.difficulty, this.skillsets, this);
      } else {
         return null;
      }
   }

   public int getRecipeSize() {
      return 9;
   }

   public ItemStack getRecipeOutput() {
      return null;
   }

   public ItemStack[] getComponents() {
      return null;
   }

   public IRecipe setDifficulty(float difficulty) {
      this.difficulty = difficulty;
      return this;
   }

   public IRecipe scaleDifficulty(float factor) {
      this.difficulty *= factor;
      return this;
   }

   public float getUnmodifiedDifficulty() {
      return this.difficulty;
   }

   public void setIncludeInLowestCraftingDifficultyDetermination() {
   }

   public boolean getIncludeInLowestCraftingDifficultyDetermination() {
      return false;
   }

   public void setSkillsets(int[] skillsets) {
      this.skillsets = skillsets;
   }

   public void setSkillset(int skillset) {
      this.skillsets = skillset == 0 ? null : new int[]{skillset};
   }

   public int[] getSkillsets() {
      return this.skillsets;
   }

   public void setMaterialToCheckToolBenchHardnessAgainst(Material material_to_check_tool_bench_hardness_against) {
      this.material_to_check_tool_bench_hardness_against = material_to_check_tool_bench_hardness_against;
   }

   public Material getMaterialToCheckToolBenchHardnessAgainst() {
      return this.material_to_check_tool_bench_hardness_against;
   }
}
