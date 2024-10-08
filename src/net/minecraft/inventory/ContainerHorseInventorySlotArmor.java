package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;

class ContainerHorseInventorySlotArmor extends Slot {
   final EntityHorse theHorse;
   final ContainerHorseInventory field_111240_b;

   ContainerHorseInventorySlotArmor(ContainerHorseInventory par1ContainerHorseInventory, IInventory par2IInventory, int par3, int par4, int par5, EntityHorse par6EntityHorse) {
      super(par2IInventory, par3, par4, par5);
      this.field_111240_b = par1ContainerHorseInventory;
      this.theHorse = par6EntityHorse;
   }

   public boolean isItemValid(ItemStack par1ItemStack) {
      return super.isItemValid(par1ItemStack) && this.theHorse.isNormalHorse() && EntityHorse.func_110211_v(par1ItemStack.itemID);
   }

   public boolean func_111238_b() {
      return this.theHorse.isNormalHorse();
   }
}
