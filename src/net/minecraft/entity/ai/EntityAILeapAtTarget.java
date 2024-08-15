package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityAILeapAtTarget extends EntityAIBase {
   EntityLiving leaper;
   EntityLivingBase leapTarget;
   float leapMotionY;

   public EntityAILeapAtTarget(EntityLiving par1EntityLiving, float par2) {
      this.leaper = par1EntityLiving;
      this.leapMotionY = par2;
      this.setMutexBits(5);
   }

   public boolean shouldExecute() {
      this.leapTarget = this.leaper.getAttackTarget();
      if (this.leapTarget == null) {
         return false;
      } else {
         double var1 = this.leaper.getDistanceSqToEntity(this.leapTarget);
         return var1 >= 4.0 && var1 <= 16.0 ? (!this.leaper.onGround ? false : this.leaper.getRNG().nextInt(5) == 0) : false;
      }
   }

   public boolean continueExecuting() {
      return !this.leaper.onGround;
   }

   public void startExecuting() {
      double var1 = this.leapTarget.posX - this.leaper.posX;
      double var3 = this.leapTarget.posZ - this.leaper.posZ;
      float var5 = MathHelper.sqrt_double(var1 * var1 + var3 * var3);
      EntityLiving var10000 = this.leaper;
      var10000.motionX += var1 / (double)var5 * 0.5 * 0.800000011920929 + this.leaper.motionX * 0.20000000298023224;
      var10000 = this.leaper;
      var10000.motionZ += var3 / (double)var5 * 0.5 * 0.800000011920929 + this.leaper.motionZ * 0.20000000298023224;
      this.leaper.motionY = (double)this.leapMotionY;
      this.leaper.rotationYaw = (float)MathHelper.getYawInDegrees(this.leaper.posX, this.leaper.posZ, this.leapTarget.posX, this.leapTarget.posZ);
   }
}
