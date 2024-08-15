package net.minecraft.dispenser;

import net.minecraft.item.ItemStack;

final class BehaviorDispenseItemProvider implements IBehaviorDispenseItem {
   public ItemStack dispense(IBlockSource var1, ItemStack var2) {
      return var2;
   }
}
