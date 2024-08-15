package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFace;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockButton extends BlockMounted {
   private final boolean sensible;

   protected BlockButton(int par1, boolean par2) {
      super(par1, Material.circuits, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.sensible = par2;
   }

   public String getMetadataNotes() {
      String[] array = new String[4];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + 1 + "=Mounted " + this.getDirectionOfSupportBlock(i + 1).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false) + ", and bit 8 set if pressed";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata > 0 && metadata < 5 || metadata > 8 && metadata < 13;
   }

   public int tickRate(World par1World) {
      return 30;
   }

   public static boolean isMaterialSuitableForButtonMounting(Material material, EnumFace face) {
      return material != Material.dirt && material != Material.grass && material != Material.sand && !material.isSnow() && material != Material.tree_leaves && material != Material.pumpkin && material != Material.cloth && material != Material.glass && material != Material.sponge;
   }

   public boolean canMountToBlock(int metadata, Block neighbor_block, int neighbor_block_metadata, EnumFace face) {
      return !isMaterialSuitableForButtonMounting(neighbor_block.blockMaterial, face) ? false : super.canMountToBlock(metadata, neighbor_block, neighbor_block_metadata, face);
   }

   public EnumFace getFaceMountedTo(int metadata) {
      int orientation = metadata & 7;
      if (orientation == 1) {
         return EnumFace.EAST;
      } else if (orientation == 2) {
         return EnumFace.WEST;
      } else if (orientation == 3) {
         return EnumFace.SOUTH;
      } else if (orientation == 4) {
         return EnumFace.NORTH;
      } else {
         Minecraft.setErrorMessage("getFaceMountedTo: invalid orientation " + orientation);
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
      } else {
         return face.isNorth() ? 4 : -1;
      }
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      this.func_82534_e(var5);
   }

   private void func_82534_e(int par1) {
      int var2 = par1 & 7;
      boolean var3 = (par1 & 8) > 0;
      float var4 = 0.375F;
      float var5 = 0.625F;
      float var6 = 0.1875F;
      float var7 = 0.125F;
      if (var3) {
         var7 = 0.0625F;
      }

      if (var2 == 1) {
         this.setBlockBoundsForCurrentThread(0.0, (double)var4, (double)(0.5F - var6), (double)var7, (double)var5, (double)(0.5F + var6));
      } else if (var2 == 2) {
         this.setBlockBoundsForCurrentThread((double)(1.0F - var7), (double)var4, (double)(0.5F - var6), 1.0, (double)var5, (double)(0.5F + var6));
      } else if (var2 == 3) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - var6), (double)var4, 0.0, (double)(0.5F + var6), (double)var5, (double)var7);
      } else if (var2 == 4) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - var6), (double)var4, (double)(1.0F - var7), (double)(0.5F + var6), (double)var5, 1.0);
      }

   }

   public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
   }

   public static boolean isPressed(int metadata) {
      return (metadata & 8) != 0;
   }

   public static int getPressed(int metadata) {
      return metadata | 8;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      int metadata = world.getBlockMetadata(x, y, z);
      if (isPressed(metadata)) {
         player.cancelRightClick();
         return true;
      } else {
         if (player.onServer()) {
            world.setBlockMetadataWithNotify(x, y, z, getPressed(metadata), 3);
            world.playSoundAtBlock(x, y, z, "random.click", 0.3F, 0.6F);
            this.func_82536_d(world, x, y, z, metadata & 7);
            world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
         }

         return true;
      }
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      if ((par6 & 8) > 0) {
         int var7 = par6 & 7;
         this.func_82536_d(par1World, par2, par3, par4, var7);
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
         return var7 == 5 && par5 == 1 ? 15 : (var7 == 4 && par5 == 2 ? 15 : (var7 == 3 && par5 == 3 ? 15 : (var7 == 2 && par5 == 4 ? 15 : (var7 == 1 && par5 == 5 ? 15 : 0))));
      }
   }

   public boolean canProvidePower() {
      return true;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if (super.updateTick(world, x, y, z, random)) {
         return true;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         if ((metadata & 8) != 0) {
            if (this.sensible) {
               return this.func_82535_o(world, x, y, z);
            } else {
               world.setBlockMetadataWithNotify(x, y, z, metadata & 7, 3);
               int var7 = metadata & 7;
               this.func_82536_d(world, x, y, z, var7);
               world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.click", 0.3F, 0.5F);
               world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
               return true;
            }
         } else {
            return false;
         }
      }
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      float var1 = 0.1875F;
      float var2 = 0.125F;
      float var3 = 0.125F;
      this.setBlockBoundsForCurrentThread((double)(0.5F - var1), (double)(0.5F - var2), (double)(0.5F - var3), (double)(0.5F + var1), (double)(0.5F + var2), (double)(0.5F + var3));
   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      if (!par1World.isRemote && this.sensible && (par1World.getBlockMetadata(par2, par3, par4) & 8) == 0) {
         this.func_82535_o(par1World, par2, par3, par4);
      }

   }

   private boolean func_82535_o(World par1World, int par2, int par3, int par4) {
      int var5 = par1World.getBlockMetadata(par2, par3, par4);
      int var6 = var5 & 7;
      boolean var7 = (var5 & 8) != 0;
      this.func_82534_e(var5);
      int index = Minecraft.getThreadIndex();
      List var9 = par1World.getEntitiesWithinAABB(EntityArrow.class, AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX[index], (double)par3 + this.minY[index], (double)par4 + this.minZ[index], (double)par2 + this.maxX[index], (double)par3 + this.maxY[index], (double)par4 + this.maxZ[index]));
      boolean var8 = !var9.isEmpty();
      boolean changed_state = false;
      if (var8 && !var7) {
         par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 | 8, 3);
         this.func_82536_d(par1World, par2, par3, par4, var6);
         par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
         par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.5, (double)par4 + 0.5, "random.click", 0.3F, 0.6F);
         changed_state = true;
      }

      if (!var8 && var7) {
         par1World.setBlockMetadataWithNotify(par2, par3, par4, var6, 3);
         this.func_82536_d(par1World, par2, par3, par4, var6);
         par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
         par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.5, (double)par4 + 0.5, "random.click", 0.3F, 0.5F);
         changed_state = true;
      }

      if (var8) {
         par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
      }

      return changed_state;
   }

   private void func_82536_d(World par1World, int par2, int par3, int par4, int par5) {
      par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
      if (par5 == 1) {
         par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
      } else if (par5 == 2) {
         par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
      } else if (par5 == 3) {
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
      } else if (par5 == 4) {
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
      } else {
         par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
      }

   }

   public void registerIcons(IconRegister par1IconRegister) {
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }
}
