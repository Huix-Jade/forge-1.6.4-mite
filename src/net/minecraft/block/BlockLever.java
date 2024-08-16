package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Axis;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockLever extends BlockMounted {
   protected BlockLever(int par1) {
      super(par1, Material.circuits, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   public String getMetadataNotes() {
      String[] array = new String[8];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + "=Mounted " + this.getDirectionOfSupportBlock(i).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false) + ", and bit 8 set if pulled";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public final int getRenderType() {
      return 12;
   }

   public static boolean isMaterialSuitableForLeverMounting(Material material, EnumFace face) {
      return material == Material.clay ? false : BlockButton.isMaterialSuitableForButtonMounting(material, face);
   }

   public boolean canMountToBlock(int metadata, Block neighbor_block, int neighbor_block_metadata, EnumFace face) {
      if (!isMaterialSuitableForLeverMounting(neighbor_block.blockMaterial, face)) {
         return false;
      } else {
         return (neighbor_block instanceof BlockPistonBase || neighbor_block instanceof BlockPistonMoving)
                 && face.isSide() && ((BlockDirectional)neighbor_block).isAlignedWith(neighbor_block_metadata, this.getAxisOfMotion(metadata)) ? false :
                 super.canMountToBlock(metadata, neighbor_block, neighbor_block_metadata, face);
      }
   }

   public EnumFace getFaceMountedTo(int metadata) {
      int orientation = metadata & 7;
      if (orientation != 0 && orientation != 7) {
         if (orientation == 1) {
            return EnumFace.EAST;
         } else if (orientation == 2) {
            return EnumFace.WEST;
         } else if (orientation == 3) {
            return EnumFace.SOUTH;
         } else if (orientation == 4) {
            return EnumFace.NORTH;
         } else if (orientation != 5 && orientation != 6) {
            Minecraft.setErrorMessage("getFaceMountedTo: invalid orientation " + orientation);
            return null;
         } else {
            return EnumFace.TOP;
         }
      } else {
         return EnumFace.BOTTOM;
      }
   }

   public final int getDefaultMetadataForFaceMountedTo(EnumFace face) {
      if (face.isBottom()) {
         return 0;
      } else if (face.isEast()) {
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

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction, int base_coord_mode) {
      if (direction.isHorizontal()) {
         return this.getMetadataForDirectionFacing(metadata, direction);
      } else if (base_coord_mode != 0 && base_coord_mode != 2) {
         int toggled_bit = metadata & 8;
         int orientation = metadata & -9;
         orientation = orientation == 0 ? 7 : (orientation == 7 ? 0 : (orientation == 5 ? 6 : (orientation == 6 ? 5 : -1)));
         return orientation | toggled_bit;
      } else {
         return metadata;
      }
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      Block block = world.getNeighborBlock(x, y, z, face.getOpposite());
      if (block instanceof BlockPistonBase && face.isTopOrBottom()) {
         BlockPistonBase block_piston_base = (BlockPistonBase)block;
         EnumDirection direction = block_piston_base.getDirectionFacing(world.getNeighborBlockMetadata(x, y, z, face.getOpposite()));
         if (direction.isNorthOrSouth()) {
            return face.isBottom() ? 0 : 6;
         }

         if (direction.isEastOrWest()) {
            return face.isTop() ? 5 : 7;
         }
      }

      EnumDirection direction = entity.getDirectionFromYaw();
      if (face.isBottom()) {
         return direction.isEastOrWest() ? 0 : 7;
      } else if (face.isTop()) {
         return direction.isNorthOrSouth() ? 5 : 6;
      } else {
         return this.getDefaultMetadataForFaceMountedTo(face);
      }
   }

   public static int invertMetadata(int par0) {
      switch (par0) {
         case 0:
            return 0;
         case 1:
            return 5;
         case 2:
            return 4;
         case 3:
            return 3;
         case 4:
            return 2;
         case 5:
            return 1;
         default:
            return -1;
      }
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 7;
      float var6 = 0.1875F;
      if (var5 == 1) {
         this.setBlockBoundsForCurrentThread(0.0, 0.20000000298023224, (double)(0.5F - var6), (double)(var6 * 2.0F), 0.800000011920929, (double)(0.5F + var6));
      } else if (var5 == 2) {
         this.setBlockBoundsForCurrentThread((double)(1.0F - var6 * 2.0F), 0.20000000298023224, (double)(0.5F - var6), 1.0, 0.800000011920929, (double)(0.5F + var6));
      } else if (var5 == 3) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - var6), 0.20000000298023224, 0.0, (double)(0.5F + var6), 0.800000011920929, (double)(var6 * 2.0F));
      } else if (var5 == 4) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - var6), 0.20000000298023224, (double)(1.0F - var6 * 2.0F), (double)(0.5F + var6), 0.800000011920929, 1.0);
      } else if (var5 != 5 && var5 != 6) {
         if (var5 == 0 || var5 == 7) {
            var6 = 0.25F;
            this.setBlockBoundsForCurrentThread((double)(0.5F - var6), 0.4000000059604645, (double)(0.5F - var6), (double)(0.5F + var6), 1.0, (double)(0.5F + var6));
         }
      } else {
         var6 = 0.25F;
         this.setBlockBoundsForCurrentThread((double)(0.5F - var6), 0.0, (double)(0.5F - var6), (double)(0.5F + var6), 0.6000000238418579, (double)(0.5F + var6));
      }

   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer()) {
         int metadata = world.getBlockMetadata(x, y, z);
         int var11 = metadata & 7;
         int var12 = 8 - (metadata & 8);
         world.setBlockMetadataWithNotify(x, y, z, var11 + var12, 3);
         world.playSoundAtBlock(x, y, z, "random.click", 0.3F, var12 > 0 ? 0.6F : 0.5F);
         world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
         EnumDirection direction = this.getDirectionFacing(metadata);
         if (direction.isEast()) {
            --x;
         } else if (direction.isWest()) {
            ++x;
         } else if (direction.isSouth()) {
            --z;
         } else if (direction.isNorth()) {
            ++z;
         } else if (direction.isUp()) {
            --y;
         } else {
            ++y;
         }

         world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
      }

      return true;
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      if ((par6 & 8) > 0) {
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
         int var7 = par6 & 7;
         if (var7 == 1) {
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
         } else if (var7 == 2) {
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
         } else if (var7 == 3) {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
         } else if (var7 == 4) {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
         } else if (var7 != 5 && var7 != 6) {
            if (var7 == 0 || var7 == 7) {
               par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
            }
         } else {
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
         }
      }

      super.breakBlock(par1World, par2, par3, par4, par5, par6);
   }

   public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) > 0 ? 15 : 0;
   }

   public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      int var6 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      if ((var6 & 8) == 0) {
         return 0;
      } else {
         int var7 = var6 & 7;
         return var7 == 0 && par5 == 0 ? 15 : (var7 == 7 && par5 == 0 ? 15 : (var7 == 6 && par5 == 1 ? 15 : (var7 == 5 && par5 == 1 ? 15 : (var7 == 4 && par5 == 2 ? 15 : (var7 == 3 && par5 == 3 ? 15 : (var7 == 2 && par5 == 4 ? 15 : (var7 == 1 && par5 == 5 ? 15 : 0)))))));
      }
   }

   public boolean canProvidePower() {
      return true;
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.stone, Material.wood});
   }

   public Axis getAxisOfMotion(int metadata) {
      EnumFace face = this.getFaceMountedTo(metadata);
      if (face.isSide()) {
         return Axis.UP_DOWN;
      } else {
         return metadata != 5 && metadata != 7 && metadata != 13 && metadata != 15 ? Axis.EAST_WEST : Axis.NORTH_SOUTH;
      }
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }
}
