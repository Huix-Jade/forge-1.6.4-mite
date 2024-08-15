package net.minecraft.inventory;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Curse;
import net.minecraft.util.Icon;

class SlotArmor extends Slot {
   final int armorType;
   final ContainerPlayer parent;

   SlotArmor(ContainerPlayer par1ContainerPlayer, IInventory par2IInventory, int par3, int par4, int par5, int par6) {
      super(par2IInventory, par3, par4, par5);
      this.parent = par1ContainerPlayer;
      this.armorType = par6;
   }

   public int getSlotStackLimit() {
      return 1;
   }

   public boolean isItemValid(ItemStack par1ItemStack) {
      if (this.parent.player.hasCurse(Curse.cannot_wear_armor, true)) {
         return false;
      } else {
         return par1ItemStack == null ? false : (par1ItemStack.getItem() instanceof ItemArmor ? ((ItemArmor)par1ItemStack.getItem()).armorType == this.armorType : (par1ItemStack.getItem().itemID != Block.pumpkin.blockID && par1ItemStack.getItem().itemID != Item.skull.itemID ? false : this.armorType == 0));
      }
   }

   public Icon getBackgroundIconIndex() {
      return ItemArmor.func_94602_b(this.armorType);
   }
}
