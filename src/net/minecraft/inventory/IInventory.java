package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IInventory {
   int getSizeInventory();

   ItemStack getStackInSlot(int var1);

   ItemStack decrStackSize(int var1, int var2);

   ItemStack getStackInSlotOnClosing(int var1);

   void setInventorySlotContents(int var1, ItemStack var2);

   String getCustomNameOrUnlocalized();

   boolean hasCustomName();

   int getInventoryStackLimit();

   void onInventoryChanged();

   boolean isUseableByPlayer(EntityPlayer var1);

   void openChest();

   void closeChest();

   boolean isItemValidForSlot(int var1, ItemStack var2);

   void destroyInventory();
}
