package net.minecraft.item.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShapelessRecipes implements IRecipe {
   private final ItemStack recipeOutput;
   private final List recipeItems;
   private float difficulty;
   private boolean include_in_lowest_crafting_difficulty_determination;
   private int[] skillsets;
   private boolean propagate_tag_compound;
   private Material material_to_check_tool_bench_hardness_against;

   public ShapelessRecipes(ItemStack recipe_output, List recipe_items, boolean include_in_lowest_crafting_difficulty_determination) {
      this.recipeOutput = recipe_output;
      this.recipeItems = recipe_items;
      RecipeHelper.addRecipe(this, include_in_lowest_crafting_difficulty_determination);
   }

   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World) {
      boolean contains_only_buckets_of_milk = false;
      int num_buckets_of_milk = 0;

      for(Iterator i = this.recipeItems.iterator(); i.hasNext(); ++num_buckets_of_milk) {
         ItemStack item_stack = (ItemStack)i.next();
         if (!(item_stack.getItem() instanceof ItemBucketMilk)) {
            contains_only_buckets_of_milk = false;
            break;
         }

         contains_only_buckets_of_milk = true;
      }

      int var4;
      if (contains_only_buckets_of_milk) {
         contains_only_buckets_of_milk = false;

         for(int row = 0; row < 3; ++row) {
            for(var4 = 0; var4 < 3; ++var4) {
               ItemStack item_stack = par1InventoryCrafting.getStackInRowAndColumn(row, var4);
               if (item_stack != null) {
                  Item item = item_stack.getItem();
                  if (!(item instanceof ItemBucketMilk)) {
                     contains_only_buckets_of_milk = false;
                     row = 3;
                     break;
                  }

                  contains_only_buckets_of_milk = true;
                  --num_buckets_of_milk;
               }
            }
         }

         if (contains_only_buckets_of_milk && num_buckets_of_milk == 0) {
            return true;
         }
      }

      ArrayList var3 = new ArrayList(this.recipeItems);

      for(var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 3; ++var5) {
            ItemStack var6 = par1InventoryCrafting.getStackInRowAndColumn(var5, var4);
            if (var6 != null) {
               boolean var7 = false;
               Iterator var8 = var3.iterator();

               while(var8.hasNext()) {
                  ItemStack var9 = (ItemStack)var8.next();
                  if (var6.itemID == var9.itemID && (var9.getItemSubtype() == 32767 || var6.getItemSubtype() == var9.getItemSubtype())) {
                     var7 = true;
                     var3.remove(var9);
                     break;
                  }
               }

               if (!var7) {
                  return false;
               }
            }
         }
      }

      return var3.isEmpty();
   }

   public CraftingResult getCraftingResult(InventoryCrafting par1InventoryCrafting) {
      ItemStack output = this.recipeOutput.copy();
      if (this.propagate_tag_compound) {
         for(int var3 = 0; var3 < par1InventoryCrafting.getSizeInventory(); ++var3) {
            ItemStack var4 = par1InventoryCrafting.getStackInSlot(var3);
            if (var4 != null && var4.hasTagCompound()) {
               output.setTagCompound((NBTTagCompound)var4.stackTagCompound.copy());
            }
         }
      }

      return new CraftingResult(output, this.difficulty, this.skillsets, this);
   }

   public int getRecipeSize() {
      return this.recipeItems.size();
   }

   public ShapelessRecipes propagateTagCompound() {
      this.propagate_tag_compound = true;
      return this;
   }

   public ShapelessRecipes setDifficulty(int difficulty) {
      this.difficulty = (float)difficulty;
      return this;
   }

   public ItemStack[] getComponents() {
      ItemStack[] recipe_items = new ItemStack[this.recipeItems.size()];

      for(int i = 0; i < this.recipeItems.size(); ++i) {
         recipe_items[i] = (ItemStack)this.recipeItems.get(i);
      }

      return recipe_items;
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
