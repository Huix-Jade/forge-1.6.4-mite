package net.minecraft.item.crafting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class FurnaceRecipes {
   private static final FurnaceRecipes smeltingBase = new FurnaceRecipes();
   private Map smeltingList = new HashMap();

   private Map experienceList = new HashMap();
   private HashMap<List<Integer>, ItemStack> metaSmeltingList = new HashMap<List<Integer>, ItemStack>();
   private HashMap<List<Integer>, Float> metaExperience = new HashMap<List<Integer>, Float>();

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

   /**
    * A metadata sensitive version of adding a furnace recipe.
    */
   public void addSmelting(int itemID, int metadata, ItemStack itemstack, float experience)
   {
      metaSmeltingList.put(Arrays.asList(itemID, metadata), itemstack);
      metaExperience.put(Arrays.asList(itemstack.itemID, itemstack.getItemDamage()), experience);
   }

   /**
    * Used to get the resulting ItemStack form a source ItemStack
    * @param item The Source ItemStack
    * @return The result ItemStack
    */
   public ItemStack getSmeltingResult(ItemStack item)
   {
      if (item == null)
      {
         return null;
      }
      ItemStack ret = (ItemStack)metaSmeltingList.get(Arrays.asList(item.itemID, item.getItemDamage()));
      if (ret != null)
      {
         return ret;
      }
      return (ItemStack)smeltingList.get(Integer.valueOf(item.itemID));
   }

   /**
    * Grabs the amount of base experience for this item to give when pulled from the furnace slot.
    */
   public float getExperience(ItemStack item)
   {
      if (item == null || item.getItem() == null)
      {
         return 0;
      }
      float ret = item.getItem().getSmeltingExperience(item);
      if (ret < 0 && metaExperience.containsKey(Arrays.asList(item.itemID, item.getItemDamage())))
      {
         ret = metaExperience.get(Arrays.asList(item.itemID, item.getItemDamage()));
      }
      if (ret < 0 && experienceList.containsKey(item.itemID))
      {
         ret = ((Float)experienceList.get(item.itemID)).floatValue();
      }
      return (ret < 0 ? 0 : ret);
   }

   public Map<List<Integer>, ItemStack> getMetaSmeltingList()
   {
      return metaSmeltingList;
   }
}
