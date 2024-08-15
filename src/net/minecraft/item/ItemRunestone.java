package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRunestone;

public class ItemRunestone extends ItemBlock {
   public ItemRunestone(Block block) {
      super(block);
   }

   public int getMetadata(int par1) {
      return par1;
   }

   public static String getMagicName(ItemStack item_stack) {
      return BlockRunestone.getMagicName(item_stack.getItemSubtype());
   }

   public String getItemDisplayName(ItemStack item_stack) {
      return item_stack == null ? super.getItemDisplayName(item_stack) : super.getItemDisplayName(item_stack) + " \"" + getMagicName(item_stack) + "\"";
   }

   public boolean canBeRenamed() {
      return false;
   }
}
