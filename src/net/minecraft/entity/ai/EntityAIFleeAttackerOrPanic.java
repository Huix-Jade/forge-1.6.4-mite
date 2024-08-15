package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class EntityAIFleeAttackerOrPanic extends EntityAIMovementTask {
   private float chance_of_panicking;
   private int panic_countdown;
   boolean is_done_panicking;

   public EntityAIFleeAttackerOrPanic(EntityCreature task_owner, float movement_speed, float chance_of_panicking, boolean swim_if_necessary) {
      super(task_owner, movement_speed, swim_if_necessary);
      this.chance_of_panicking = chance_of_panicking;
   }

   private Entity getAttacker() {
      return this.task_owner.getLastHarmingEntity();
   }

   public void startPanicking() {
      this.panic_countdown = this.task_owner.rand.nextInt(100) + 100;
   }

   public boolean isPanicking() {
      if (this.task_owner.isBurning() || this.task_owner.isSpooked()) {
         this.startPanicking();
      }

      return this.panic_countdown > 0;
   }

   public boolean shouldExecute() {
      if (this.is_done_panicking && this.world.rand.nextInt(10000) == 0) {
         this.is_done_panicking = false;
      }

      this.task_owner.considerFleeing();
      return this.task_owner.has_decided_to_flee || this.isPanicking();
   }

   private PathEntity getRandomPath() {
      Vec3 var1 = RandomPositionGenerator.findRandomTarget((EntityCreature)this.task_owner, 5, 4);
      return var1 == null ? null : this.task_owner.getNavigator().getPathToXYZ(var1.xCoord, var1.yCoord, var1.zCoord);
   }

   private PathEntity getPathAwayFromAttacker() {
      Entity attacker = this.getAttacker();
      if (attacker == null) {
         return null;
      } else {
         PathEntity path = this.task_owner.findPathAwayFromXYZ(MathHelper.floor_double(attacker.posX), MathHelper.floor_double(attacker.posY), MathHelper.floor_double(attacker.posZ), 16, 48, true);
         if (path == null) {
            return null;
         } else {
            this.task_owner.fleeing = true;
            this.task_owner.onFleeing();
            return path;
         }
      }
   }

   protected PathEntity getMovementPath() {
      if (this.isPanicking()) {
         return this.getRandomPath();
      } else {
         PathEntity path = this.getPathAwayFromAttacker();
         if (path == null) {
            if (this.task_owner.rand.nextFloat() < this.chance_of_panicking) {
               this.startPanicking();
            }

            return this.getRandomPath();
         } else {
            return path;
         }
      }
   }

   public void startExecuting() {
      if (!this.is_done_panicking && this.task_owner.rand.nextFloat() < this.chance_of_panicking) {
         this.startPanicking();
      }

      if (!this.isPanicking()) {
         this.is_done_panicking = true;
      }

      super.startExecuting();
   }

   public void updateTask() {
      int panic_countdown_before = this.panic_countdown;
      if (this.task_owner.isBurning()) {
         this.startPanicking();
      } else if (this.panic_countdown > 0 && --this.panic_countdown == 0) {
         this.is_done_panicking = true;
      }

      if (panic_countdown_before == 0 && this.panic_countdown > 0 || this.taskOwnerIsStuck() || this.task_owner.getNavigator().noPath()) {
         super.startExecuting();
      }

   }

   public boolean continueExecuting() {
      this.task_owner.considerStopFleeing();
      return this.task_owner.has_decided_to_flee || this.isPanicking();
   }

   protected float getMovementSpeed() {
      return super.getMovementSpeed() + (this.isPanicking() ? 0.2F : 0.0F);
   }

   public void resetTask() {
      this.task_owner.considerStopFleeing();
      super.resetTask();
   }
}
