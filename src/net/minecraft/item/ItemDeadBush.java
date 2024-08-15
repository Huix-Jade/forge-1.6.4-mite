package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemDeadBush extends ItemMultiTextureTile {
   public ItemDeadBush(Block block, String[] names) {
      super(block, names);
      this.setUnlocalizedName("deadbush");
   }

   public String getUnlocalizedName(ItemStack item_stack) {
      if (item_stack == null) {
         return super.getUnlocalizedName();
      } else {
         return Block.deadBush.isWitherwood(item_stack.getItemSubtype()) ? "tile.witherwood" : super.getUnlocalizedName();
      }
   }

   public float getCompostingValue() {
      return 0.0F;
   }
}
