package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public final class BlockNetherrack extends Block {
   public BlockNetherrack(int par1) {
      super(par1, Material.netherrack, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? 0 : super.dropBlockAsEntityItem(info);
   }
}
