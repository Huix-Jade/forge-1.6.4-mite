package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;

public class EntityAIFollowOwner extends EntityAIBase {
   private EntityTameable thePet;
   private EntityLivingBase theOwner;
   World theWorld;
   private double field_75336_f;
   private PathNavigate petPathfinder;
   private int field_75343_h;
   float maxDist;
   float minDist;
   private boolean field_75344_i;

   public EntityAIFollowOwner(EntityTameable par1EntityTameable, double par2, float par4, float par5) {
      this.thePet = par1EntityTameable;
      this.theWorld = par1EntityTameable.worldObj;
      this.field_75336_f = par2;
      this.petPathfinder = par1EntityTameable.getNavigator();
      this.minDist = par4;
      this.maxDist = par5;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      EntityLivingBase var1 = this.thePet.func_130012_q();
      if (var1 == null) {
         return false;
      } else if (this.thePet.isSitting()) {
         return false;
      } else if (this.thePet.getDistanceSqToEntity(var1) < (double)(this.minDist * this.minDist)) {
         return false;
      } else {
         this.theOwner = var1;
         return true;
      }
   }

   public boolean continueExecuting() {
      return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > (double)(this.maxDist * this.maxDist) && !this.thePet.isSitting();
   }

   public void startExecuting() {
      this.field_75343_h = 0;
      this.field_75344_i = this.thePet.getNavigator().getAvoidsWater();
      this.thePet.getNavigator().setAvoidsWater(false);
   }

   public void resetTask() {
      this.theOwner = null;
      this.petPathfinder.clearPathEntity();
      this.thePet.getNavigator().setAvoidsWater(this.field_75344_i);
   }

   public void updateTask() {
      this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, (float)this.thePet.getVerticalFaceSpeed());
      if (!this.thePet.isSitting() && --this.field_75343_h <= 0) {
         this.field_75343_h = 10;
         if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.field_75336_f) && !this.thePet.getLeashed() && this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0 && this.thePet.getTicksExistedWithOffset() % 10 == 0 && Math.random() < 0.20000000298023224) {
            this.thePet.callToOwner();
         }
      }

   }
}
