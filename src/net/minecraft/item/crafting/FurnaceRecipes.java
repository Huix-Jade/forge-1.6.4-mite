package net.minecraft.item.crafting;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class FurnaceRecipes {
   private static final FurnaceRecipes smeltingBase = new FurnaceRecipes();
   private Map smeltingList = new HashMap();

   public static final FurnaceRecipes smelting() {
      return smeltingBase;
   }

   private FurnaceRecipes() {
      RecipesMITE.addFurnaceRecipes(this);
   }

   public void addSmelting(int input_item_id, ItemStack output_item_stack) {
      this.smeltingList.put(input_item_id, output_item_stack);
   }

   public ItemStack getSmeltingResult(ItemStack input_item_stack, int heat_level) {
      if (input_item_stack == null) {
         return null;
      } else {
         int input_item_id = input_item_stack.itemID;
         if (heat_level == -1) {
            return (ItemStack)this.smeltingList.get(input_item_id);
         } else {
            ItemStack result_item_stack;
            if (input_item_id == Block.sand.blockID) {
               result_item_stack = (heat_level != 1 || input_item_stack.stackSize >= 4) && input_item_stack.stackSize >= 4 ? new ItemStack(heat_level == 1 ? Block.sandStone : Block.glass) : null;
            } else {
               result_item_stack = (ItemStack)this.smeltingList.get(input_item_id);
            }

            return heat_level < TileEntityFurnace.getHeatLevelRequired(input_item_stack.itemID) ? null : result_item_stack;
         }
      }
   }

   public Map getSmeltingList() {
      return this.smeltingList;
   }

   public boolean doesSmeltingRecipeExistFor(ItemStack input_item_stack) {
      return this.smeltingList.get(input_item_stack.itemID) != null;
   }
}
