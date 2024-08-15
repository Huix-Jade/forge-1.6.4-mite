package net.minecraft.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class SlotBrewingStandIngredient extends Slot {
   final ContainerBrewingStand brewingStand;

   public SlotBrewingStandIngredient(ContainerBrewingStand par1ContainerBrewingStand, IInventory par2IInventory, int par3, int par4, int par5) {
      super(par2IInventory, par3, par4, par5);
      this.brewingStand = par1ContainerBrewingStand;
   }

   public boolean isItemValid(ItemStack par1ItemStack) {
      ContainerBrewingStand cbs = (ContainerBrewingStand)this.getContainer();
      if (!cbs.canPlayerAddIngredients()) {
         return false;
      } else {
         return par1ItemStack != null ? Item.itemsList[par1ItemStack.itemID].isPotionIngredient() : false;
      }
   }

   public int getSlotStackLimit() {
      return 64;
   }
}
