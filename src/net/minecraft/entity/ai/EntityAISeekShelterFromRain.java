package net.minecraft.entity.ai;

import net.minecraft.client.renderer.RNG;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAISeekShelterFromRain extends EntityAIMovementTask {
   private int max_path_length = 16;

   public EntityAISeekShelterFromRain(EntityLiving task_owner, float movement_speed, boolean swim_if_necessary) {
      super(task_owner, movement_speed, swim_if_necessary);
   }

   public boolean shouldExecute() {
      if (!RNG.chance_in_16[++this.random_number_index & 32767]) {
         return false;
      } else {
         return this.task_owner instanceof EntityLivestock && ((EntityLivestock)this.task_owner).isThirsty() ? false : this.task_owner.isInRain();
      }
   }

   protected PathEntity getMovementPath() {
      int task_owner_x = MathHelper.floor_double(this.task_owner.posX);
      int task_owner_y = MathHelper.floor_double(this.task_owner.posY);
      int task_owner_z = MathHelper.floor_double(this.task_owner.posZ);
      double shortest_distance_sq_to_shelter = 0.0;
      PathEntity selected_path = null;
      int domain_size = this.max_path_length * 2 + 1;

      for(int attempt = 0; attempt < 16; ++attempt) {
         int dx = RNG.int_max[++this.random_number_index & 32767] % domain_size - this.max_path_length;
         int dy = RNG.int_7_minus_3[++this.random_number_index & 32767];
         int dz = RNG.int_max[++this.random_number_index & 32767] % domain_size - this.max_path_length;
         int trial_x = task_owner_x + dx;
         int trial_y = task_owner_y + dy;
         int trial_z = task_owner_z + dz;

         int i;
         for(i = 0; i < 8 && this.world.isAirOrPassableBlock(trial_x, trial_y - 1, trial_z, false); ++i) {
            --trial_y;
         }

         for(i = 0; i < 8 && !this.world.isAirOrPassableBlock(trial_x, trial_y, trial_z, false); ++i) {
            ++trial_y;
         }

         if (this.world.isAirOrPassableBlock(trial_x, trial_y, trial_z, false) && !this.world.isInRain(trial_x, trial_y, trial_z) && !this.world.occupiedByLivingEntity(trial_x, trial_y, trial_z)) {
            World var10000 = this.world;
            double distance_sq_to_shelter = World.getDistanceSqFromDeltas((float)dx, (float)dy, (float)dz);
            if (selected_path == null || distance_sq_to_shelter < shortest_distance_sq_to_shelter) {
               PathEntity path = this.task_owner.getNavigator().getPathToXYZ(trial_x, trial_y, trial_z, this.max_path_length);
               if (path != null) {
                  PathPoint final_point = path.getFinalPathPoint();
                  if (!this.world.isInRain(final_point.xCoord, final_point.yCoord, final_point.zCoord) && !this.world.occupiedByLivingEntity(final_point.xCoord, final_point.yCoord, final_point.zCoord)) {
                     dx = final_point.xCoord - task_owner_x;
                     dy = final_point.yCoord - task_owner_y;
                     dz = final_point.zCoord - task_owner_z;
                     var10000 = this.world;
                     distance_sq_to_shelter = World.getDistanceSqFromDeltas((float)dx, (float)dy, (float)dz);
                     if (selected_path == null || distance_sq_to_shelter < shortest_distance_sq_to_shelter) {
                        shortest_distance_sq_to_shelter = distance_sq_to_shelter;
                        selected_path = path;
                     }
                  }
               }
            }
         }
      }

      return selected_path;
   }

   public boolean continueExecuting() {
      return this.task_owner instanceof EntityLivestock && ((EntityLivestock)this.task_owner).isThirsty() ? false : super.continueExecuting();
   }
}
