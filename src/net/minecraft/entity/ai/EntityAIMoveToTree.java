package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.WorldServer;

public class EntityAIMoveToTree extends EntityAIMovementTask {
   public EntityAIMoveToTree(EntityLiving task_owner, float movement_speed) {
      super(task_owner, movement_speed, false);
   }

   public boolean shouldExecute() {
      return this.task_owner.getRNG().nextInt(40) > 0 ? false : this.task_owner.isBurning();
   }

   protected PathEntity getMovementPath() {
      int max_candidates = 8;
      int[] candidate_x = new int[max_candidates];
      int[] candidate_y = new int[max_candidates];
      int[] candidate_z = new int[max_candidates];
      double[] candidate_distance_sq = new double[max_candidates];
      int max_distance = 16;
      int[] block_ids = new int[]{Block.wood.blockID};
      int candidates = this.task_owner.worldObj.getNearestBlockCandidates(this.task_owner.posX, this.task_owner.posY + (double)(this.task_owner.height * 0.75F), this.task_owner.posZ, max_distance, max_distance / 4, max_candidates, block_ids, candidate_x, candidate_y, candidate_z, candidate_distance_sq);
      if (candidates == 0) {
         return null;
      } else {
         for(int candidate_index = 0; candidate_index < candidates; ++candidate_index) {
            int x = candidate_x[candidate_index];
            int y = candidate_y[candidate_index];
            int z = candidate_z[candidate_index];
            WorldServer world = this.task_owner.worldObj.getAsWorldServer();

            for(int i = 0; i < world.playerEntities.size(); ++i) {
               EntityPlayer player = (EntityPlayer)world.playerEntities.get(i);
               if (!player.isGhost() && !player.isZevimrgvInTournament() && !player.isDead && !(player.getHealth() <= 0.0F) && player.getFootBlockPosY() > y + 2 && player.getFootBlockPosY() < y + 9) {
                  int dx = player.getBlockPosX() - x;
                  int dz = player.getBlockPosZ() - z;
                  int horizontal_distance_sq = dx * dx + dz * dz;
                  if (horizontal_distance_sq <= 32) {
                     PathEntity path = this.task_owner.getNavigator().getPathToXYZ(candidate_x[candidate_index], candidate_y[candidate_index], candidate_z[candidate_index], max_distance);
                     if (path != null) {
                        PathPoint final_point = path.getFinalPathPoint();
                        if (WorldServer.getDistanceSqFromDeltas((float)(final_point.xCoord - x), (float)(final_point.yCoord - y), (float)(final_point.zCoord - z)) < 2.0) {
                           this.task_owner.increased_chance_of_spreading_fire_countdown = 100;
                           return path;
                        }
                     }
                  }
               }
            }
         }

         return null;
      }
   }

   public boolean continueExecuting() {
      return !this.task_owner.getNavigator().noPath();
   }

   public void resetTask() {
      this.task_owner.getNavigator().clearPathEntity();
   }
}
