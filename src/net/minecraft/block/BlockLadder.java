package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFace;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockLadder extends BlockMounted {
   protected BlockLadder(int par1) {
      super(par1, Material.circuits, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setHardness(0.5F);
      this.setMaxStackSize(8);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public String getMetadataNotes() {
      String[] array = new String[4];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + 2 + "=Mounted " + this.getDirectionOfSupportBlock(i + 2).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata > 1 && metadata < 6;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.updateLadderBounds(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
   }

   public void updateLadderBounds(int par1) {
      float var3 = 0.125F;
      if (par1 == 2) {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, (double)(1.0F - var3), 1.0, 1.0, 1.0);
      }

      if (par1 == 3) {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 1.0, (double)var3);
      }

      if (par1 == 4) {
         this.setBlockBoundsForCurrentThread((double)(1.0F - var3), 0.0, 0.0, 1.0, 1.0, 1.0);
      }

      if (par1 == 5) {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, (double)var3, 1.0, 1.0);
      }

   }

   public final int getRenderType() {
      return 8;
   }

   public EnumFace getFaceMountedTo(int metadata) {
      int orientation = metadata;
      if (orientation == 2) {
         return EnumFace.NORTH;
      } else if (orientation == 3) {
         return EnumFace.SOUTH;
      } else if (orientation == 4) {
         return EnumFace.WEST;
      } else if (orientation == 5) {
         return EnumFace.EAST;
      } else {
         Minecraft.setErrorMessage("getFaceMountedTo: invalid orientation " + orientation);
         return null;
      }
   }

   public final int getDefaultMetadataForFaceMountedTo(EnumFace face) {
      if (face.isEast()) {
         return 5;
      } else if (face.isWest()) {
         return 4;
      } else if (face.isSouth()) {
         return 3;
      } else {
         return face.isNorth() ? 2 : -1;
      }
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return true;
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.wood});
   }

   @Override
   public boolean isLadder(World world, int x, int y, int z, EntityLivingBase entity)
   {
      return true;
   }
}
