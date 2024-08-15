package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.world.World;

public class EntityWitherSkull extends EntityFireball {
   public EntityWitherSkull(World par1World) {
      super(par1World);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntityWitherSkull(World par1World, EntityLivingBase par2EntityLivingBase, double par3, double par5, double par7) {
      super(par1World, par2EntityLivingBase, par3, par5, par7);
      this.setSize(0.3125F, 0.3125F);
   }

   protected float getMotionFactor() {
      return this.isInvulnerable() ? 0.73F : super.getMotionFactor();
   }

   public EntityWitherSkull(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
      super(par1World, par2, par4, par6, par8, par10, par12);
      this.setSize(0.3125F, 0.3125F);
   }

   public boolean isBurning() {
      return false;
   }

   protected void onImpact(RaycastCollision rc) {
      if (!this.worldObj.isRemote) {
         if (rc.isEntity()) {
            Entity entity_hit = rc.getEntityHit();
            if (this.shootingEntity != null) {
               EntityDamageResult result = entity_hit.attackEntityFrom(new Damage(DamageSource.causeMobDamage(this.shootingEntity), 8.0F));
               if (result != null && result.entityWasDestroyed()) {
                  this.shootingEntity.heal(5.0F, EnumEntityFX.vampiric_gain);
               }
            } else {
               entity_hit.attackEntityFrom(new Damage(DamageSource.magic, 5.0F));
            }

            if (entity_hit instanceof EntityLivingBase) {
               byte var2 = 0;
               if (this.worldObj.difficultySetting > 1) {
                  if (this.worldObj.difficultySetting == 2) {
                     var2 = 10;
                  } else if (this.worldObj.difficultySetting == 3) {
                     var2 = 40;
                  }
               }

               if (var2 > 0) {
                  ((EntityLivingBase)entity_hit).addPotionEffect(new PotionEffect(Potion.wither.id, 20 * var2, 1));
               }
            }
         }

         this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, 1.0F, 1.0F, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
         this.setDead();
      }

   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      return null;
   }

   protected void entityInit() {
      this.dataWatcher.addObject(10, (byte)0);
   }

   public boolean isInvulnerable() {
      return this.dataWatcher.getWatchableObjectByte(10) == 1;
   }

   public void setInvulnerable(boolean par1) {
      this.dataWatcher.updateObject(10, (byte)(par1 ? 1 : 0));
   }

   public void setCollisionPolicies(Raycast raycast) {
      raycast.setForBluntProjectile(this);
   }
}
