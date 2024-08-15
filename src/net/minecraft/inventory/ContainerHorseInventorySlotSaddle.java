package net.minecraft.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class ContainerHorseInventorySlotSaddle extends Slot {
   // $FF: synthetic field
   final ContainerHorseInventory field_111239_a;

   ContainerHorseInventorySlotSaddle(ContainerHorseInventory var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.field_111239_a = var1;
   }

   public boolean isItemValid(ItemStack var1) {
      return super.isItemValid(var1) && var1.itemID == Item.saddle.itemID && !this.getHasStack();
   }
}
