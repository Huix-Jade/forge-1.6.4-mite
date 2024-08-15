package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySmallFireball extends EntityFireball {
   public EntitySmallFireball(World par1World) {
      super(par1World);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World par1World, EntityLivingBase par2EntityLivingBase, double par3, double par5, double par7) {
      super(par1World, par2EntityLivingBase, par3, par5, par7);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
      super(par1World, par2, par4, par6, par8, par10, par12);
      this.setSize(0.3125F, 0.3125F);
   }

   protected void onImpact(RaycastCollision rc) {
      if (!this.worldObj.isRemote) {
         if (rc.isEntity()) {
            rc.getEntityHit().attackEntityFrom(new Damage(DamageSource.causeFireballDamage(this, this.shootingEntity), 2.0F));
            rc.getEntityHit().setFire(5);
         } else if (rc.isNeighborAirBlock()) {
            rc.setNeighborBlock(Block.fire);
         }

         this.setDead();
      }

   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      return null;
   }

   public void setCollisionPolicies(Raycast raycast) {
      raycast.setForPiercingProjectile(this);
   }
}
