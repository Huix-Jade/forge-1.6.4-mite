package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMantleOrCore;

public class ItemMantleOrCore extends ItemMultiTextureTile {
   public ItemMantleOrCore(Block block, String[] names) {
      super(block, names);
      this.setUnlocalizedName("mantle");
   }

   public String getUnlocalizedName(ItemStack item_stack) {
      if (item_stack == null) {
         return super.getUnlocalizedName();
      } else {
         BlockMantleOrCore var10000 = Block.mantleOrCore;
         return BlockMantleOrCore.isCore(Block.mantleOrCore, item_stack.getItemSubtype()) ? "tile.core" : super.getUnlocalizedName();
      }
   }
}
