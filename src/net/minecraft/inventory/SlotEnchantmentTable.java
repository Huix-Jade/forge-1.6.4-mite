package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

class SlotEnchantmentTable extends InventoryBasic {
   // $FF: synthetic field
   final ContainerEnchantment container;

   SlotEnchantmentTable(ContainerEnchantment var1, String var2, boolean var3, int var4) {
      super(var2, var3, var4);
      this.container = var1;
   }

   public int getInventoryStackLimit() {
      return 1;
   }

   public void onInventoryChanged() {
      super.onInventoryChanged();
      this.container.onCraftMatrixChanged(this);
   }

   public boolean isItemValidForSlot(int var1, ItemStack var2) {
      return true;
   }
}
