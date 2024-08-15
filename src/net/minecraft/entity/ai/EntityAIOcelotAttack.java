package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityAIOcelotAttack extends EntityAIBase {
   World theWorld;
   EntityLiving theEntity;
   EntityLivingBase theVictim;
   int attackCountdown;

   public EntityAIOcelotAttack(EntityLiving par1EntityLiving) {
      this.theEntity = par1EntityLiving;
      this.theWorld = par1EntityLiving.worldObj;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      EntityLivingBase var1 = this.theEntity.getAttackTarget();
      if (var1 == null) {
         return false;
      } else {
         this.theVictim = var1;
         return true;
      }
   }

   public boolean continueExecuting() {
      return !this.theVictim.isEntityAlive() ? false : (this.theEntity.getDistanceSqToEntity(this.theVictim) > 225.0 ? false : !this.theEntity.getNavigator().noPath() || this.shouldExecute());
   }

   public void resetTask() {
      this.theVictim = null;
      this.theEntity.getNavigator().clearPathEntity();
   }

   public void updateTask() {
      this.theEntity.getLookHelper().setLookPositionWithEntity(this.theVictim, 30.0F, 30.0F);
      double var1 = (double)(this.theEntity.width * 2.0F * this.theEntity.width * 2.0F);
      double var3 = this.theEntity.getDistanceSq(this.theVictim.posX, this.theVictim.boundingBox.minY, this.theVictim.posZ);
      double var5 = 0.8;
      if (var3 > var1 && var3 < 16.0) {
         var5 = 1.33;
      } else if (var3 < 225.0) {
         var5 = 0.6;
      }

      this.theEntity.getNavigator().tryMoveToEntityLiving(this.theVictim, var5);
      this.attackCountdown = Math.max(this.attackCountdown - 1, 0);
      if (var3 <= var1 && this.attackCountdown <= 0) {
         this.attackCountdown = 20;
         this.theEntity.attackEntityAsMob(this.theVictim);
      }

   }
}
