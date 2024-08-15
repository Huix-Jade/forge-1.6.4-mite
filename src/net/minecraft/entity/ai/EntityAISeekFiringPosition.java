package net.minecraft.entity.ai;

import net.minecraft.client.renderer.RNG;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;

public class EntityAISeekFiringPosition extends EntityAIMovementTask {
   private int max_path_length = 16;
   EntityPlayer[] candidate_players = new EntityPlayer[16];
   int num_candidate_players;

   public EntityAISeekFiringPosition(EntityLiving task_owner, float movement_speed, boolean swim_if_necessary) {
      super(task_owner, movement_speed, swim_if_necessary);
   }

   public boolean shouldExecute() {
      if (this.task_owner.getAttackTarget() != null) {
         return false;
      } else if (!RNG.chance_in_16[++this.random_number_index & 32767]) {
         return false;
      } else {
         this.num_candidate_players = 0;

         for(int i = 0; i < this.world.playerEntities.size() && this.num_candidate_players < this.candidate_players.length; ++i) {
            EntityPlayer player = (EntityPlayer)this.world.playerEntities.get(i);
            if (!(this.task_owner.getDistanceSqToEntity(player) > 900.0)) {
               if (this.task_owner.getEntitySenses().canSee(player)) {
                  return false;
               }

               this.candidate_players[this.num_candidate_players++] = player;
            }
         }

         return this.num_candidate_players > 0;
      }
   }

   protected PathEntity getMovementPath() {
      int task_owner_x = this.task_owner.getBlockPosX();
      int task_owner_y = this.task_owner.getBlockPosY();
      int task_owner_z = this.task_owner.getBlockPosZ();
      int domain_size = this.max_path_length * 2 + 1;

      for(int candidate_player_index = 0; candidate_player_index < this.num_candidate_players; ++candidate_player_index) {
         EntityPlayer target = this.candidate_players[candidate_player_index];

         for(int attempt = 0; attempt < 4; ++attempt) {
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

            if (this.world.isAirOrPassableBlock(trial_x, trial_y, trial_z, false) && !this.world.occupiedByLivingEntity(trial_x, trial_y, trial_z) && target.canEntityBeSeenFrom((double)((float)trial_x + 0.5F), (double)((float)trial_y + this.task_owner.getEyeHeight()), (double)((float)trial_z + 0.5F), 900.0)) {
               PathEntity path = this.task_owner.getNavigator().getPathToXYZ(trial_x, trial_y, trial_z, this.max_path_length);
               if (path != null) {
                  PathPoint final_point = path.getFinalPathPoint();
                  if (target.canEntityBeSeenFrom((double)((float)final_point.xCoord + 0.5F), (double)((float)final_point.yCoord + this.task_owner.getEyeHeight()), (double)((float)final_point.zCoord + 0.5F), 900.0) && !this.world.occupiedByLivingEntity(final_point.xCoord, final_point.yCoord, final_point.zCoord)) {
                     return path;
                  }
               }
            }
         }
      }

      return null;
   }

   public boolean continueExecuting() {
      if (this.task_owner.getAttackTarget() != null) {
         return false;
      } else {
         if (RNG.chance_in_16[++this.random_number_index & 32767]) {
            for(int candidate_player_index = 0; candidate_player_index < this.num_candidate_players; ++candidate_player_index) {
               if (this.task_owner.getEntitySenses().canSee(this.candidate_players[candidate_player_index])) {
                  return false;
               }
            }
         }

         return super.continueExecuting();
      }
   }

   public void resetTask() {
      super.resetTask();
      this.num_candidate_players = 0;
   }
}
