package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

class SlotBrewingStandPotion extends Slot {
   private EntityPlayer player;

   public SlotBrewingStandPotion(EntityPlayer par1EntityPlayer, IInventory par2IInventory, int par3, int par4, int par5) {
      super(par2IInventory, par3, par4, par5);
      this.player = par1EntityPlayer;
   }

   public boolean isItemValid(ItemStack par1ItemStack) {
      return canHoldPotion(par1ItemStack);
   }

   public int getSlotStackLimit() {
      return 1;
   }

   public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack) {
      if (par2ItemStack.getItem() instanceof ItemPotion && par2ItemStack.getItemDamage() > 0) {
         this.player.addStat(AchievementList.potion, 1);
      }

      super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
   }

   public static boolean canHoldPotion(ItemStack par0ItemStack) {
      return par0ItemStack != null && (par0ItemStack.getItem() instanceof ItemPotion || par0ItemStack.itemID == Item.glassBottle.itemID);
   }
}
