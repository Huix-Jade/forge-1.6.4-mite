package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFace;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTrapDoor extends BlockMounted {
   private static final int hinge_side_bits = 3;
   private static final int open_bit = 4;
   private static final int height_bit = 8;

   protected BlockTrapDoor(int par1, Material par2Material) {
      super(par1, par2Material, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      float var3 = 0.5F;
      float var4 = 1.0F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var3), 0.0, (double)(0.5F - var3), (double)(0.5F + var3), (double)var4, (double)(0.5F + var3));
      this.setHardness(BlockHardness.planks);
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   public String getMetadataNotes() {
      String[] array = new String[4];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + "=Mounted " + this.getDirectionOfSupportBlock(i).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false) + ", bit 4 set if open, and bit 8 set if upper";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public int getRenderType() {
      return 0;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.setBlockBoundsForBlockRender(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      float var1 = 0.1875F;
      this.setBlockBoundsForCurrentThread(0.0, (double)(0.5F - var1 / 2.0F), 0.0, 1.0, (double)(0.5F + var1 / 2.0F), 1.0);
   }

   public void setBlockBoundsForBlockRender(int par1) {
      float var2 = 0.1875F;
      if ((par1 & 8) != 0) {
         this.setBlockBoundsForCurrentThread(0.0, (double)(1.0F - var2), 0.0, 1.0, 1.0, 1.0);
      } else {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, (double)var2, 1.0);
      }

      if (isTrapdoorOpen(par1)) {
         if ((par1 & 3) == 0) {
            this.setBlockBoundsForCurrentThread(0.0, 0.0, (double)(1.0F - var2), 1.0, 1.0, 1.0);
         }

         if ((par1 & 3) == 1) {
            this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 1.0, (double)var2);
         }

         if ((par1 & 3) == 2) {
            this.setBlockBoundsForCurrentThread((double)(1.0F - var2), 0.0, 0.0, 1.0, 1.0, 1.0);
         }

         if ((par1 & 3) == 3) {
            this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, (double)var2, 1.0, 1.0);
         }
      }

   }

   public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (this.blockMaterial != Material.wood) {
         return false;
      } else {
         if (player.onServer()) {
            int metadata = world.getBlockMetadata(x, y, z);
            metadata ^= 4;
            world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
            this.makeOpenOrCloseSound(world, x, y, z, metadata);
         }

         return true;
      }
   }

   private void makeOpenOrCloseSound(World world, int x, int y, int z, int metadata_after) {
      if (this.isOpen(metadata_after)) {
         world.playSoundAtBlock(x, y, z, "random.door_open", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
      } else {
         world.playSoundAtBlock(x, y, z, "random.door_close", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
      }

   }

   public boolean onPoweredBlockChange(World world, int x, int y, int z, boolean par5) {
      int metadata = world.getBlockMetadata(x, y, z);
      boolean var7 = (metadata & 4) > 0;
      if (var7 != par5) {
         metadata ^= 4;
         if (world.setBlockMetadataWithNotify(x, y, z, metadata, 2)) {
            this.makeOpenOrCloseSound(world, x, y, z, metadata);
            return true;
         }
      }

      return false;
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (super.onNeighborBlockChange(world, x, y, z, neighbor_block_id)) {
         return true;
      } else {
         boolean is_indirectly_powered = world.isBlockIndirectlyGettingPowered(x, y, z);
         return !is_indirectly_powered && (neighbor_block_id <= 0 || !Block.blocksList[neighbor_block_id].canProvidePower()) ? false : this.onPoweredBlockChange(world, x, y, z, is_indirectly_powered);
      }
   }

   public EnumFace getFaceMountedTo(int metadata) {
      int side = metadata & 3;
      if (side == 0) {
         return EnumFace.NORTH;
      } else if (side == 1) {
         return EnumFace.SOUTH;
      } else {
         return side == 2 ? EnumFace.WEST : EnumFace.EAST;
      }
   }

   public final int getDefaultMetadataForFaceMountedTo(EnumFace face) {
      if (face.isNorth()) {
         return 0;
      } else if (face.isSouth()) {
         return 1;
      } else if (face.isWest()) {
         return 2;
      } else {
         return face.isEast() ? 3 : -1;
      }
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return this.getDefaultMetadataForFaceMountedTo(face) | (!face.isTopOrBottom() && offset_y > 0.5F ? 8 : 0);
   }

   public boolean canMountToBlock(int metadata, Block neighbor_block, int neighbor_block_metadata, EnumFace face) {
      if (!BlockDoor.isSuitableMaterialForAttachingHingesTo(neighbor_block.blockMaterial)) {
         return false;
      } else if (neighbor_block == glass) {
         return true;
      } else {
         if (neighbor_block.isSingleSlab()) {
            if (BlockSlab.isBottom(neighbor_block_metadata)) {
               if (isLower(metadata)) {
                  return true;
               }
            } else if (!isLower(metadata)) {
               return true;
            }
         } else if (neighbor_block instanceof BlockStairs) {
            if ((neighbor_block_metadata & 4) == 0) {
               if (isLower(metadata)) {
                  return true;
               }
            } else if (!isLower(metadata)) {
               return true;
            }
         } else if (neighbor_block == snow && neighbor_block_metadata > 2 && isLower(metadata)) {
            return true;
         }

         return super.canMountToBlock(metadata, neighbor_block, neighbor_block_metadata, face);
      }
   }

   public static boolean isTrapdoorOpen(int par0) {
      return (par0 & 4) != 0;
   }

   private boolean isOpen(int metadata) {
      return (metadata & 4) != 0;
   }

   public int setOpen(int metadata, boolean open) {
      return open ? metadata | 4 : metadata & -5;
   }

   public static boolean isLower(int metadata) {
      return (metadata & 8) == 0;
   }

   public int setUpper(int metadata, boolean upper) {
      return upper ? metadata | 8 : metadata & -9;
   }

   public boolean isPortal() {
      return true;
   }

   public boolean isOpenPortal(World world, int x, int y, int z) {
      return this.isOpen(world.getBlockMetadata(x, y, z));
   }

   public boolean isFaceFlatAndSolid(int metadata, EnumFace face) {
      if (!face.isSide() && !this.isOpen(metadata)) {
         return isLower(metadata) ? face.isBottom() : face.isTop();
      } else {
         return false;
      }
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return !this.isOpen(metadata);
   }
}
