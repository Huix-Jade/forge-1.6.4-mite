package net.minecraft.item.crafting;

import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShapedRecipes implements IRecipe {
   public int recipeWidth;
   public int recipeHeight;
   public ItemStack[] recipeItems;
   public ItemStack recipeOutput;
   public final int recipeOutputItemID;
   private boolean field_92101_f;
   protected float difficulty;
   private boolean include_in_lowest_crafting_difficulty_determination;
   private int[] skillsets;
   private Material material_to_check_tool_bench_hardness_against;
   private static boolean prevent_adding_of_next_recipe;
   private static boolean skip_next_string_or_sinews_that_can_be_anywhere_check;

   public ShapedRecipes(int recipe_width, int recipe_height, ItemStack[] recipe_items, ItemStack recipe_output, boolean include_in_lowest_crafting_difficulty_determinations) {
      this.recipeOutputItemID = recipe_output.itemID;
      this.recipeWidth = recipe_width;
      this.recipeHeight = recipe_height;
      this.recipeItems = recipe_items;
      this.recipeOutput = recipe_output;
      if (prevent_adding_of_next_recipe) {
         prevent_adding_of_next_recipe = false;
      } else {
         RecipeHelper.addRecipe(this, include_in_lowest_crafting_difficulty_determinations);
      }

   }

   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World) {
      for(int var3 = 0; var3 <= 3 - this.recipeWidth; ++var3) {
         for(int var4 = 0; var4 <= 3 - this.recipeHeight; ++var4) {
            if (this.checkMatch(par1InventoryCrafting, var3, var4, true)) {
               return true;
            }

            if (this.checkMatch(par1InventoryCrafting, var3, var4, false)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean checkMatch(InventoryCrafting par1InventoryCrafting, int par2, int par3, boolean par4) {
      boolean has_strings_or_sinews_that_can_be_anywhere = this.recipeOutputItemID == Item.knifeFlint.itemID || this.recipeOutputItemID == Item.hatchetFlint.itemID || this.recipeOutputItemID == Item.shovelFlint.itemID || this.recipeOutputItemID == Item.axeFlint.itemID || this.recipeOutputItemID == Item.knifeObsidian.itemID || this.recipeOutputItemID == Item.hatchetObsidian.itemID || this.recipeOutputItemID == Item.shovelObsidian.itemID || this.recipeOutputItemID == Item.axeObsidian.itemID;
      int num_strings_or_sinews_that_can_be_anywhere;
      int var7;
      int var8;
      ItemStack var10;
      if (skip_next_string_or_sinews_that_can_be_anywhere_check) {
         skip_next_string_or_sinews_that_can_be_anywhere_check = false;
      } else if (has_strings_or_sinews_that_can_be_anywhere) {
         num_strings_or_sinews_that_can_be_anywhere = 0;
         ItemStack[] recipe_items = new ItemStack[this.recipeWidth * this.recipeHeight];

         int i;
         for(var7 = 0; var7 < this.recipeWidth; ++var7) {
            for(var8 = 0; var8 < this.recipeHeight; ++var8) {
               i = var7 + var8 * this.recipeWidth;
               var10 = this.recipeItems[i];
               if (var10 != null) {
                  if (var10.itemID != Item.sinew.itemID && var10.itemID != Item.sinew.itemID) {
                     recipe_items[i] = this.recipeItems[i];
                  } else {
                     ++num_strings_or_sinews_that_can_be_anywhere;
                  }
               }
            }
         }

         var7 = 0;
         ItemStack[] item_stacks = new ItemStack[par1InventoryCrafting.getSizeInventory()];

         for(i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i) {
            var10 = par1InventoryCrafting.getStackInSlot(i);
            if (var10 != null) {
               Item item = var10.getItem();
               if (item != Item.silk && item != Item.sinew) {
                  item_stacks[i] = var10;
               } else {
                  ++var7;
               }
            }
         }

         if (var7 != num_strings_or_sinews_that_can_be_anywhere) {
            return false;
         }

         prevent_adding_of_next_recipe = true;
         ShapedRecipes shaped_recipe = new ShapedRecipes(this.recipeWidth, this.recipeHeight, recipe_items, this.recipeOutput, this.include_in_lowest_crafting_difficulty_determination);
         skip_next_string_or_sinews_that_can_be_anywhere_check = true;
         ItemStack[] original_item_stacks = par1InventoryCrafting.getInventory();
         par1InventoryCrafting.setInventory(item_stacks);
         boolean result = shaped_recipe.checkMatch(par1InventoryCrafting, par2, par3, par4);
         par1InventoryCrafting.setInventory(original_item_stacks);
         return result;
      }

      for(num_strings_or_sinews_that_can_be_anywhere = 0; num_strings_or_sinews_that_can_be_anywhere < 3; ++num_strings_or_sinews_that_can_be_anywhere) {
         for(int var6 = 0; var6 < 3; ++var6) {
            var7 = num_strings_or_sinews_that_can_be_anywhere - par2;
            var8 = var6 - par3;
            ItemStack var9 = null;
            if (var7 >= 0 && var8 >= 0 && var7 < this.recipeWidth && var8 < this.recipeHeight) {
               if (par4) {
                  var9 = this.recipeItems[this.recipeWidth - var7 - 1 + var8 * this.recipeWidth];
               } else {
                  var9 = this.recipeItems[var7 + var8 * this.recipeWidth];
               }
            }

            var10 = par1InventoryCrafting.getStackInRowAndColumn(num_strings_or_sinews_that_can_be_anywhere, var6);
            if (var10 != null || var9 != null) {
               if (var10 == null && var9 != null || var10 != null && var9 == null) {
                  return false;
               }

               if (var9.itemID != var10.itemID) {
                  return false;
               }

               if (var9.getItemSubtype() != 32767 && var9.getItemSubtype() != var10.getItemSubtype()) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public CraftingResult getCraftingResult(InventoryCrafting par1InventoryCrafting) {
      ItemStack var2 = this.getRecipeOutput().copy();
      if (this.field_92101_f) {
         for(int var3 = 0; var3 < par1InventoryCrafting.getSizeInventory(); ++var3) {
            ItemStack var4 = par1InventoryCrafting.getStackInSlot(var3);
            if (var4 != null && var4.hasTagCompound()) {
               var2.setTagCompound((NBTTagCompound)var4.stackTagCompound.copy());
            }
         }
      }

      return new CraftingResult(var2, this.difficulty, this.skillsets, this);
   }

   public int getRecipeSize() {
      return this.recipeWidth * this.recipeHeight;
   }

   public ShapedRecipes func_92100_c() {
      this.field_92101_f = true;
      return this;
   }

   public ItemStack[] getComponents() {
      return this.recipeItems;
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
      this.include_in_lowest_crafting_difficulty_determination = true;
   }

   public boolean getIncludeInLowestCraftingDifficultyDetermination() {
      return this.include_in_lowest_crafting_difficulty_determination;
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
