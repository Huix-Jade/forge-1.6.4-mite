package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.item.ItemStack;

class CallableItemName implements Callable {
   // $FF: synthetic field
   final ItemStack theItemStack;
   // $FF: synthetic field
   final InventoryPlayer playerInventory;

   CallableItemName(InventoryPlayer var1, ItemStack var2) {
      this.playerInventory = var1;
      this.theItemStack = var2;
   }

   public String callItemDisplayName() {
      return this.theItemStack.getDisplayName();
   }

   // $FF: synthetic method
   public Object call() {
      return this.callItemDisplayName();
   }
}
