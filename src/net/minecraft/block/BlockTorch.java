package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTorch extends BlockMounted {
   protected BlockTorch(int par1) {
      super(par1, Material.circuits, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setTickRandomly(true);
      this.setMaxStackSize(16);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public String getMetadataNotes() {
      String[] array = new String[5];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + 1 + "=Mounted " + this.getDirectionOfSupportBlock(i + 1).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata > 0 && metadata < 6;
   }

   public final int getRenderType() {
      return 2;
   }

   public EnumFace getFaceMountedTo(int metadata) {
      if (metadata == 1) {
         return EnumFace.EAST;
      } else if (metadata == 2) {
         return EnumFace.WEST;
      } else if (metadata == 3) {
         return EnumFace.SOUTH;
      } else if (metadata == 4) {
         return EnumFace.NORTH;
      } else if (metadata == 5) {
         return EnumFace.TOP;
      } else {
         Minecraft.setErrorMessage("getFaceMountedTo: unexpected metadata " + metadata);
         return null;
      }
   }

   public final int getDefaultMetadataForFaceMountedTo(EnumFace face) {
      if (face.isEast()) {
         return 1;
      } else if (face.isWest()) {
         return 2;
      } else if (face.isSouth()) {
         return 3;
      } else if (face.isNorth()) {
         return 4;
      } else {
         return face.isTop() ? 5 : -1;
      }
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      return super.updateTick(world, x, y, z, random);
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var7 = par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 7;
      float var8 = 0.15F;
      if (var7 == 1) {
         this.setBlockBoundsForCurrentThread(0.0, 0.20000000298023224, (double)(0.5F - var8), (double)(var8 * 2.0F), 0.800000011920929, (double)(0.5F + var8));
      } else if (var7 == 2) {
         this.setBlockBoundsForCurrentThread((double)(1.0F - var8 * 2.0F), 0.20000000298023224, (double)(0.5F - var8), 1.0, 0.800000011920929, (double)(0.5F + var8));
      } else if (var7 == 3) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - var8), 0.20000000298023224, 0.0, (double)(0.5F + var8), 0.800000011920929, (double)(var8 * 2.0F));
      } else if (var7 == 4) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - var8), 0.20000000298023224, (double)(1.0F - var8 * 2.0F), (double)(0.5F + var8), 0.800000011920929, 1.0);
      } else {
         var8 = 0.1F;
         this.setBlockBoundsForCurrentThread((double)(0.5F - var8), 0.0, (double)(0.5F - var8), (double)(0.5F + var8), 0.6000000238418579, (double)(0.5F + var8));
      }

   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      int var6 = par1World.getBlockMetadata(par2, par3, par4);
      double var7 = (double)par2 + 0.5;
      double var9 = (double)par3 + 0.7;
      double var11 = (double)par4 + 0.5;
      double var13 = 0.2199999988079071;
      double var15 = 0.27000001072883606;
      if (var6 == 1) {
         par1World.spawnParticle(EnumParticle.smoke, var7 - var15, var9 + var13, var11, 0.0, 0.0, 0.0);
         par1World.spawnParticle(EnumParticle.flame, var7 - var15, var9 + var13, var11, 0.0, 0.0, 0.0);
      } else if (var6 == 2) {
         par1World.spawnParticle(EnumParticle.smoke, var7 + var15, var9 + var13, var11, 0.0, 0.0, 0.0);
         par1World.spawnParticle(EnumParticle.flame, var7 + var15, var9 + var13, var11, 0.0, 0.0, 0.0);
      } else if (var6 == 3) {
         par1World.spawnParticle(EnumParticle.smoke, var7, var9 + var13, var11 - var15, 0.0, 0.0, 0.0);
         par1World.spawnParticle(EnumParticle.flame, var7, var9 + var13, var11 - var15, 0.0, 0.0, 0.0);
      } else if (var6 == 4) {
         par1World.spawnParticle(EnumParticle.smoke, var7, var9 + var13, var11 + var15, 0.0, 0.0, 0.0);
         par1World.spawnParticle(EnumParticle.flame, var7, var9 + var13, var11 + var15, 0.0, 0.0, 0.0);
      } else {
         par1World.spawnParticle(EnumParticle.smoke, var7, var9, var11, 0.0, 0.0, 0.0);
         par1World.spawnParticle(EnumParticle.flame, var7, var9, var11, 0.0, 0.0, 0.0);
      }

   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? 0 : super.dropBlockAsEntityItem(info);
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.wood, Material.coal});
   }

   public boolean canMountToBlock(int metadata, Block neighbor_block, int neighbor_block_metadata, EnumFace face) {
      if (face.isTop()) {
         if (neighbor_block != null && neighbor_block.canPlaceTorchOnTop(neighbor_block)) {
            return true;
         }

         if (neighbor_block != leaves && !(neighbor_block instanceof BlockDirectional) && neighbor_block.isFaceFlatAndSolid(neighbor_block_metadata, face)) {
            return true;
         }
      } else if (face.isSide()) {
         if (neighbor_block == snow && neighbor_block_metadata > 2) {
            return true;
         }

         if (neighbor_block.isSingleSlab()) {
            if (BlockSlab.isBottom(neighbor_block_metadata)) {
               return true;
            }
         } else if (neighbor_block instanceof BlockStairs && (neighbor_block_metadata & 4) == 0) {
            return true;
         }
      }

      if (neighbor_block == cloth) {
         return true;
      } else {
         return (neighbor_block instanceof BlockPistonBase || neighbor_block instanceof BlockPistonMoving || neighbor_block instanceof BlockPistonExtension) && face == Block.pistonBase.getFrontFace(neighbor_block_metadata) ? true : super.canMountToBlock(metadata, neighbor_block, neighbor_block_metadata, face);
      }
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }
}
