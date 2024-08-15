package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSign extends BlockMountedWithTileEntity {
   private Class signEntityClass;
   private boolean isFreestanding;

   protected BlockSign(int par1, Class par2Class, boolean par3) {
      super(par1, Material.wood, par2Class, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.isFreestanding = par3;
      this.signEntityClass = par2Class;
      float var4 = 0.25F;
      float var5 = 1.0F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var4), 0.0, (double)(0.5F - var4), (double)(0.5F + var4), (double)var5, (double)(0.5F + var4));
      this.setHardness(0.1F);
   }

   public String getMetadataNotes() {
      String[] array = new String[4];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + 2 + "=Mounted " + this.getDirectionOfSupportBlock(i + 2).getDescriptor(true);
      }

      return this.isFreestanding ? "All bits used for yaw" : StringHelper.implode(array, ", ", true, false);
   }

   public boolean isValidMetadata(int metadata) {
      return this.isFreestanding ? metadata >= 0 && metadata < 16 : metadata > 1 && metadata < 6;
   }

   public Icon getIcon(int par1, int par2) {
      return Block.planks.getBlockTextureFromSide(par1);
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      if (!this.isFreestanding) {
         int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
         float var6 = 0.28125F;
         float var7 = 0.78125F;
         float var8 = 0.0F;
         float var9 = 1.0F;
         float var10 = 0.125F;
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         if (var5 == 2) {
            this.setBlockBoundsForCurrentThread((double)var8, (double)var6, (double)(1.0F - var10), (double)var9, (double)var7, 1.0);
         }

         if (var5 == 3) {
            this.setBlockBoundsForCurrentThread((double)var8, (double)var6, 0.0, (double)var9, (double)var7, (double)var10);
         }

         if (var5 == 4) {
            this.setBlockBoundsForCurrentThread((double)(1.0F - var10), (double)var6, (double)var8, 1.0, (double)var7, (double)var9);
         }

         if (var5 == 5) {
            this.setBlockBoundsForCurrentThread(0.0, (double)var6, (double)var8, (double)var10, (double)var7, (double)var9);
         }
      }

   }

   public int getRenderType() {
      return -1;
   }

   public EnumFace getFaceMountedTo(int metadata) {
      if (this.isFreestanding) {
         return EnumFace.TOP;
      } else {
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
   }

   public final int getDefaultMetadataForFaceMountedTo(EnumFace face) {
      if (this.isFreestanding) {
         return 0;
      } else if (face.isNorth()) {
         return 2;
      } else if (face.isSouth()) {
         return 3;
      } else if (face.isWest()) {
         return 4;
      } else {
         return face.isEast() ? 5 : -1;
      }
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return this.isFreestanding ? entity.getRotationYawAsSixteenths() + 8 & 15 : this.getDefaultMetadataForFaceMountedTo(face);
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.sign.itemID;
   }

   public boolean canBeCarried() {
      return false;
   }

   public void registerIcons(IconRegister par1IconRegister) {
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? this.dropBlockAsEntityItem(info, Item.stick) : this.dropBlockAsEntityItem(info, Item.sign);
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (!test_only && placer instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)placer;
         if (player.onServer()) {
            TileEntitySign tile_entity = (TileEntitySign)world.getBlockTileEntity(x, y, z);
            if (tile_entity != null) {
               player.displayGUIEditSign(tile_entity);
            }
         }
      }

      return true;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.isFreestanding ? "standing" : "mounted";
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }
}
