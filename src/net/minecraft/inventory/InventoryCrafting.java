package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryCrafting implements IInventory {
   private ItemStack[] stackList;
   private int inventoryWidth;
   private Container eventHandler;

   public InventoryCrafting(Container par1Container, int par2, int par3) {
      int var4 = par2 * par3;
      this.stackList = new ItemStack[var4];
      this.eventHandler = par1Container;
      this.inventoryWidth = par2;
   }

   public int getSizeInventory() {
      return this.stackList.length;
   }

   public ItemStack getStackInSlot(int par1) {
      return par1 >= this.getSizeInventory() ? null : this.stackList[par1];
   }

   public ItemStack getStackInRowAndColumn(int par1, int par2) {
      if (par1 >= 0 && par1 < this.inventoryWidth) {
         int var3 = par1 + par2 * this.inventoryWidth;
         return this.getStackInSlot(var3);
      } else {
         return null;
      }
   }

   public String getCustomNameOrUnlocalized() {
      return "container.crafting";
   }

   public boolean hasCustomName() {
      return false;
   }

   public ItemStack getStackInSlotOnClosing(int par1) {
      if (this.stackList[par1] != null) {
         ItemStack var2 = this.stackList[par1];
         this.stackList[par1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public ItemStack decrStackSize(int par1, int par2) {
      if (this.stackList[par1] != null) {
         ItemStack var3;
         if (this.stackList[par1].stackSize <= par2) {
            var3 = this.stackList[par1];
            this.stackList[par1] = null;
            this.eventHandler.onCraftMatrixChanged(this);
            return var3;
         } else {
            var3 = this.stackList[par1].splitStack(par2);
            if (this.stackList[par1].stackSize == 0) {
               this.stackList[par1] = null;
            }

            this.eventHandler.onCraftMatrixChanged(this);
            return var3;
         }
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
      this.stackList[par1] = par2ItemStack;
      this.eventHandler.onCraftMatrixChanged(this);
   }

   public void setInventory(ItemStack[] item_stacks) {
      this.stackList = item_stacks;
   }

   public ItemStack[] getInventory() {
      return this.stackList;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void onInventoryChanged() {
   }

   public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
      return true;
   }

   public void openChest() {
   }

   public void closeChest() {
   }

   public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
      return true;
   }

   public Container getEventHandler() {
      return this.eventHandler;
   }

   public void destroyInventory() {
      ItemStack[] item_stacks = this.stackList;

      for(int i = 0; i < item_stacks.length; ++i) {
         item_stacks[i] = null;
      }

   }

   public boolean hasDamagedItem() {
      for(int i = 0; i < this.stackList.length; ++i) {
         ItemStack item_stack = this.stackList[i];
         if (item_stack != null && item_stack.isItemDamaged()) {
            return true;
         }
      }

      return false;
   }
}
