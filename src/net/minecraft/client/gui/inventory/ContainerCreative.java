package net.minecraft.client.gui.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

class ContainerCreative extends Container {
   public List itemList = new ArrayList();

   public ContainerCreative(EntityPlayer par1EntityPlayer) {
      super(par1EntityPlayer);
      InventoryPlayer var2 = par1EntityPlayer.inventory;

      int var3;
      for(var3 = 0; var3 < 5; ++var3) {
         for(int var4 = 0; var4 < 9; ++var4) {
            this.addSlotToContainer(new Slot(GuiContainerCreative.getInventory(), var3 * 9 + var4, 9 + var4 * 18, 18 + var3 * 18));
         }
      }

      for(var3 = 0; var3 < 9; ++var3) {
         this.addSlotToContainer(new Slot(var2, var3, 9 + var3 * 18, 112));
      }

      this.scrollTo(0.0F);
   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      return true;
   }

   public void scrollTo(float par1) {
      int var2 = this.itemList.size() / 9 - 5 + 1;
      int var3 = (int)((double)(par1 * (float)var2) + 0.5);
      if (var3 < 0) {
         var3 = 0;
      }

      for(int var4 = 0; var4 < 5; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            int var6 = var5 + (var4 + var3) * 9;
            if (var6 >= 0 && var6 < this.itemList.size()) {
               GuiContainerCreative.getInventory().setInventorySlotContents(var5 + var4 * 9, (ItemStack)this.itemList.get(var6));
            } else {
               GuiContainerCreative.getInventory().setInventorySlotContents(var5 + var4 * 9, (ItemStack)null);
            }
         }
      }

   }

   public boolean hasMoreThan1PageOfItemsInList() {
      return this.itemList.size() > 45;
   }

   protected void retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer) {
   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
      if (par2 >= this.inventorySlots.size() - 9 && par2 < this.inventorySlots.size()) {
         Slot var3 = (Slot)this.inventorySlots.get(par2);
         if (var3 != null && var3.getHasStack()) {
            var3.putStack((ItemStack)null);
         }
      }

      return null;
   }

   public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot) {
      return par2Slot.yDisplayPosition > 90;
   }

   public boolean canDragIntoSlot(Slot par1Slot) {
      return par1Slot.inventory instanceof InventoryPlayer || par1Slot.yDisplayPosition > 90 && par1Slot.xDisplayPosition <= 162;
   }
}
