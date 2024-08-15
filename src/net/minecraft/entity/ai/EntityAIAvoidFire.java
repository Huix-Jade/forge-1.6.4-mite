package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RNG;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIAvoidFire extends EntityAIMovementTask {
   private int max_path_length = 8;
   private final int max_fires = 16;
   private int num_fires;
   private int[] fire_x = new int[16];
   private int[] fire_y = new int[16];
   private int[] fire_z = new int[16];
   private double[] fire_distance_sq = new double[16];

   public EntityAIAvoidFire(EntityLiving task_owner, float movement_speed, boolean swim_if_necessary) {
      super(task_owner, movement_speed, swim_if_necessary);
   }

   public boolean shouldExecute() {
      if (this.task_owner.rand.nextInt(10) > 0) {
         return false;
      } else {
         return !this.task_owner.isHarmedByFire() ? false : this.world.blockTypeIsNearTo(Block.fire.blockID, this.task_owner.posX, this.task_owner.posY, this.task_owner.posZ, this.max_path_length, this.max_path_length / 4);
      }
   }

   double getDistanceSqToNearestFire(int x, int y, int z) {
      double distance_sq_to_nearest_fire = Double.MAX_VALUE;

      for(int i = 0; i < this.num_fires; ++i) {
         World var10000 = this.world;
         double distance_sq_to_fire = World.getDistanceSqFromDeltas((float)(x - this.fire_x[i]), (float)(y - this.fire_y[i]), (float)(z - this.fire_z[i]));
         if (distance_sq_to_fire < distance_sq_to_nearest_fire) {
            distance_sq_to_nearest_fire = distance_sq_to_fire;
         }
      }

      return distance_sq_to_nearest_fire;
   }

   protected PathEntity getMovementPath() {
      int[] block_ids = new int[]{Block.fire.blockID};
      this.num_fires = this.world.getNearestBlockCandidates(this.task_owner.posX, this.task_owner.posY + (double)(this.task_owner.height * 0.75F), this.task_owner.posZ, this.max_path_length * 2, this.max_path_length / 2, 16, block_ids, this.fire_x, this.fire_y, this.fire_z, this.fire_distance_sq);
      if (this.num_fires == 0) {
         return null;
      } else {
         int x = MathHelper.floor_double(this.task_owner.posX);
         int y = MathHelper.floor_double(this.task_owner.posY);
         int z = MathHelper.floor_double(this.task_owner.posZ);
         double longest_distance_sq_to_nearest_fire = this.getDistanceSqToNearestFire(x, y, z);
         PathEntity selected_path = null;

         for(int attempt = 0; attempt < 16; ++attempt) {
            int dx = RNG.int_max[++this.random_number_index & 32767] % (this.max_path_length * 2 + 1) - this.max_path_length;
            int dy = RNG.int_7_minus_3[++this.random_number_index & 32767];
            int dz = RNG.int_max[++this.random_number_index & 32767] % (this.max_path_length * 2 + 1) - this.max_path_length;
            int trial_x = x + dx;
            int trial_y = y + dy;
            int trial_z = z + dz;

            int i;
            for(i = 0; i < 8 && this.world.isAirOrPassableBlock(trial_x, trial_y - 1, trial_z, false); ++i) {
               --trial_y;
            }

            for(i = 0; i < 8 && !this.world.isAirOrPassableBlock(trial_x, trial_y, trial_z, false); ++i) {
               ++trial_y;
            }

            double distance_sq_to_nearest_fire = this.getDistanceSqToNearestFire(trial_x, trial_y, trial_z);
            if (distance_sq_to_nearest_fire > longest_distance_sq_to_nearest_fire) {
               PathEntity path = this.task_owner.getNavigator().getPathToXYZ(trial_x, trial_y, trial_z, this.max_path_length);
               if (path != null) {
                  PathPoint final_point = path.getFinalPathPoint();
                  trial_x = final_point.xCoord;
                  trial_y = final_point.yCoord;
                  trial_z = final_point.zCoord;
                  distance_sq_to_nearest_fire = this.getDistanceSqToNearestFire(trial_x, trial_y, trial_z);
                  if (distance_sq_to_nearest_fire > longest_distance_sq_to_nearest_fire) {
                     longest_distance_sq_to_nearest_fire = distance_sq_to_nearest_fire;
                     selected_path = path;
                  }
               }
            }
         }

         return selected_path;
      }
   }
}
