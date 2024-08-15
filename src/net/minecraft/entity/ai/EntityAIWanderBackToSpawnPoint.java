package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;

public class EntityAIWanderBackToSpawnPoint extends EntityAIMovementTask {
   public EntityAIWanderBackToSpawnPoint(EntityLiving task_owner, float movement_speed, boolean swim_if_necessary) {
      super(task_owner, movement_speed, swim_if_necessary);
   }

   public boolean shouldExecute() {
      return this.task_owner.getRNG().nextInt(40) <= 0 && !(this.task_owner.getDistanceSqToSpawnPoint() < 64.0);
   }

   protected PathEntity getMovementPath() {
      return this.task_owner.findPathTowardXYZ(this.task_owner.spawn_x, this.task_owner.spawn_y, this.task_owner.spawn_z, 16, true);
   }

   public boolean continueExecuting() {
      return this.task_owner.getDistanceSqToSpawnPoint() < 64.0 ? false : super.continueExecuting();
   }
}
