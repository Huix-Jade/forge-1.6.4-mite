package net.minecraft.client.gui.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotFuel extends Slot {
   private TileEntityFurnace furnace;

   public SlotFuel(IInventory inventory, int slot_index, int display_x, int display_y, TileEntityFurnace furnace) {
      super(inventory, slot_index, display_x, display_y, furnace.acceptsLargeItems());
      this.furnace = furnace;
   }

   public boolean isItemValid(ItemStack item_stack) {
      int heat_level = this.furnace.getItemHeatLevel(item_stack);
      return heat_level > 0 && heat_level <= this.furnace.getMaxHeatLevel() && super.isItemValid(item_stack);
   }
}
