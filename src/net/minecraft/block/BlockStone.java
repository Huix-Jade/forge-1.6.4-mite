package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public final class BlockStone extends Block {
   public BlockStone(int par1) {
      super(par1, Material.stone, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, cobblestone);
   }
}
