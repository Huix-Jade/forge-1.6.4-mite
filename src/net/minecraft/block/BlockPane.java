package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockPane extends Block {
   private final String sideTextureIndex;
   private final boolean canDropItself;
   private final String field_94402_c;
   private Icon theIcon;

   protected BlockPane(int par1, String par2Str, String par3Str, Material par4Material, boolean par5) {
      super(par1, par4Material, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.sideTextureIndex = par3Str;
      this.canDropItself = par5;
      this.field_94402_c = par2Str;
      this.setMaxStackSize(16);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (this == Block.thinGlass) {
         return this.dropBlockAsEntityItem(info, Item.shardGlass);
      } else {
         return this.canDropItself ? super.dropBlockAsEntityItem(info) : 0;
      }
   }

   public int getRenderType() {
      return 18;
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
      return var6 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      if (this.useFullBlockForCollisions(entity)) {
         return getStandardFormBoundingBoxFromPool(x, y, z);
      } else {
         AxisAlignedBB[] bb = new AxisAlignedBB[3];
         int index = 0;
         boolean var8 = this.canPaneConnectTo(world,x, y, z, ForgeDirection.NORTH);
         boolean var9 = this.canPaneConnectTo(world,x, y, z, ForgeDirection.SOUTH);
         boolean var10 = this.canPaneConnectTo(world,x, y, z, ForgeDirection.WEST);
         boolean var11 = this.canPaneConnectTo(world,x, y, z, ForgeDirection.EAST);
         if (var10 && var11 || !var10 && !var11 && !var8 && !var9) {
            bb[index++] = AxisAlignedBB.getBoundingBoxFromPool(0.0, 0.0, 0.4375, 1.0, 1.0, 0.5625);
         } else if (var10 && !var11) {
            bb[index++] = AxisAlignedBB.getBoundingBoxFromPool(0.0, 0.0, 0.4375, 0.5, 1.0, 0.5625);
         } else if (!var10 && var11) {
            bb[index++] = AxisAlignedBB.getBoundingBoxFromPool(0.5, 0.0, 0.4375, 1.0, 1.0, 0.5625);
         }

         if (var8 && var9 || !var10 && !var11 && !var8 && !var9) {
            bb[index++] = AxisAlignedBB.getBoundingBoxFromPool(0.4375, 0.0, 0.0, 0.5625, 1.0, 1.0);
         } else if (var8 && !var9) {
            bb[index++] = AxisAlignedBB.getBoundingBoxFromPool(0.4375, 0.0, 0.0, 0.5625, 1.0, 0.5);
         } else if (!var8 && var9) {
            bb[index++] = AxisAlignedBB.getBoundingBoxFromPool(0.4375, 0.0, 0.5, 0.5625, 1.0, 1.0);
         }

         return bb;
      }
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      float var5 = 0.4375F;
      float var6 = 0.5625F;
      float var7 = 0.4375F;
      float var8 = 0.5625F;
      boolean var9 = this.canPaneConnectTo(par1IBlockAccess,par2, par3, par4,ForgeDirection.NORTH);
      boolean var10 = this.canPaneConnectTo(par1IBlockAccess,par2, par3, par4,ForgeDirection.SOUTH);
      boolean var11 = this.canPaneConnectTo(par1IBlockAccess,par2, par3, par4,ForgeDirection.WEST);
      boolean var12 = this.canPaneConnectTo(par1IBlockAccess,par2, par3, par4,ForgeDirection.EAST);
      if (var11 && var12 || !var11 && !var12 && !var9 && !var10) {
         var5 = 0.0F;
         var6 = 1.0F;
      } else if (var11 && !var12) {
         var5 = 0.0F;
      } else if (!var11 && var12) {
         var6 = 1.0F;
      }

      if (var9 && var10 || !var11 && !var12 && !var9 && !var10) {
         var7 = 0.0F;
         var8 = 1.0F;
      } else if (var9 && !var10) {
         var7 = 0.0F;
      } else if (!var9 && var10) {
         var8 = 1.0F;
      }

      this.setBlockBoundsForCurrentThread((double)var5, 0.0, (double)var7, (double)var6, 1.0, (double)var8);
   }

   public Icon getSideTextureIndex() {
      return this.theIcon;
   }

   public final boolean canThisPaneConnectToThisBlockID(int par1) {
      return Block.opaqueCubeLookup[par1] || par1 == this.blockID || par1 == Block.glass.blockID;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.field_94402_c);
      this.theIcon = par1IconRegister.registerIcon(this.sideTextureIndex);
   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      if (!par1World.isRemote && this == thinGlass && par5Entity instanceof EntityArrow) {
         EntityArrow arrow = (EntityArrow)par5Entity;
          if (arrow.speed_before_collision_sq > 4.0F) {
            par1World.destroyBlock(new BlockBreakInfo(par1World, par2, par3, par4), true);
         }
      }

   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean canPaneConnectTo(IBlockAccess access, int x, int y, int z, ForgeDirection dir)
   {
      return canThisPaneConnectToThisBlockID(access.getBlockId(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ))
              || access.isBlockSolid(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
   }
}
