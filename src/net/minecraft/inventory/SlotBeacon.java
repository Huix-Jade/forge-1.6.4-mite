package net.minecraft.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

class SlotBeacon extends Slot {
   // $FF: synthetic field
   final ContainerBeacon beacon;

   public SlotBeacon(ContainerBeacon var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.beacon = var1;
   }

   public boolean isItemValid(ItemStack var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.itemID == Item.emerald.itemID || var1.itemID == Item.diamond.itemID || var1.itemID == Item.ingotGold.itemID || var1.itemID == Item.ingotIron.itemID;
      }
   }

   public int getSlotStackLimit() {
      return 1;
   }
}
