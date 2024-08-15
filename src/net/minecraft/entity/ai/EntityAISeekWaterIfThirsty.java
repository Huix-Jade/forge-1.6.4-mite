package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivestock;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

public class EntityAISeekWaterIfThirsty extends EntityAIMovementTask {
   private EntityLivestock task_owner;

   public EntityAISeekWaterIfThirsty(EntityLivestock task_owner, float movement_speed, boolean swim_if_necessary) {
      super(task_owner, movement_speed, swim_if_necessary);
      this.task_owner = task_owner;
   }

   public boolean shouldExecute() {
      if (!this.task_owner.isThirsty()) {
         return false;
      } else if (this.task_owner.isNearWaterSource()) {
         return true;
      } else {
         return this.task_owner.getRNG().nextInt(this.task_owner.isDesperateForWater() ? 10 : (this.task_owner.isVeryThirsty() ? 40 : 120)) <= 0;
      }
   }

   protected PathEntity getMovementPath() {
      boolean is_very_thirsty = this.task_owner.isVeryThirsty();
      boolean is_desperate = this.task_owner.isDesperateForWater();
      int max_candidates = is_desperate ? 24 : (is_very_thirsty ? 16 : 8);
      int[] candidate_x = new int[max_candidates];
      int[] candidate_y = new int[max_candidates];
      int[] candidate_z = new int[max_candidates];
      double[] candidate_distance_sq = new double[max_candidates];
      int max_distance = is_desperate ? 48 : (is_very_thirsty ? 32 : 16);
      int[] block_ids = this.task_owner.getWaterBlockIDs();
      int candidates = this.task_owner.worldObj.getNearestBlockCandidates(this.task_owner.posX, this.task_owner.posY + (double)(this.task_owner.height * 0.75F), this.task_owner.posZ, max_distance, max_distance / 8, max_candidates, block_ids, candidate_x, candidate_y, candidate_z, candidate_distance_sq);

      for(int candidate_index = 0; candidate_index < candidates; ++candidate_index) {
         PathEntity path = this.task_owner.getNavigator().getPathToXYZ(candidate_x[candidate_index], candidate_y[candidate_index], candidate_z[candidate_index], max_distance);
         if (path != null) {
            PathPoint final_point = path.getFinalPathPoint();
            if (this.task_owner.isNearWaterSource(final_point.xCoord, final_point.yCoord, final_point.zCoord)) {
               return path;
            }
         }
      }

      return this.task_owner.findPathAwayFromXYZ(MathHelper.floor_double(this.task_owner.posX), MathHelper.floor_double(this.task_owner.posY), MathHelper.floor_double(this.task_owner.posZ), 16, 32, true);
   }

   public void startExecuting() {
      if (this.task_owner.isNearWaterSource()) {
         this.resetTask();
      } else {
         super.startExecuting();
      }
   }

   public boolean continueExecuting() {
      if (this.task_owner.getWater() > 0.95F) {
         return false;
      } else if (this.task_owner.isNearWaterSource()) {
         this.resetTask();
         return true;
      } else {
         return super.continueExecuting();
      }
   }
}
