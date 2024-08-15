package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class RecipesMapExtending extends ShapedRecipes {
   public RecipesMapExtending() {
      super(3, 3, new ItemStack[]{new ItemStack(Item.paper), new ItemStack(Item.paper), new ItemStack(Item.paper), new ItemStack(Item.paper), new ItemStack(Item.map, 0, 32767), new ItemStack(Item.paper), new ItemStack(Item.paper), new ItemStack(Item.paper), new ItemStack(Item.paper)}, new ItemStack(Item.emptyMap, 1, 0), true);
      this.setSkillset(Skill.FINE_ARTS.id);
   }

   public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World) {
      if (!super.matches(par1InventoryCrafting, par2World)) {
         return false;
      } else {
         ItemStack var3 = null;

         for(int var4 = 0; var4 < par1InventoryCrafting.getSizeInventory() && var3 == null; ++var4) {
            ItemStack var5 = par1InventoryCrafting.getStackInSlot(var4);
            if (var5 != null && var5.itemID == Item.map.itemID) {
               var3 = var5;
            }
         }

         if (var3 == null) {
            return false;
         } else if (var3.stackSize != 1) {
            return false;
         } else {
            MapData var6 = Item.map.getMapData(var3, par2World);
            return var6 == null ? false : var6.scale < 4;
         }
      }
   }

   public CraftingResult getCraftingResult(InventoryCrafting par1InventoryCrafting) {
      ItemStack var2 = null;

      for(int var3 = 0; var3 < par1InventoryCrafting.getSizeInventory() && var2 == null; ++var3) {
         ItemStack var4 = par1InventoryCrafting.getStackInSlot(var3);
         if (var4 != null && var4.itemID == Item.map.itemID) {
            var2 = var4;
         }
      }

      var2 = var2.copy();
      var2.stackSize = 1;
      if (var2.getTagCompound() == null) {
         var2.setTagCompound(new NBTTagCompound());
      }

      var2.getTagCompound().setBoolean("map_is_scaling", true);
      return new CraftingResult(var2, this.difficulty, this.getSkillsets(), this);
   }
}
