package net.minecraft.client.gui.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

class SlotCreativeInventory extends Slot {
   private final Slot theSlot;
   // $FF: synthetic field
   final GuiContainerCreative theCreativeInventory;

   public SlotCreativeInventory(GuiContainerCreative var1, Slot var2, int var3) {
      super(var2.inventory, var3, 0, 0);
      this.theCreativeInventory = var1;
      this.theSlot = var2;
   }

   public void onPickupFromSlot(EntityPlayer var1, ItemStack var2) {
      this.theSlot.onPickupFromSlot(var1, var2);
   }

   public boolean isItemValid(ItemStack var1) {
      return this.theSlot.isItemValid(var1);
   }

   public ItemStack getStack() {
      return this.theSlot.getStack();
   }

   public boolean getHasStack() {
      return this.theSlot.getHasStack();
   }

   public void putStack(ItemStack var1) {
      this.theSlot.putStack(var1);
   }

   public void onSlotChanged() {
      this.theSlot.onSlotChanged();
   }

   public int getSlotStackLimit() {
      return this.theSlot.getSlotStackLimit();
   }

   public Icon getBackgroundIconIndex() {
      return this.theSlot.getBackgroundIconIndex();
   }

   public ItemStack decrStackSize(int var1) {
      return this.theSlot.decrStackSize(var1);
   }

   public boolean isSlotInInventory(IInventory var1, int var2) {
      return this.theSlot.isSlotInInventory(var1, var2);
   }

   // $FF: synthetic method
   static Slot func_75240_a(SlotCreativeInventory var0) {
      return var0.theSlot;
   }
}
