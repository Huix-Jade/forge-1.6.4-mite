package net.minecraft.block;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class BlockLilyPad extends BlockPlant {
   protected BlockLilyPad(int par1) {
      super(par1);
      float var2 = 0.5F;
      float var3 = 0.015625F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var2), 0.0, (double)(0.5F - var2), (double)(0.5F + var2), (double)var3, (double)(0.5F + var2));
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public int getRenderType() {
      return 23;
   }

   public boolean canCollideWithEntity(Entity entity) {
      return !(entity instanceof EntityBoat);
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      return this.canCollideWithEntity(entity) ? super.getCollisionBounds(world, x, y, z, entity) : null;
   }

   public int getBlockColor() {
      return 2129968;
   }

   public int getRenderColor(int par1) {
      return 2129968;
   }

   public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return 2129968;
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return BlockFluid.isFullWaterBlock(block_below, block_below_metadata, false);
   }

   public boolean canBePlacedOnBlock(int metadata, Block block_below, int block_below_metadata, double block_below_bounds_max_y) {
      return this.isLegalOn(metadata, block_below, block_below_metadata);
   }

   public boolean canReplaceBlock(int metadata, Block existing_block, int existing_block_metadata) {
      return false;
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return true;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }

   @Override
   public EnumPlantType getPlantType(World world, int x, int y, int z) {
      return EnumPlantType.Unknown;
   }

   @Override
   public int getPlantID(World world, int x, int y, int z) {
      return 0;
   }

   @Override
   public int getPlantMetadata(World world, int x, int y, int z) {
      return 0;
   }
}
