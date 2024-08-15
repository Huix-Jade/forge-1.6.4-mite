package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
   EntityTameable theEntityTameable;
   EntityLivingBase theTarget;
   private int field_142050_e;

   public EntityAIOwnerHurtTarget(EntityTameable par1EntityTameable) {
      super(par1EntityTameable, false);
      this.theEntityTameable = par1EntityTameable;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (!this.theEntityTameable.isTamed()) {
         return false;
      } else {
         EntityLivingBase var1 = this.theEntityTameable.func_130012_q();
         if (var1 == null) {
            return false;
         } else {
            this.theTarget = var1.getLastAttackTarget();
            int var2 = var1.getLastAttackTime();
            return var2 != this.field_142050_e && this.isSuitableTarget(this.theTarget, false) && this.theEntityTameable.func_142018_a(this.theTarget, var1);
         }
      }
   }

   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.theTarget);
      EntityLivingBase var1 = this.theEntityTameable.func_130012_q();
      if (var1 != null) {
         this.field_142050_e = var1.getLastAttackTime();
      }

      super.startExecuting();
   }
}
