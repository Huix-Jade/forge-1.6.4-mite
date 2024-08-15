package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockIce extends BlockBreakable {
   public BlockIce(int par1) {
      super(par1, "ice", Material.ice, false, (new BlockConstants()).setUsesAlphaBlending());
      this.slipperiness = 0.98F;
      this.setTickRandomly(true);
      this.modifyMinHarvestLevel(1);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public int getRenderBlockPass() {
      return 1;
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, 1 - par5);
   }

   public boolean melt(World world, int x, int y, int z) {
      return world.setBlock(x, y, z, Block.waterMoving.blockID);
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if ((!world.isFreezing(x, z) || world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 11 - lightOpacity[this.blockID]) && BlockSnow.canMelt(world, x, y, z)) {
         this.melt(world, x, y, z);
         return true;
      } else {
         return false;
      }
   }

   public int getMobilityFlag() {
      return 0;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return 0;
   }
}
