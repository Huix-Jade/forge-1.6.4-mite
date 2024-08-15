package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityWoodSpider;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFace;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockLeavesBase extends Block {
   protected boolean graphicsLevel;

   protected BlockLeavesBase(int par1, Material par2Material, boolean par3) {
      super(par1, par2Material, new BlockConstants());
      this.graphicsLevel = par3;
   }

   public final boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
      return !this.graphicsLevel && var6 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return item_stack.getItemSubtype() | 4;
   }

   public final void addCollidingBoundsToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity entity) {
      if (this.canCollideWithEntity(entity)) {
         if (!(entity instanceof EntityWoodSpider) || !((double)((float)par3 + 0.99F) > entity.posY)) {
            super.addCollidingBoundsToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, entity);
         }
      }
   }

   public boolean canCollideWithEntity(Entity entity) {
      return !(entity instanceof EntityItem) && !(entity instanceof EntityDiggingFX) && !(entity instanceof EntityBreakingFX);
   }

   public boolean canBePathedInto(World world, int x, int y, int z, Entity entity, boolean allow_closed_wooden_portals) {
      return entity instanceof EntityWoodSpider && (double)y + 1.0 > entity.posY;
   }
}
