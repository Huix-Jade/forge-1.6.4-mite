package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BlockClay extends Block {
   public BlockClay(int par1) {
      super(par1, Material.clay, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Item.clay.itemID, 0, 4, 1.0F);
   }
}
