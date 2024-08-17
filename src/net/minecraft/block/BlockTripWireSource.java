package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFace;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireSource extends BlockMounted {
   public BlockTripWireSource(int par1) {
      super(par1, Material.circuits, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setTickRandomly(true);
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for orientation, bit 4 set if attached to another hook via wire (ready to be triggered), and bit 8 set if triggered (entity pulled wire)";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public int getRenderType() {
      return 29;
   }

   public final int tickRate(World par1World) {
      return 10;
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (!test_only) {
         this.func_72143_a(world, x, y, z, this.blockID, metadata, false, -1, 0);
      }

      return super.onBlockPlacedMITE(world, x, y, z, metadata, placer, test_only);
   }

   public EnumFace getFaceMountedTo(int metadata) {
      int orientation = metadata & 3;
      if (orientation == 0) {
         return EnumFace.SOUTH;
      } else if (orientation == 1) {
         return EnumFace.WEST;
      } else {
         return orientation == 2 ? EnumFace.NORTH : EnumFace.EAST;
      }
   }

   public final int getDefaultMetadataForFaceMountedTo(EnumFace face) {
      if (face.isSouth()) {
         return 0;
      } else if (face.isWest()) {
         return 1;
      } else if (face.isNorth()) {
         return 2;
      } else {
         return face.isEast() ? 3 : -1;
      }
   }

   public boolean func_72143_a(World par1World, int par2, int par3, int par4, int par5, int par6, boolean par7, int par8, int par9) {
      boolean changed_state = false;
      int var10 = par6 & 3;
      boolean var11 = (par6 & 4) == 4;
      boolean var12 = (par6 & 8) == 8;
      boolean var13 = par5 == Block.tripWireSource.blockID;
      boolean var14 = false;
      boolean var15 = !par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4);
      int var16 = Direction.offsetX[var10];
      int var17 = Direction.offsetZ[var10];
      int var18 = 0;
      int[] var19 = new int[42];

      int var21;
      int var20;
      int var23;
      int var22;
      int var24;
      for(var20 = 1; var20 < 42; ++var20) {
         var21 = par2 + var16 * var20;
         var22 = par4 + var17 * var20;
         var23 = par1World.getBlockId(var21, par3, var22);
         if (var23 == Block.tripWireSource.blockID) {
            var24 = par1World.getBlockMetadata(var21, par3, var22);
            if ((var24 & 3) == Direction.rotateOpposite[var10]) {
               var18 = var20;
            }
            break;
         }

         if (var23 != Block.tripWire.blockID && var20 != par8) {
            var19[var20] = -1;
            var13 = false;
         } else {
            var24 = var20 == par8 ? par9 : par1World.getBlockMetadata(var21, par3, var22);
            boolean var25 = (var24 & 8) != 8;
            boolean var26 = (var24 & 1) == 1;
            boolean var27 = (var24 & 2) == 2;
            var13 &= var27 == var15;
            var14 |= var25 && var26;
            var19[var20] = var24;
            if (var20 == par8) {
               par1World.scheduleBlockUpdate(par2, par3, par4, par5, this.tickRate(par1World));
               var13 &= var25;
            }
         }
      }

      var13 &= var18 > 1;
      var14 &= var13;
      var20 = (var13 ? 4 : 0) | (var14 ? 8 : 0);
      par6 = var10 | var20;
      if (var18 > 0) {
         var21 = par2 + var16 * var18;
         var22 = par4 + var17 * var18;
         var23 = Direction.rotateOpposite[var10];
         par1World.setBlockMetadataWithNotify(var21, par3, var22, var23 | var20, 3);
         this.notifyNeighborOfChange(par1World, var21, par3, var22, var23);
         this.playSoundEffect(par1World, var21, par3, var22, var13, var14, var11, var12);
      }

      this.playSoundEffect(par1World, par2, par3, par4, var13, var14, var11, var12);
      if (par5 > 0) {
         par1World.setBlockMetadataWithNotify(par2, par3, par4, par6, 3);
         changed_state = true;
         if (par7) {
            this.notifyNeighborOfChange(par1World, par2, par3, par4, var10);
         }
      }

      if (var11 != var13) {
         for(var21 = 1; var21 < var18; ++var21) {
            var22 = par2 + var16 * var21;
            var23 = par4 + var17 * var21;
            var24 = var19[var21];
            if (var24 >= 0) {
               if (var13) {
                  var24 |= 4;
               } else {
                  var24 &= -5;
               }

               par1World.setBlockMetadataWithNotify(var22, par3, var23, var24, 3);
            }
         }
      }

      return changed_state;
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      return super.updateTick(par1World, par2, par3, par4, par5Random) ? true : this.func_72143_a(par1World, par2, par3, par4, this.blockID, par1World.getBlockMetadata(par2, par3, par4), true, -1, 0);
   }

   private void playSoundEffect(World par1World, int par2, int par3, int par4, boolean par5, boolean par6, boolean par7, boolean par8) {
      if (par6 && !par8) {
         par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.1, (double)par4 + 0.5, "random.click", 0.4F, 0.6F);
      } else if (!par6 && par8) {
         par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.1, (double)par4 + 0.5, "random.click", 0.4F, 0.5F);
      } else if (par5 && !par7) {
         par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.1, (double)par4 + 0.5, "random.click", 0.4F, 0.7F);
      } else if (!par5 && par7) {
         par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.1, (double)par4 + 0.5, "random.bowhit", 0.4F, 1.2F / (par1World.rand.nextFloat() * 0.2F + 0.9F));
      }

   }

   private void notifyNeighborOfChange(World par1World, int par2, int par3, int par4, int par5) {
      par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
      if (par5 == 3) {
         par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
      } else if (par5 == 1) {
         par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
      } else if (par5 == 0) {
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
      } else if (par5 == 2) {
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
      }

   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 3;
      float var6 = 0.1875F;
      if (var5 == 3) {
         this.setBlockBoundsForCurrentThread(0.0, 0.20000000298023224, (double)(0.5F - var6), (double)(var6 * 2.0F), 0.800000011920929, (double)(0.5F + var6));
      } else if (var5 == 1) {
         this.setBlockBoundsForCurrentThread((double)(1.0F - var6 * 2.0F), 0.20000000298023224, (double)(0.5F - var6), 1.0, 0.800000011920929, (double)(0.5F + var6));
      } else if (var5 == 0) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - var6), 0.20000000298023224, 0.0, (double)(0.5F + var6), 0.800000011920929, (double)(var6 * 2.0F));
      } else if (var5 == 2) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - var6), 0.20000000298023224, (double)(1.0F - var6 * 2.0F), (double)(0.5F + var6), 0.800000011920929, 1.0);
      }

   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      boolean var7 = (par6 & 4) == 4;
      boolean var8 = (par6 & 8) == 8;
      if (var7 || var8) {
         this.func_72143_a(par1World, par2, par3, par4, 0, par6, false, -1, 0);
      }

      if (var8) {
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
         int var9 = par6 & 3;
         if (var9 == 3) {
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
         } else if (var9 == 1) {
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
         } else if (var9 == 0) {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
         } else if (var9 == 2) {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
         }
      }

      super.breakBlock(par1World, par2, par3, par4, par5, par6);
   }

   public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) == 8 ? 15 : 0;
   }

   public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      int var6 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      if ((var6 & 8) != 8) {
         return 0;
      } else {
         int var7 = var6 & 3;
         return var7 == 2 && par5 == 2 ? 15 : (var7 == 0 && par5 == 3 ? 15 : (var7 == 1 && par5 == 4 ? 15 : (var7 == 3 && par5 == 5 ? 15 : 0)));
      }
   }

   public boolean canProvidePower() {
      return true;
   }

   public boolean canBeReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return false;
   }

   public boolean canMountToBlock(int metadata, Block neighbor_block, int neighbor_block_metadata, EnumFace face) {
      return !BlockLever.isMaterialSuitableForLeverMounting(neighbor_block.blockMaterial, face) ? false : super.canMountToBlock(metadata, neighbor_block, neighbor_block_metadata, face);
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }
}
