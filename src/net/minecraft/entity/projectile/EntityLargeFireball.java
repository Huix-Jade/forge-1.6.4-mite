package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityLargeFireball extends EntityFireball {
   public int field_92057_e = 1;

   public EntityLargeFireball(World par1World) {
      super(par1World);
   }

   public EntityLargeFireball(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
      super(par1World, par2, par4, par6, par8, par10, par12);
   }

   public EntityLargeFireball(World par1World, EntityLivingBase par2EntityLivingBase, double par3, double par5, double par7) {
      super(par1World, par2EntityLivingBase, par3, par5, par7);
   }

   public EntityLargeFireball(World world, EntityLivingBase shooter, Vec3 target, float initial_distance) {
      super(world, shooter, target, initial_distance);
   }

   public EntityLargeFireball(World world, EntityLivingBase shooter, Vec3 origin, Vec3 target, float initial_distance) {
      super(world, shooter, origin, target, initial_distance);
   }

   protected void onImpact(RaycastCollision rc) {
      if (!this.worldObj.isRemote) {
         if (rc.isEntity()) {
            rc.getEntityHit().attackEntityFrom(new Damage(DamageSource.causeFireballDamage(this, this.shootingEntity), 6.0F));
         }

         this.worldObj.newExplosion((Entity)null, this.posX, this.posY, this.posZ, (float)this.field_92057_e, (float)this.field_92057_e, true, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
         this.setDead();
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("ExplosionPower", this.field_92057_e);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("ExplosionPower")) {
         this.field_92057_e = par1NBTTagCompound.getInteger("ExplosionPower");
      }

   }

   public void setCollisionPolicies(Raycast raycast) {
      raycast.setForBluntProjectile(this);
   }
}
