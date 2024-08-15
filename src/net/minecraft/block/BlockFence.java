package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPudding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemLeash;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFace;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFence extends Block {
   private final String field_94464_a;

   public BlockFence(int par1, String par2Str, Material par3Material) {
      super(par1, par3Material, (new BlockConstants()).setNeverHidesAdjacentFaces().setAlwaysConnectsWithFence());
      this.field_94464_a = par2Str;
      this.setMaxStackSize(8);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      AxisAlignedBB[] bb;
      int i;
      if (entity instanceof EntityPudding) {
         bb = (AxisAlignedBB[])((AxisAlignedBB[])this.getCollisionBounds(world, x, y, z, (Entity)null));

         for(i = 0; i < bb.length; ++i) {
            if (bb[i] != null) {
               bb[i].setMaxY((double)y + 1.5);
            }
         }

         return bb;
      } else if (this.useFullBlockForCollisions(entity)) {
         return AxisAlignedBB.getBoundingBoxFromPool(x, y, z, 0.0, 0.0, 0.0, 1.0, 1.5, 1.0);
      } else {
         bb = new AxisAlignedBB[3];
         i = 0;
         boolean var8 = this.canConnectFenceTo(world, x, y, z - 1);
         boolean var9 = this.canConnectFenceTo(world, x, y, z + 1);
         boolean var10 = this.canConnectFenceTo(world, x - 1, y, z);
         boolean var11 = this.canConnectFenceTo(world, x + 1, y, z);
         float min_x = var10 ? 0.0F : 0.375F;
         float max_x = var11 ? 1.0F : 0.625F;
         float min_z = var8 ? 0.0F : 0.375F;
         float max_z = var9 ? 1.0F : 0.625F;
         if (!var8 && !var9 && !var10 && !var11) {
            bb[i++] = AxisAlignedBB.getBoundingBoxFromPool((double)min_x, 0.0, (double)min_z, (double)max_x, 1.0, (double)max_z);
         } else {
            if (var8 || var9) {
               bb[i++] = AxisAlignedBB.getBoundingBoxFromPool(0.375, 0.0, (double)min_z, 0.625, 1.0, (double)max_z);
            }

            if (var10 || var11) {
               bb[i++] = AxisAlignedBB.getBoundingBoxFromPool((double)min_x, 0.0, 0.375, (double)max_x, 1.0, 0.625);
            }
         }

         return bb;
      }
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      boolean var5 = this.canConnectFenceTo(par1IBlockAccess, par2, par3, par4 - 1);
      boolean var6 = this.canConnectFenceTo(par1IBlockAccess, par2, par3, par4 + 1);
      boolean var7 = this.canConnectFenceTo(par1IBlockAccess, par2 - 1, par3, par4);
      boolean var8 = this.canConnectFenceTo(par1IBlockAccess, par2 + 1, par3, par4);
      float var9 = 0.375F;
      float var10 = 0.625F;
      float var11 = 0.375F;
      float var12 = 0.625F;
      if (var5) {
         var11 = 0.0F;
      }

      if (var6) {
         var12 = 1.0F;
      }

      if (var7) {
         var9 = 0.0F;
      }

      if (var8) {
         var10 = 1.0F;
      }

      this.setBlockBoundsForCurrentThread((double)var9, 0.0, (double)var11, (double)var10, 1.0, (double)var12);
   }

   public int getRenderType() {
      return 11;
   }

   public boolean canConnectFenceTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return Block.doesBlockConnectWithFence(par1IBlockAccess, par2, par3, par4);
   }

   public static boolean isIdAFence(int par0) {
      return par0 == Block.fence.blockID || par0 == Block.netherFence.blockID;
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return true;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.field_94464_a);
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return player.canPlayerEdit(x, y, z, player.getHeldItemStack()) && ItemLeash.tryTieingLeashedEntitiesToBlock(player, x, y, z);
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }
}
