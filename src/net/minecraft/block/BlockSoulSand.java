package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;

public final class BlockSoulSand extends BlockUnderminable {
   public BlockSoulSand(int par1) {
      super(par1, Material.sand, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setCushioning(0.4F);
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      return !(entity instanceof EntityItem) && !(entity instanceof EntityFallingSand) ? getStandardFormBoundingBoxFromPool(x, y, z).addToMaxY(-0.125) : getStandardFormBoundingBoxFromPool(x, y, z);
   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      par5Entity.motionX *= 0.4;
      par5Entity.motionZ *= 0.4;
      super.onEntityCollidedWithBlock(par1World, par2, par3, par4, par5Entity);
   }
}
