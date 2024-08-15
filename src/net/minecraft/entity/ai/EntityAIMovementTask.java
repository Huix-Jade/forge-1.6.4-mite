package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAIMovementTask extends EntityAIBase {
   protected EntityLiving task_owner;
   protected float movement_speed;
   protected boolean swim_if_necessary;
   protected World world;
   protected boolean normally_avoids_water;
   private int last_task_owner_x;
   private int last_task_owner_z;
   private int task_owner_has_not_moved_counter;
   private int task_owner_has_not_moved_threshold;
   protected int random_number_index;

   public EntityAIMovementTask(EntityLiving task_owner, float movement_speed, boolean swim_if_necessary) {
      this.task_owner = task_owner;
      this.movement_speed = movement_speed;
      this.swim_if_necessary = swim_if_necessary;
      this.world = task_owner.worldObj;
      this.random_number_index = task_owner.rand.nextInt();
      this.setMutexBits(1);
   }

   private void checkIfTaskOwnerHasMoved() {
      int task_owner_x = MathHelper.floor_double(this.task_owner.posX);
      int task_owner_z = MathHelper.floor_double(this.task_owner.posZ);
      if (task_owner_x == this.last_task_owner_x && task_owner_z == this.last_task_owner_z) {
         ++this.task_owner_has_not_moved_counter;
      } else {
         this.last_task_owner_x = task_owner_x;
         this.last_task_owner_z = task_owner_z;
         this.task_owner_has_not_moved_counter = 0;
      }

   }

   protected boolean taskOwnerIsStuck() {
      return this.task_owner_has_not_moved_counter > this.task_owner_has_not_moved_threshold;
   }

   protected abstract PathEntity getMovementPath();

   protected float getMovementSpeed() {
      return this.movement_speed;
   }

   public void startExecuting() {
      this.last_task_owner_x = MathHelper.floor_double(this.task_owner.posX);
      this.last_task_owner_z = MathHelper.floor_double(this.task_owner.posZ);
      this.task_owner_has_not_moved_counter = 0;
      this.task_owner_has_not_moved_threshold = 30 + this.task_owner.rand.nextInt(20);
      this.normally_avoids_water = this.task_owner.getNavigator().getAvoidsWater();
      PathEntity path = this.getMovementPath();
      boolean path_is_valid = true;
      if (path != null && !path.isFinished()) {
         PathPoint final_point = path.getFinalPathPoint();
         World var10000 = this.world;
         double distance_from_final_point = World.getDistanceSqFromDeltas(this.task_owner.posX - (double)final_point.xCoord, this.task_owner.posY - (double)final_point.yCoord, this.task_owner.posZ - (double)final_point.zCoord);
         if (distance_from_final_point < 1.0) {
            path_is_valid = false;
         }
      } else {
         path_is_valid = false;
      }

      if (!path_is_valid && this.normally_avoids_water && this.swim_if_necessary) {
         this.task_owner.getNavigator().setAvoidsWater(false);
         path = this.getMovementPath();
      }

      this.task_owner.getNavigator().setPath(path, (double)this.getMovementSpeed());
   }

   public void updateTask() {
      this.task_owner.getNavigator().setSpeed((double)this.getMovementSpeed());
      this.checkIfTaskOwnerHasMoved();
   }

   public boolean continueExecuting() {
      return !this.taskOwnerIsStuck() && !this.task_owner.getNavigator().noPath();
   }

   public void resetTask() {
      this.task_owner.getNavigator().clearPathEntity();
      this.task_owner.getNavigator().setAvoidsWater(this.normally_avoids_water);
   }
}
