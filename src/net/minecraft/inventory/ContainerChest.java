package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container {
   private IInventory lowerChestInventory;
   private int numRows;

   public ContainerChest(EntityPlayer player, IInventory par2IInventory) {
      super(player);
      this.lowerChestInventory = par2IInventory;
      this.numRows = par2IInventory.getSizeInventory() / 9;
      par2IInventory.openChest();
      int var3 = (this.numRows - 4) * 18;

      int var4;
      int var5;
      for(var4 = 0; var4 < this.numRows; ++var4) {
         for(var5 = 0; var5 < 9; ++var5) {
            this.addSlotToContainer(new Slot(par2IInventory, var5 + var4 * 9, 8 + var5 * 18, 18 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 3; ++var4) {
         for(var5 = 0; var5 < 9; ++var5) {
            this.addSlotToContainer(new Slot(player.inventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 103 + var4 * 18 + var3));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.addSlotToContainer(new Slot(player.inventory, var4, 8 + var4 * 18, 161 + var3));
      }

   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      return this.lowerChestInventory.isUseableByPlayer(par1EntityPlayer);
   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.inventorySlots.get(par2);
      if (var4 != null && var4.getHasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (par2 < this.numRows * 9) {
            if (!this.mergeItemStack(var5, this.numRows * 9, this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (!this.mergeItemStack(var5, 0, this.numRows * 9, false)) {
            return null;
         }

         if (var5.stackSize == 0) {
            var4.putStack((ItemStack)null);
         } else {
            var4.onSlotChanged();
         }
      }

      return var3;
   }

   public void onContainerClosed(EntityPlayer par1EntityPlayer) {
      super.onContainerClosed(par1EntityPlayer);
      this.lowerChestInventory.closeChest();
   }

   public IInventory getLowerChestInventory() {
      return this.lowerChestInventory;
   }
}
