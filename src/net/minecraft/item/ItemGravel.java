package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemGravel extends ItemMultiTextureTile {
   public ItemGravel(Block block, String[] names) {
      super(block, names);
      this.setUnlocalizedName("gravel");
   }

   public String getUnlocalizedName(ItemStack item_stack) {
      if (item_stack == null) {
         return super.getUnlocalizedName();
      } else {
         return Block.gravel.isNetherGravel(item_stack.getItemSubtype()) ? "tile.gravelNether" : super.getUnlocalizedName();
      }
   }
}
