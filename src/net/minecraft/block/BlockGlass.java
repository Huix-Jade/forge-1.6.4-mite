package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public final class BlockGlass extends BlockBreakable {
   public BlockGlass(int par1, Material par2Material, boolean par3) {
      super(par1, "glass", par2Material, par3, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setMinHarvestLevel(1);
   }

   public int getRenderBlockPass() {
      return 0;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Item.shardGlass.itemID, 0, 6, 1.0F);
   }
}
