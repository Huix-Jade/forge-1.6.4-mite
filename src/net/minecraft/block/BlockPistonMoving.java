package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Facing;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockDirectionalWithTileEntity {
   public BlockPistonMoving(int par1) {
      super(par1, Material.piston, (new BlockConstants()).setNotAlwaysLegal());
      this.setHardness(-1.0F);
      this.setUnlocalizedName("pistonMoving");
   }

   public String getMetadataNotes() {
      String[] array = new String[6];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + "=" + pistonBase.getDirectionFacing(i).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false) + ", bit 8 set if extension is sticky";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 14;
   }

   public TileEntity createNewTileEntity(World par1World) {
      return null;
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      TileEntity var7 = par1World.getBlockTileEntity(par2, par3, par4);
      if (var7 instanceof TileEntityPiston) {
         ((TileEntityPiston)var7).clearPistonTileEntity();
      } else {
         super.breakBlock(par1World, par2, par3, par4, par5, par6);
      }

   }

   public int getRenderType() {
      return -1;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (world.getBlockTileEntity(x, y, z) == null) {
         if (player.onServer()) {
            world.setBlockToAir(x, y, z);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      TileEntityPiston var8 = this.getTileEntityAtLocation(info.world, info.x, info.y, info.z);
      return var8 == null ? 0 : Block.blocksList[var8.getStoredBlockID()].dropBlockAsEntityItem(info.setMetadata(var8.getBlockMetadata()));
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      world.getBlockTileEntity(x, y, z);
      return false;
   }

   public static TileEntity getTileEntity(int par0, int par1, int par2, boolean par3, boolean par4) {
      return new TileEntityPiston(par0, par1, par2, par3, par4);
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      TileEntityPiston tile_entity = this.getTileEntityAtLocation(world, x, y, z);
      if (tile_entity == null) {
         return null;
      } else {
         float progress = tile_entity.getProgress(0.0F);
         if (tile_entity.isExtending()) {
            progress = 1.0F - progress;
         }

         return this.getAxisAlignedBB(world, x, y, z, tile_entity.getStoredBlockID(), progress, tile_entity.getPistonOrientation());
      }
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      TileEntityPiston var5 = this.getTileEntityAtLocation(par1IBlockAccess, par2, par3, par4);
      if (var5 != null) {
         Block var6 = Block.blocksList[var5.getStoredBlockID()];
         if (var6 == null || var6 == this) {
            return;
         }

         var6.setBlockBoundsBasedOnStateAndNeighbors(par1IBlockAccess, par2, par3, par4);
         float var7 = var5.getProgress(0.0F);
         if (var5.isExtending()) {
            var7 = 1.0F - var7;
         }

         if (var7 < 1.0F) {
            var7 = 0.0F;
         }

         int index = Minecraft.getThreadIndex();
         int var8 = var5.getPistonOrientation();
         this.minX[index] = var6.getBlockBoundsMinX(index) - (double)((float)Facing.offsetsXForSide[var8] * var7);
         this.minY[index] = var6.getBlockBoundsMinY(index) - (double)((float)Facing.offsetsYForSide[var8] * var7);
         this.minZ[index] = var6.getBlockBoundsMinZ(index) - (double)((float)Facing.offsetsZForSide[var8] * var7);
         this.maxX[index] = var6.getBlockBoundsMaxX(index) - (double)((float)Facing.offsetsXForSide[var8] * var7);
         this.maxY[index] = var6.getBlockBoundsMaxY(index) - (double)((float)Facing.offsetsYForSide[var8] * var7);
         this.maxZ[index] = var6.getBlockBoundsMaxZ(index) - (double)((float)Facing.offsetsZForSide[var8] * var7);
      }

   }

   public AxisAlignedBB getAxisAlignedBB(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {
      if (par5 != 0 && par5 != this.blockID) {
         AxisAlignedBB var8 = Block.blocksList[par5].getCollisionBoundsCombined(par1World, par2, par3, par4, (Entity)null, true);
         if (var8 == null) {
            return null;
         } else {
            var8 = getBoundingBoxFromPool(par2, par3, par4, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            EnumDirection direction = this.getDirectionFacing(par1World.getBlockMetadata(par2, par3, par4));
            double difference = (double)par6 * 0.75;
            if (direction.isUp()) {
               var8.maxY -= difference;
            } else if (direction.isDown()) {
               var8.minY += difference;
            } else if (direction.isSouth()) {
               var8.maxZ -= difference;
            } else if (direction.isNorth()) {
               var8.minZ += difference;
            } else if (direction.isEast()) {
               var8.maxX -= difference;
            } else if (direction.isWest()) {
               var8.minX += difference;
            }

            return var8;
         }
      } else {
         return null;
      }
   }

   private TileEntityPiston getTileEntityAtLocation(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      TileEntity var5 = par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
      return var5 instanceof TileEntityPiston ? (TileEntityPiston)var5 : null;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return 0;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("piston_top_normal");
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return "moving";
   }

   public boolean isFaceFlatAndSolid(int metadata, EnumFace face) {
      return face == this.getBackFace(metadata);
   }

   public final EnumDirection getDirectionFacing(int metadata) {
      return pistonBase.getDirectionFacing(metadata);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      return pistonBase.getMetadataForDirectionFacing(metadata, direction);
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
