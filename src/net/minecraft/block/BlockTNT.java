package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.Icon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends BlockFalling {
   private Icon field_94393_a;
   private Icon field_94392_b;

   public BlockTNT(int par1) {
      super(par1, Material.tnt, (new BlockConstants()).setNeverConnectsWithFence());
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 0 ? this.field_94392_b : (par1 == 1 ? this.field_94393_a : this.blockIcon);
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      super.onBlockAdded(par1World, par2, par3, par4);
      if (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
         ignite(par1World, par2, par3, par4, (EntityLivingBase)null);
      }

   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
         ignite(world, x, y, z, (EntityLivingBase)null);
         return true;
      } else {
         return false;
      }
   }

   public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion) {
      if (!par1World.isRemote) {
         EntityTNTPrimed var6 = new EntityTNTPrimed(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), par5Explosion.getExplosivePlacedBy());
         var6.fuse = par1World.rand.nextInt(var6.fuse / 4) + var6.fuse / 8;
         par1World.spawnEntityInWorld(var6);
      }

   }

   public static void primeTnt(World par1World, int par2, int par3, int par4, int par5, EntityLivingBase par6EntityLivingBase) {
      if (!par1World.isRemote && (par5 & 1) == 1) {
         EntityTNTPrimed var7 = new EntityTNTPrimed(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), par6EntityLivingBase);
         par1World.spawnEntityInWorld(var7);
         par1World.playSoundAtEntity(var7, "random.fuse", 1.0F, 1.0F);
      }

   }

   public static void ignite(World world, int x, int y, int z, EntityLivingBase igniter) {
      if (!world.isRemote) {
         primeTnt(world, x, y, z, 1, igniter);
         world.setBlockToAir(x, y, z);
      }

   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      if (par5Entity instanceof EntityArrow && !par1World.isRemote) {
         EntityArrow var6 = (EntityArrow)par5Entity;
         if (var6.isBurning()) {
            primeTnt(par1World, par2, par3, par4, 1, var6.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase)var6.shootingEntity : null);
            par1World.setBlockToAir(par2, par3, par4);
         }
      }

   }

   public boolean canDropFromExplosion(Explosion par1Explosion) {
      return false;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
      this.field_94393_a = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.field_94392_b = par1IconRegister.registerIcon(this.getTextureName() + "_bottom");
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.getResponsiblePlayer() != null) {
         primeTnt(info.world, info.x, info.y, info.z, info.getMetadata(), info.getResponsiblePlayer());
      }

      return super.dropBlockAsEntityItem(info);
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return true;
   }
}
