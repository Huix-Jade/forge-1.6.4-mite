package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.Item;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class EntityEgg extends EntityThrowable {
   public EntityEgg(World par1World) {
      super(par1World);
   }

   public EntityEgg(World par1World, EntityLivingBase par2EntityLivingBase) {
      super(par1World, par2EntityLivingBase);
   }

   public EntityEgg(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
   }

   protected void onImpact(RaycastCollision rc) {
      if (rc.isEntity()) {
         rc.getEntityHit().attackEntityFrom(new Damage(DamageSource.causeThrownDamage(this, this.getThrower()), 1.0F));
      }

      int var2;
      if (!this.worldObj.isRemote && this.rand.nextInt(8) == 0) {
         var2 = 1;
         if (this.rand.nextInt(32) == 0) {
            var2 = 4;
         }

         for(int var3 = 0; var3 < var2; ++var3) {
            EntityChicken var4 = new EntityChicken(this.worldObj);
            var4.setGrowingAgeToNewborn();
            var4.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            this.worldObj.spawnEntityInWorld(var4);
         }
      }

      for(var2 = 0; var2 < 8; ++var2) {
         this.worldObj.spawnParticle(EnumParticle.snowballpoof, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
      }

      if (!this.worldObj.isRemote) {
         this.setDead();
      }

   }

   public Item getModelItem() {
      return Item.egg;
   }
}
