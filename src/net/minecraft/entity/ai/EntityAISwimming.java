package net.minecraft.entity.ai;

import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityAISwimming extends EntityAIBase {
   private EntityLiving theEntity;

   public EntityAISwimming(EntityLiving par1EntityLiving) {
      this.theEntity = par1EntityLiving;
      this.setMutexBits(4);
      par1EntityLiving.getNavigator().setCanSwim(true);
   }

   private boolean isTheEntityDeepInLava() {
      return this.theEntity.worldObj.getBlockMaterial(this.theEntity.getBlockPosX(), this.theEntity.getFootBlockPosY() + 1, this.theEntity.getBlockPosZ()) == Material.lava;
   }

   private boolean isEntityBuoyant() {
      if (this.theEntity.isCollidedHorizontally) {
         return true;
      } else if (this.theEntity.isPreventedFromSwimmingDueToParalyzation()) {
         return false;
      } else if (this.theEntity instanceof EntityEarthElemental) {
         EntityLivingBase target = this.theEntity.getTarget();
         return target != null && !(target.getHealth() <= 0.0F) ? this.isTheEntityDeepInLava() : false;
      } else {
         return true;
      }
   }

   public boolean shouldExecute() {
      if (!this.isEntityBuoyant()) {
         return false;
      } else if (this.theEntity.isInWater() && !this.theEntity.isInsideOfMaterial(Material.water, -this.theEntity.getEyeHeight()) && !BlockFluid.isFullWaterBlock(this.theEntity.worldObj, this.theEntity.getBlockPosX(), this.theEntity.getFootBlockPosY(), this.theEntity.getBlockPosZ(), true)) {
         return this.theEntity.handleLavaMovement();
      } else {
         return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
      }
   }

   public void updateTask() {
      if (this.isEntityBuoyant()) {
         if (this.theEntity.worldObj.getBlockMaterial(MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY + (double)this.theEntity.getEyeHeight() + 0.5), MathHelper.floor_double(this.theEntity.posZ)) == Material.water) {
            this.theEntity.motionY = 0.10000000149011612;
         } else {
            if (this.theEntity.getRNG().nextFloat() < 0.8F) {
               this.theEntity.getJumpHelper().setJumping();
            }

         }
      }
   }

   public boolean continueExecuting() {
      return !this.isEntityBuoyant() ? false : super.continueExecuting();
   }
}
