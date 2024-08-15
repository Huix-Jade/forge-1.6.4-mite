package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityTameable;

public class EntityAITargetNonTamed extends EntityAINearestAttackableTarget {
   private EntityTameable theTameable;

   public EntityAITargetNonTamed(EntityTameable par1EntityTameable, Class par2Class, int par3, boolean par4) {
      super(par1EntityTameable, par2Class, par3, par4);
      this.theTameable = par1EntityTameable;
   }

   public boolean shouldExecute() {
      if (!this.theTameable.canEat()) {
         return false;
      } else {
         return !this.theTameable.isTamed() && super.shouldExecute();
      }
   }

   public boolean continueExecuting() {
      return !this.theTameable.canEat() ? false : super.continueExecuting();
   }
}
