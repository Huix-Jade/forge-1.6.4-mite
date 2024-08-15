package net.minecraft.entity.passive;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityWaterMob extends EntityCreature implements IAnimals {
   public EntityWaterMob(World par1World) {
      super(par1World);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      return this.worldObj.checkNoEntityCollision(this.boundingBox);
   }

   public int getTalkInterval() {
      return 120;
   }

   protected boolean canDespawn() {
      return super.canDespawn();
   }

   public void onEntityUpdate() {
      int var1 = this.getAir();
      super.onEntityUpdate();
      if (this.isEntityAlive() && !this.isInWater()) {
         --var1;
         this.setAir(var1);
         if (this.getAir() == -20) {
            this.setAir(0);
            if (this.onServer()) {
               this.attackEntityFrom(new Damage(DamageSource.reverse_drown, 2.0F));
            }
         }
      } else {
         this.setAir(300);
      }

   }
}
