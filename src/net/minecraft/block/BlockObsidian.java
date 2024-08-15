package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BlockObsidian extends Block {
   public BlockObsidian(int par1) {
      super(par1, Material.obsidian, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? super.dropBlockAsEntityItem(info, Item.shardObsidian.itemID, 0, 6, 0.5F) : super.dropBlockAsEntityItem(info);
   }
}
