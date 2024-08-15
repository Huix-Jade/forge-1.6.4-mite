package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

class ContainerRepairINNER1 extends InventoryBasic {
   // $FF: synthetic field
   final ContainerRepair repairContainer;

   ContainerRepairINNER1(ContainerRepair var1, String var2, boolean var3, int var4) {
      super(var2, var3, var4);
      this.repairContainer = var1;
   }

   public void onInventoryChanged() {
      super.onInventoryChanged();
      this.repairContainer.onCraftMatrixChanged(this);
   }

   public boolean isItemValidForSlot(int var1, ItemStack var2) {
      return true;
   }
}
