package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.Icon;

public class ItemBlockWithMetadata extends ItemBlock {
   public ItemBlockWithMetadata(Block block) {
      super(block);
   }

   public Icon getIconFromSubtype(int par1) {
      return this.getBlock().getIcon(2, par1);
   }

   public int getMetadata(int par1) {
      return par1;
   }
}
