package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BlockGlowStone extends Block {
   public BlockGlowStone(int par1, Material par2Material) {
      super(par1, par2Material, (new BlockConstants()).setNeverConnectsWithFence());
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Item.glowstone.itemID, 0, 2 + info.world.rand.nextInt(3), 1.0F);
   }
}
