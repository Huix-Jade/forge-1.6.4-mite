package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerMerchant extends Container {
   private IMerchant theMerchant;
   private InventoryMerchant merchantInventory;

   public ContainerMerchant(EntityPlayer player, IMerchant par2IMerchant) {
      super(player);
      this.theMerchant = par2IMerchant;
      this.merchantInventory = new InventoryMerchant(player, par2IMerchant);
      this.addSlotToContainer(new Slot(this.merchantInventory, 0, 36, 53));
      this.addSlotToContainer(new Slot(this.merchantInventory, 1, 62, 53));
      this.addSlotToContainer(new SlotMerchantResult(player, par2IMerchant, this.merchantInventory, 2, 120, 53));

      int var4;
      for(var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlotToContainer(new Slot(player.inventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.addSlotToContainer(new Slot(player.inventory, var4, 8 + var4 * 18, 142));
      }

   }

   public InventoryMerchant getMerchantInventory() {
      return this.merchantInventory;
   }

   public void addCraftingToCrafters(ICrafting par1ICrafting) {
      super.addCraftingToCrafters(par1ICrafting);
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();
   }

   public void onCraftMatrixChanged(IInventory par1IInventory) {
      this.merchantInventory.resetRecipeAndSlots();
      super.onCraftMatrixChanged(par1IInventory);
   }

   public void setCurrentRecipeIndex(int par1) {
      this.merchantInventory.setCurrentRecipeIndex(par1);
   }

   public void updateProgressBar(int par1, int par2) {
   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      return this.theMerchant.getCustomer() == par1EntityPlayer;
   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.inventorySlots.get(par2);
      if (var4 != null && var4.getHasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (par2 == 2) {
            if (!this.mergeItemStack(var5, 3, 39, true)) {
               return null;
            }

            var4.onSlotChange(var5, var3);
         } else if (par2 != 0 && par2 != 1) {
            if (par2 >= 3 && par2 < 30) {
               if (!this.mergeItemStack(var5, 30, 39, false)) {
                  return null;
               }
            } else if (par2 >= 30 && par2 < 39 && !this.mergeItemStack(var5, 3, 30, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(var5, 3, 39, false)) {
            return null;
         }

         if (var5.stackSize == 0) {
            var4.putStack((ItemStack)null);
         } else {
            var4.onSlotChanged();
         }

         if (var5.stackSize == var3.stackSize) {
            return null;
         }

         var4.onPickupFromSlot(par1EntityPlayer, var5);
      }

      return var3;
   }

   public void onContainerClosed(EntityPlayer par1EntityPlayer) {
      super.onContainerClosed(par1EntityPlayer);
      this.theMerchant.setCustomer((EntityPlayer)null);
      super.onContainerClosed(par1EntityPlayer);
      if (!this.world.isRemote) {
         ItemStack var2 = this.merchantInventory.getStackInSlotOnClosing(0);
         if (var2 != null) {
            par1EntityPlayer.dropPlayerItem(var2);
         }

         var2 = this.merchantInventory.getStackInSlotOnClosing(1);
         if (var2 != null) {
            par1EntityPlayer.dropPlayerItem(var2);
         }
      }

   }
}
