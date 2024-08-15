package net.minecraft.block;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPudding;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWall extends Block {
   public static final String[] types = new String[]{"normal", "mossy"};

   public BlockWall(int par1, Block par2Block) {
      super(par1, par2Block.blockMaterial, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.setHardness(par2Block.getBlockHardness(0) * 6.0F / 8.0F);
      this.setStepSound(par2Block.stepSound);
      this.setMaxStackSize(8);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public Icon getIcon(int par1, int par2) {
      return par2 == 1 ? Block.cobblestoneMossy.getBlockTextureFromSide(par1) : Block.cobblestone.getBlockTextureFromSide(par1);
   }

   public int getRenderType() {
      return 32;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      boolean var5 = this.canConnectWallTo(par1IBlockAccess, par2, par3, par4 - 1);
      boolean var6 = this.canConnectWallTo(par1IBlockAccess, par2, par3, par4 + 1);
      boolean var7 = this.canConnectWallTo(par1IBlockAccess, par2 - 1, par3, par4);
      boolean var8 = this.canConnectWallTo(par1IBlockAccess, par2 + 1, par3, par4);
      float var9 = 0.25F;
      float var10 = 0.75F;
      float var11 = 0.25F;
      float var12 = 0.75F;
      float var13 = 1.0F;
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

      if (var5 && var6 && !var7 && !var8) {
         var13 = 0.8125F;
         var9 = 0.3125F;
         var10 = 0.6875F;
      } else if (!var5 && !var6 && var7 && var8) {
         var13 = 0.8125F;
         var11 = 0.3125F;
         var12 = 0.6875F;
      }

      this.setBlockBoundsForCurrentThread((double)var9, 0.0, (double)var11, (double)var10, (double)var13, (double)var12);
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      if (entity instanceof EntityPudding) {
         AxisAlignedBB bounds = (AxisAlignedBB)this.getCollisionBounds(world, x, y, z, (Entity)null);
         return bounds.setMaxY((double)y + 1.5);
      } else {
         return this.useFullBlockForCollisions(entity) ? AxisAlignedBB.getBoundingBoxFromPool(x, y, z, 0.0, 0.0, 0.0, 1.0, 1.5, 1.0) : super.getCollisionBounds(world, x, y, z, entity);
      }
   }

   public boolean canConnectWallTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      Block block = par1IBlockAccess.getBlock(par2, par3, par4);
      if (block != null && !(block instanceof BlockFence)) {
         return block == this || block.connectsWithFence();
      } else {
         return false;
      }
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 2;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata & 1;
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return par5 == 0 ? super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5) : true;
   }

   public void registerIcons(IconRegister par1IconRegister) {
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }
}
