package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFace;
import net.minecraft.world.World;

public final class BlockReed extends Block {
   protected BlockReed(int par1) {
      super(par1, Material.plants, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      float var2 = 0.375F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var2), 0.0, (double)(0.5F - var2), (double)(0.5F + var2), 1.0, (double)(0.5F + var2));
      this.setTickRandomly(true);
   }

   public String getMetadataNotes() {
      return "All bits used to track growth";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (super.updateTick(par1World, par2, par3, par4, par5Random)) {
         return true;
      } else if (par5Random.nextFloat() > par1World.getBiomeGenForCoords(par2, par4).temperature - 0.2F) {
         return false;
      } else if (par5Random.nextFloat() < 0.8F) {
         return false;
      } else if (par1World.getBlockLightValue(par2, par3, par4) < 15) {
         return false;
      } else if (this.canOccurAt(par1World, par2, par3 + 1, par4, 0)) {
         int metadata = par1World.getBlockMetadata(par2, par3, par4);
         ++metadata;
         if (metadata == 16) {
            par1World.setBlock(par2, par3 + 1, par4, this.blockID);
            metadata = 0;
         }

         par1World.setBlockMetadataWithNotify(par2, par3, par4, metadata, 4);
         return true;
      } else {
         return false;
      }
   }

   public boolean canOccurAt(World world, int x, int y, int z, int metadata) {
      return world.canBlockSeeTheSky(x, y + 1, z) && super.canOccurAt(world, x, y, z, metadata);
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      if (!super.isLegalAt(world, x, y, z, metadata)) {
         return false;
      } else if (world.getBlock(x, y - 1, z) == this) {
         --y;
         int height = 1;

         while(true) {
            --y;
            if (world.getBlock(x, y, z) != this) {
               return height < 3;
            }

            ++height;
         }
      } else {
         --y;
         return world.getNeighborBlockMaterial(x, y, z, EnumFace.NORTH) == Material.water || world.getNeighborBlockMaterial(x, y, z, EnumFace.EAST) == Material.water || world.getNeighborBlockMaterial(x, y, z, EnumFace.SOUTH) == Material.water || world.getNeighborBlockMaterial(x, y, z, EnumFace.WEST) == Material.water;
      }
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      if (block_below == this) {
         return true;
      } else {
         return block_below == grass || block_below == dirt || block_below == sand;
      }
   }

   public boolean canBePlacedOnBlock(int metadata, Block block_below, int block_below_metadata, double block_below_bounds_max_y) {
      return block_below == this || super.canBePlacedOnBlock(metadata, block_below, block_below_metadata, block_below_bounds_max_y);
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.wasNotLegal() || info.wasExploded()) {
         info.world.destroyBlock(info, false, true);
      }

      return !info.wasExploded() && !info.wasCrushed() ? this.dropBlockAsEntityItem(info, Item.reed) : 0;
   }

   public int getRenderType() {
      return 1;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.reed.itemID;
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }
}
