package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAnimalWatcher;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityHellhound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIAttackOnCollide extends EntityAIBase {
   World worldObj;
   EntityCreature attacker;
   public int attackTick;
   double speedTowardsTarget;
   boolean longMemory;
   PathEntity entityPathEntity;
   Class classTarget;
   private int field_75445_i;
   public int ticks_suppressed;

   public EntityAIAttackOnCollide(EntityCreature par1EntityCreature, Class par2Class, double par3, boolean par5) {
      this(par1EntityCreature, par3, par5);
      this.classTarget = par2Class;
   }

   public EntityAIAttackOnCollide(EntityCreature par1EntityCreature, double par2, boolean par4) {
      this.attacker = par1EntityCreature;
      this.worldObj = par1EntityCreature.worldObj;
      this.speedTowardsTarget = par2;
      this.longMemory = par4;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      EntityLivingBase var1 = this.attacker.getAttackTarget();
      if (var1 == null) {
         return false;
      } else if (this.classTarget == EntityAnimal.class && var1 instanceof EntityHellhound) {
         return false;
      } else if (!var1.isEntityAlive()) {
         return false;
      } else if (this.classTarget != null && !this.classTarget.isAssignableFrom(var1.getClass())) {
         return false;
      } else if (this.attacker.worldObj.getEntitiesWithinAABB(EntityFallingSand.class, this.attacker.boundingBox.expand(1.0, 1.0, 1.0).translate(0.0, 1.0, 0.0)).size() > 0) {
         if (this.ticks_suppressed < 10) {
            this.ticks_suppressed = 10;
         }

         if (this.attackTick < 10) {
            this.attackTick = 10;
         }

         return false;
      } else {
         if (this.ticks_suppressed < 1 && this.attacker.getTicksExistedWithOffset() % 10 == 0 && this.attacker.getDistanceToEntity(var1) < 3.0F && !this.attacker.hasLineOfStrikeAndTargetIsWithinStrikingDistance(var1)) {
            Vec3 limit = var1.getCenterPoint();
            RaycastCollision rc = this.attacker.worldObj.getBlockCollisionForPhysicalReach(this.attacker.getFootPosPlusFractionOfHeight(0.75F), limit);
            if (rc == null) {
               rc = this.attacker.worldObj.getBlockCollisionForPhysicalReach(this.attacker.getFootPosPlusFractionOfHeight(0.25F), limit);
            }

            boolean edging_prevented = false;
            if (rc != null && rc.getBlockHit() == Block.cactus && this.attacker.canBeDamagedByCacti()) {
               edging_prevented = true;
            }

            if (!edging_prevented) {
               this.attacker.getMoveHelper().setMoveTo(var1.posX, var1.posY, var1.posZ, 2.0);
               this.attacker.getLookHelper().setLookPositionWithEntity(var1, 30.0F, 30.0F);
               this.attacker.getLookHelper().onUpdateLook();
               this.attacker.setRotation(this.attacker.rotationYawHead, this.attacker.rotationPitch);
            }
         }

         if (this.ticks_suppressed > 0) {
            --this.ticks_suppressed;
         }

         this.attackTick = Math.max(this.attackTick - 1, 0);
         this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(var1);
         if (this.attacker.hasLineOfStrikeAndTargetIsWithinStrikingDistance(var1)) {
            return true;
         } else {
            return this.entityPathEntity != null;
         }
      }
   }

   public boolean continueExecuting() {
      EntityLivingBase var1 = this.attacker.getAttackTarget();
      return var1 == null ? false : (!var1.isEntityAlive() ? false : (!this.longMemory ? !this.attacker.getNavigator().noPath() : this.attacker.func_110176_b(MathHelper.floor_double(var1.posX), MathHelper.floor_double(var1.posY), MathHelper.floor_double(var1.posZ))));
   }

   public void startExecuting() {
      this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
      this.field_75445_i = 0;
   }

   public void resetTask() {
      this.attacker.getNavigator().clearPathEntity();
   }

   public boolean canStrikeTargetNow() {
      if (this.ticks_suppressed <= 0 && this.attackTick <= 0) {
         EntityLivingBase target = this.attacker.getTarget();
         return target != null && this.attacker.hasLineOfStrikeAndTargetIsWithinStrikingDistance(target);
      } else {
         return false;
      }
   }

   public void updateTask() {
      if (this.ticks_suppressed > 0) {
         --this.ticks_suppressed;
      } else {
         EntityLivingBase var1 = this.attacker.getAttackTarget();
         if (var1 != null) {
            this.attacker.getLookHelper().setLookPositionWithEntity(var1, 30.0F, 30.0F);
            if ((this.longMemory || this.attacker.getEntitySenses().canSee(var1)) && --this.field_75445_i <= 0) {
               this.field_75445_i = 4 + this.attacker.getRNG().nextInt(7);
               this.attacker.getNavigator().tryMoveToEntityLiving(var1, this.speedTowardsTarget);
            }

            this.attackTick = Math.max(this.attackTick - 1, 0);
            if (this.attackTick <= 0) {
               if (this.attacker.hasLineOfStrikeAndTargetIsWithinStrikingDistance(var1)) {
                  this.attackTick = 20;
                  if (this.attacker.getHeldItemStack() != null) {
                     this.attacker.swingArm();
                  }

                  this.attacker.attackEntityAsMob(var1);
               } else if (this.attacker instanceof EntityAnimalWatcher) {
                  EntityAnimalWatcher entity_digger = (EntityAnimalWatcher)this.attacker;
                  if (entity_digger.isDiggingEnabled()) {
                     int attacker_y = MathHelper.floor_double(this.attacker.posY + 0.5);
                     EntityLivingBase target = var1;
                     int target_y = MathHelper.floor_double(target.posY + 0.5);
                     if (target_y > attacker_y) {
                        int target_x = MathHelper.floor_double(target.posX);
                        int target_z = MathHelper.floor_double(target.posZ);
                        entity_digger.setBlockToDig(target_x, target_y - 1, target_z, true);
                        this.attackTick = 20;
                     }
                  }
               }

            }
         }
      }
   }

   public double getMovementSpeed() {
      return this.speedTowardsTarget;
   }
}
