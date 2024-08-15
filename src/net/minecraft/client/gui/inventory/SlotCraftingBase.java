package net.minecraft.client.gui.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCraftingBase extends Slot {
   protected EntityPlayer player;
   protected int quantity_taken;

   public SlotCraftingBase(EntityPlayer player, IInventory inventory, int slot_index, int display_x, int display_y) {
      super(inventory, slot_index, display_x, display_y);
      this.player = player;
   }

   public boolean isItemValid(ItemStack item_stack) {
      return false;
   }

   public ItemStack decrStackSize(int quantity) {
      if (this.getHasStack()) {
         this.quantity_taken += Math.min(quantity, this.getStack().stackSize);
      }

      return super.decrStackSize(quantity);
   }

   protected void onCrafting(ItemStack item_stack, int quantity) {
      this.quantity_taken += quantity;
      this.onCrafting(item_stack);
   }

   protected void onCrafting(ItemStack item_stack) {
      item_stack.onCrafting(this.player.worldObj, this.player, this.quantity_taken);
      this.quantity_taken = 0;
   }
}
