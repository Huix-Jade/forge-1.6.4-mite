package net.minecraft.client.gui.inventory;

import net.minecraft.block.BlockAnvil;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotRepairOrEnchantConsumable extends Slot {
   private BlockAnvil anvil;

   public SlotRepairOrEnchantConsumable(IInventory inventory, int slot_index, int display_x, int display_y, BlockAnvil anvil) {
      super(inventory, slot_index, display_x, display_y, true);
      this.anvil = anvil;
   }

   public boolean isItemValid(ItemStack item_stack) {
      if (!super.isItemValid(item_stack)) {
         return false;
      } else {
         Item item = item_stack.getItem();
         if (item != Item.enchantedBook && item != Item.bottleOfDisenchanting) {
            return item_stack.isItemStackDamageable() && !item_stack.isItemEnchanted() ? true : item_stack.isRepairItem();
         } else {
            return true;
         }
      }
   }
}
