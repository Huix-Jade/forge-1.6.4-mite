package net.minecraft.item;

import net.minecraft.block.material.Material;

public class ItemBook extends Item {
   public ItemBook(int id, String texture) {
      super(id, texture);
      this.setMaterial(new Material[]{Material.paper, Material.leather});
   }

   public boolean isItemTool(ItemStack par1ItemStack) {
      return par1ItemStack.stackSize == 1;
   }

   public int getItemEnchantability() {
      return 30;
   }

   public boolean canBeRenamed() {
      return false;
   }
}
