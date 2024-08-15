package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityShadow;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;

public class EntityAISeekLitTorch extends EntityAIBase {
   private EntityLiving task_owner;
   private float movement_speed;
   private int random_number_index;

   public EntityAISeekLitTorch(EntityLiving task_owner, float movement_speed) {
      this.task_owner = task_owner;
      this.movement_speed = movement_speed;
      this.setMutexBits(3);
      this.random_number_index = task_owner.rand.nextInt();
   }

   public boolean shouldExecute() {
      return this.task_owner.getRNG().nextInt(this.task_owner instanceof EntityShadow ? 40 : 200) <= 0;
   }

   protected PathEntity findPathToLitTorch() {
      int max_candidates = 8;
      int[] candidate_x = new int[max_candidates];
      int[] candidate_y = new int[max_candidates];
      int[] candidate_z = new int[max_candidates];
      double[] candidate_distance_sq = new double[max_candidates];
      int max_distance = 16;
      int[] block_ids = new int[]{Block.torchWood.blockID, Block.torchRedstoneActive.blockID, Block.pumpkinLantern.blockID};
      int candidates = this.task_owner.worldObj.getNearestBlockCandidates(this.task_owner.posX, this.task_owner.posY + (double)(this.task_owner.height * 0.75F), this.task_owner.posZ, max_distance, max_distance / 4, max_candidates, block_ids, candidate_x, candidate_y, candidate_z, candidate_distance_sq);
      if (candidates == 0) {
         return null;
      } else {
         for(int candidate_index = 0; candidate_index < candidates; ++candidate_index) {
            PathEntity path = this.task_owner.getNavigator().getPathToXYZ(candidate_x[candidate_index], candidate_y[candidate_index], candidate_z[candidate_index], max_distance);
            if (path != null) {
               PathPoint final_point = path.getFinalPathPoint();
               if (this.task_owner.isNearLitTorch(final_point.xCoord, final_point.yCoord, final_point.zCoord)) {
                  return path;
               }
            }
         }

         return null;
      }
   }

   public boolean continueExecuting() {
      return !this.task_owner.getNavigator().noPath();
   }

   public void startExecuting() {
      this.task_owner.getNavigator().setPath(this.findPathToLitTorch(), (double)this.movement_speed);
   }

   public void resetTask() {
      this.task_owner.getNavigator().clearPathEntity();
   }
}
