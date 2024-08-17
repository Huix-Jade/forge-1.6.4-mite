package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFace;
import net.minecraft.util.ReferenceFileWriter;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockSnow extends Block {
   protected BlockSnow(int par1) {
      super(par1, Material.snow, (new BlockConstants()).setNotAlwaysLegal());
      this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setBlockBoundsForSnowDepth(0, true);
      this.setMaxStackSize(32);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("snow");
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      int depth = getDepth(world.getBlockMetadata(x, y, z));
      if (entity instanceof EntityItem) {
         ++depth;
      }

      return depth == 1 ? null : getStandardFormBoundingBoxFromPool(x, y, z).setMaxY((double)y + this.getRenderHeight(depth - 1));
   }

   public double getRenderHeight(int depth) {
      return (double)depth * 0.125;
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      this.setBlockBoundsForSnowDepth(item_damage, false);
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.setBlockBoundsForSnowDepth(par1IBlockAccess.getBlockMetadata(par2, par3, par4), false);
   }

   protected void setBlockBoundsForSnowDepth(int par1, boolean for_all_threads) {
      int var2 = par1 & 7;
      float var3 = (float)(2 * (1 + var2)) / 16.0F;
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, (double)var3, 1.0, for_all_threads);
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      if (getDepth(metadata) == getMaxDepth()) {
         return Block.blockSnow.isLegalOn(metadata, block_below, block_below_metadata);
      } else {
         return block_below != null && block_below.isBlockTopFacingSurfaceSolid(block_below_metadata);
      }
   }

   public static int getDepthBits() {
      return 7;
   }

   public static int getDepth(int metadata) {
      return (metadata & getDepthBits()) + 1;
   }

   public static int setDepth(int metadata, int depth) {
      return metadata & ~getDepthBits() | depth - 1 & getDepthBits();
   }

   public static int getMaxDepth() {
      return getDepthBits() + 1;
   }

   public static boolean canMelt(World world, int x, int y, int z) {
      Block block_above = Block.blocksList[world.getBlockId(x, y + 1, z)];
      return block_above == null || block_above.blockMaterial != Material.snow && block_above.blockMaterial != Material.craftedSnow && block_above.blockMaterial != Material.ice;
   }

   public boolean melt(World world, int x, int y, int z) {
      int metadata = world.getBlockMetadata(x, y, z);
      if (metadata == 0) {
         return world.setBlockToAir(x, y, z);
      } else {
         --metadata;
         return world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
      }
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (super.updateTick(par1World, par2, par3, par4, par5Random)) {
         return true;
      } else if ((!par1World.isFreezing(par2, par4) || par1World.getSavedLightValue(EnumSkyBlock.Block, par2, par3 + 1, par4) > 11)
              && canMelt(par1World, par2, par3, par4)) {
         this.melt(par1World, par2, par3, par4);
         return true;
      } else {
         return false;
      }
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return par5 == 1 ? true : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
   }

   public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
      if (!world.isRemote) {
         if (this == Block.snow && world.isAirBlock(x, y - 1, z)) {
            this.dropBlockAsEntityItem((new BlockBreakInfo(world, x, y, z)).setDroppedSelf());
            world.setBlockToAir(x, y, z);
         }

      }
   }

   public float getCushioning(int metadata) {
      return 0.2F * (float)getDepth(metadata);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.wasFlooded()) {
         return 0;
      } else {
         if (info.wasNotLegal()) {
            info.world.destroyBlock(info, false, true);
         }

         return !info.wasHarvestedByPlayer() ? 0 : this.dropBlockAsEntityItem(info, Item.snowball.itemID, 0, getDepth(info.getMetadata()), 1.0F);
      }
   }

   public boolean isSlab(int metadata) {
      return getDepth(metadata) == 4;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return ReferenceFileWriter.running ? (this.isSlab(metadata) ? "slab" : "thin") : null;
   }

   public boolean canBeReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      if (other_block != null && other_block != this && other_block != blockSnow) {
         if (other_block.getBlockHardness(other_block_metadata) == 0.0F) {
            return false;
         } else if (other_block.isLiquid()) {
            return getDepth(metadata) < 8;
         } else {
            return getDepth(metadata) == 1;
         }
      } else {
         return false;
      }
   }

   public boolean isFaceFlatAndSolid(int metadata, EnumFace face) {
      int depth = getDepth(metadata);
      return depth == 1 ? false : face.isBottom() || depth == getMaxDepth();
   }

   public float getBlockHardness(int metadata) {
      return BlockHardness.snow * (float)getDepth(metadata);
   }

   public boolean tryPlaceBlock(World world, int x, int y, int z, EnumFace face, int metadata, Entity placer, boolean perform_placement_check, boolean drop_existing_block, boolean test_only) {
      return getDepth(metadata) == getMaxDepth() ? blockSnow.tryPlaceBlock(world, x, y, z, face, 0, placer, perform_placement_check, drop_existing_block, test_only) : super.tryPlaceBlock(world, x, y, z, face, metadata, placer, perform_placement_check, drop_existing_block, test_only);
   }

   public String getMetadataNotes() {
      return "Bits 1, 2, and 4 used for snow depth";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 8;
   }

   public int getItemSubtype(int metadata) {
      if (!this.isValidMetadata(metadata)) {
         this.reportInvalidMetadata(metadata);
      }

      return this.isSlab(metadata) ? metadata & getDepthBits() : 0;
   }

   public boolean hidesAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      if (side == 1) {
         return true;
      } else if (side != 0 && neighbor == this) {
         EnumFace face = EnumFace.get(side).getOpposite();
         int neighbor_metadata = block_access.getBlockMetadata(face.getNeighborX(x), y, face.getNeighborZ(z));
         return neighbor_metadata == 0 || neighbor_metadata <= block_access.getBlockMetadata(x, y, z);
      } else {
         return false;
      }
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return getDepth(metadata) > 1;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return getDepth(metadata) > 5;
   }

   public boolean showDestructionParticlesWhenReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return true;
   }

   @Override
   public int quantityDropped(int meta, int fortune, Random random)
   {
      return (meta & 7) + 1;
   }

   /**
    * Determines if a new block can be replace the space occupied by this one,
    * Used in the player's placement code to make the block act like water, and lava.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y position
    * @param z Z position
    * @return True if the block is replaceable by another block
    */
   @Override
   public boolean isBlockReplaceable(World world, int x, int y, int z)
   {
      int meta = world.getBlockMetadata(x, y, z);
      return (meta >= 7 ? false : blockMaterial.isReplaceable());
   }
}
