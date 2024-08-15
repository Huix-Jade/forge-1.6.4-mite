package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public final class BlockDirt extends BlockUnderminable {
   protected BlockDirt(int id) {
      super(id, Material.dirt, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setCushioning(0.2F);
   }
}
