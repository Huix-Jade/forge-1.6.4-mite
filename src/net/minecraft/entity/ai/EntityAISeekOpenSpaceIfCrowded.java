package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RNG;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

public class EntityAISeekOpenSpaceIfCrowded extends EntityAIBase {
   private EntityLivestock livestock;
   private float movement_speed;
   private int random_number_index;

   public EntityAISeekOpenSpaceIfCrowded(EntityLivestock livestock, float movement_speed) {
      this.livestock = livestock;
      this.movement_speed = movement_speed;
      this.setMutexBits(3);
      this.random_number_index = livestock.rand.nextInt();
   }

   public boolean shouldExecute() {
      if (this.livestock.getFreedom() > 0.5F) {
         return false;
      } else if (!this.livestock.isCrowded()) {
         return true;
      } else {
         return this.livestock.getRNG().nextInt(this.livestock.getFreedom() < 0.25F ? 10 : 40) == 0;
      }
   }

   protected PathEntity getShortestPathToFreedom() {
      boolean is_very_crowded = this.livestock.getFreedom() < 0.25F;
      int entity_x = MathHelper.floor_double(this.livestock.posX);
      int entity_y = MathHelper.floor_double(this.livestock.posY);
      int entity_z = MathHelper.floor_double(this.livestock.posZ);
      PathEntity shortest_path = null;
      int shortest_path_length = -1;
      int num_paths_found = 0;
      int max_num_paths = is_very_crowded ? 2 : 1;
      int attempts = is_very_crowded ? 16 : 4;

      for(int attempt = 0; attempt < attempts; ++attempt) {
         int dx;
         int dy;
         int dz;
         if (attempt % 2 == 0) {
            dx = RNG.int_17_minus_8[++this.random_number_index & 32767];
            dy = RNG.int_7_minus_3[++this.random_number_index & 32767];
            dz = RNG.int_17_minus_8[++this.random_number_index & 32767];
         } else {
            dx = RNG.int_33_minus_16[++this.random_number_index & 32767];
            dy = RNG.int_7_minus_3[++this.random_number_index & 32767];
            dz = RNG.int_33_minus_16[++this.random_number_index & 32767];
         }

         int x = entity_x + dx;
         int y = entity_y + dy;
         int z = entity_z + dz;

         int block_id;
         int i;
         for(i = 0; i < 8; ++i) {
            block_id = this.livestock.worldObj.getBlockId(x, y - 1, z);
            if (block_id != 0 && block_id != Block.tallGrass.blockID) {
               break;
            }

            --y;
         }

         for(i = 0; i < 8; ++i) {
            block_id = this.livestock.worldObj.getBlockId(x, y, z);
            if (block_id != Block.stone.blockID && block_id != Block.dirt.blockID && block_id != Block.grass.blockID) {
               break;
            }

            ++y;
         }

         block_id = this.livestock.worldObj.getBlockId(x, y - 1, z);
         if (block_id != Block.waterStill.blockID && block_id != Block.waterMoving.blockID && !this.livestock.isCrowded(x, y, z)) {
            PathEntity path = this.livestock.getNavigator().getPathToXYZ(x, y, z, 16);
            if (path != null) {
               PathPoint final_point = path.getFinalPathPoint();
               if (!this.livestock.isCrowded(final_point.xCoord, final_point.yCoord, final_point.zCoord)) {
                  int path_length = path.getCurrentPathLength();
                  if (shortest_path == null || path_length < shortest_path_length) {
                     shortest_path = path;
                     shortest_path_length = path_length;
                     ++num_paths_found;
                     if (num_paths_found > max_num_paths) {
                        break;
                     }
                  }
               }
            }
         }
      }

      return shortest_path;
   }

   public boolean continueExecuting() {
      if (this.livestock.getFreedom() > 0.95F) {
         return false;
      } else if (!this.livestock.isCrowded()) {
         this.resetTask();
         return true;
      } else {
         return !this.livestock.getNavigator().noPath();
      }
   }

   public void startExecuting() {
      if (this.livestock.isCrowded()) {
         this.livestock.getNavigator().setPath(this.getShortestPathToFreedom(), (double)this.movement_speed);
      }
   }

   public void resetTask() {
      this.livestock.getNavigator().clearPathEntity();
   }
}
