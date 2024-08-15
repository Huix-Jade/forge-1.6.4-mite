package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.EntityAnimalWatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.World;

public class EntityAIWatchAnimal extends EntityAIBase {
   private EntityAnimalWatcher digger;
   private static boolean player_attacks_always_reset_digging = false;

   public EntityAIWatchAnimal(EntityAnimalWatcher attacker) {
      this.digger = attacker;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      if (this.digger.isHoldingItemThatPreventsDigging()) {
         return false;
      } else if (!this.digger.isDiggingEnabled() && !this.digger.canSeeTarget(false) || this.digger.recentlyHit > 0 && player_attacks_always_reset_digging) {
         return false;
      } else {
         EntityLivingBase target = this.digger.getAttackTarget();
         if (target == null) {
            return false;
         } else if (this.digger.getBlockPosX() == target.getBlockPosX() && this.digger.getBlockPosY() == target.getBlockPosY() && this.digger.getBlockPosZ() == target.getBlockPosZ()) {
            return false;
         } else if (this.digger.is_destroying_block && this.digger.canDestroyBlock(this.digger.destroy_block_x, this.digger.destroy_block_y, this.digger.destroy_block_z, true)) {
            return true;
         } else if (!this.digger.is_destroying_block && this.digger.rand.nextInt(20) != 0) {
            return false;
         } else {
            World world = this.digger.worldObj;
            float distance_to_target = this.digger.getDistanceToEntity(target);
            if (distance_to_target > 16.0F) {
               return false;
            } else {
               int attacker_foot_y = this.digger.getFootBlockPosY();
               if (distance_to_target * distance_to_target > 2.0F) {
                  int x = target.getBlockPosX();
                  int y = target.getFootBlockPosY();
                  int z = target.getBlockPosZ();

                  while(true) {
                     --y;
                     if (y < attacker_foot_y) {
                        break;
                     }

                     if (this.digger.setBlockToDig(x, y, z, true)) {
                        return true;
                     }
                  }
               }

               if (distance_to_target > 8.0F) {
                  return false;
               } else {
                  Vec3Pool vec3_pool = world.getWorldVec3Pool();
                  boolean can_attacker_see_target = world.isAirOrPassableBlock(this.digger.getBlockPosX(), MathHelper.floor_double(this.digger.getEyePosY() + 1.0), this.digger.getBlockPosZ(), false) && world.checkForLineOfPhysicalReach(world.getVec3(this.digger.posX, this.digger.getEyePosY() + 1.0, this.digger.posZ), target.getEyePos());
                  if (distance_to_target > (can_attacker_see_target ? 8.0F : (this.digger.isFrenzied() ? 6.0F : 4.0F))) {
                     return false;
                  } else {
                     PathEntity path = this.digger.getNavigator().getPathToEntityLiving(target, 16);
                     if (!this.digger.getNavigator().noPath()) {
                        return false;
                     } else if (this.digger.hasLineOfStrikeAndTargetIsWithinStrikingDistance(target)) {
                        return false;
                     } else {
                        Vec3 target_center_pos = this.digger.getTargetEntityCenterPosForBlockDestroying(target);
                        RaycastCollision rc;
                        if (world.isAirOrPassableBlock(target.getBlockPosX(), target.getHeadBlockPosY() + 1, target.getBlockPosZ(), false)) {
                           rc = world.getBlockCollisionForPhysicalReach(this.digger.getEyePosForBlockDestroying(), target_center_pos.addVector(0.0, 1.0, 0.0));
                           if (rc != null && rc.isBlock() && (!this.isRestrictedBlock(rc.getBlockHit()) || this.digger.isHoldingAnEffectiveTool(rc.getBlockHit(), rc.block_hit_metadata) || this.digger.isTargettingAPlayer())) {
                              ++rc.block_hit_y;

                              while(rc.block_hit_y >= attacker_foot_y) {
                                 if (this.digger.setBlockToDig(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, true)) {
                                    return true;
                                 }

                                 --rc.block_hit_y;
                              }
                           }
                        }

                        rc = world.getBlockCollisionForPhysicalReach(this.digger.getEyePosForBlockDestroying(), target_center_pos);
                        if (rc != null && rc.isBlock() && (!this.isRestrictedBlock(rc.getBlockHit()) || this.digger.isHoldingAnEffectiveTool(rc.getBlockHit(), rc.block_hit_metadata) || this.digger.isTargettingAPlayer())) {
                           ++rc.block_hit_y;

                           while(rc.block_hit_y >= attacker_foot_y) {
                              if (this.digger.setBlockToDig(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, true)) {
                                 return true;
                              }

                              --rc.block_hit_y;
                           }
                        }

                        rc = world.getBlockCollisionForPhysicalReach(this.digger.getAttackerLegPosForBlockDestroying(), target_center_pos);
                        return rc != null && rc.isBlock() && (!this.isRestrictedBlock(rc.getBlockHit()) || this.digger.isHoldingAnEffectiveTool(rc.getBlockHit(), rc.block_hit_metadata) || this.digger.isTargettingAPlayer()) && (world.isAirOrPassableBlock(rc.block_hit_x, rc.block_hit_y + 1, rc.block_hit_z, false) || this.digger.blockWillFall(rc.block_hit_x, rc.block_hit_y + 1, rc.block_hit_z)) && this.digger.setBlockToDig(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, false);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isRestrictedBlock(Block block) {
      return block instanceof BlockFence;
   }

   private RaycastCollision getIntersectingBlock(Vec3 attacker_eye_pos, Vec3 target_pos) {
      return this.digger.worldObj.getBlockCollisionForPhysicalReach(attacker_eye_pos, target_pos);
   }

   private boolean couldGetCloserByPathing() {
      EntityLivingBase target = this.digger.getAttackTarget();
      if (target == null) {
         return false;
      } else {
         World var10000 = this.digger.worldObj;
         double distance = (double)World.getDistanceFromDeltas(this.digger.posX - target.posX, this.digger.posY - target.posY, this.digger.posZ - target.posZ);
         PathEntity path = this.digger.getNavigator().getPathToEntityLiving(target, 16);
         if (path == null) {
            return false;
         } else {
            PathPoint final_point = path.getFinalPathPoint();
            float x = (float)final_point.xCoord + 0.5F;
            float y = (float)final_point.yCoord;
            float z = (float)final_point.zCoord + 0.5F;
            var10000 = this.digger.worldObj;
            return (double)World.getDistanceFromDeltas((double)x - target.posX, (double)y - target.posY, (double)z - target.posZ) < distance - 2.0;
         }
      }
   }

   private boolean couldHitTargetByPathing() {
      EntityLivingBase target = this.digger.getAttackTarget();
      if (target == null) {
         return false;
      } else {
         PathEntity path = this.digger.getNavigator().getPathToEntityLiving(target, 16);
         if (path == null) {
            return false;
         } else {
            PathPoint final_point = path.getFinalPathPoint();
            float x = (float)final_point.xCoord + 0.5F;
            float y = (float)final_point.yCoord;
            float z = (float)final_point.zCoord + 0.5F;
            World var10000 = this.digger.worldObj;
            if (World.getDistanceFromDeltas((double)x - target.posX, (double)y - target.posY, (double)z - target.posZ) > 1.0F) {
               return false;
            } else {
               return this.getIntersectingBlock(this.digger.worldObj.getWorldVec3Pool().getVecFromPool((double)x, (double)y, (double)z), this.digger.getTargetEntityCenterPosForBlockDestroying(target)) == null;
            }
         }
      }
   }

   public void startExecuting() {
      this.digger.is_destroying_block = true;
   }

   public boolean continueExecuting() {
      if (this.digger.isHoldingItemThatPreventsDigging()) {
         return false;
      } else {
         EntityAIAttackOnCollide ai = (EntityAIAttackOnCollide)this.digger.getEntityAITask(EntityAIAttackOnCollide.class);
         if (ai != null) {
            if (ai.attackTick > 0) {
               --ai.attackTick;
            }

            if (ai.canStrikeTargetNow()) {
               ai.updateTask();
               return false;
            }
         }

         if (this.digger.destroy_pause_ticks > 0) {
            return this.digger.destroy_pause_ticks != 1 || !this.couldGetCloserByPathing();
         } else if (!this.digger.is_destroying_block) {
            return false;
         } else if (!this.digger.canDestroyBlock(this.digger.destroy_block_x, this.digger.destroy_block_y, this.digger.destroy_block_z, true)) {
            return false;
         } else if (this.digger.recentlyHit > 0 && player_attacks_always_reset_digging) {
            return false;
         } else {
            EntityLivingBase target = this.digger.getAttackTarget();
            if (target == null) {
               return false;
            } else if (this.digger.getBlockPosX() == target.getBlockPosX() && this.digger.getBlockPosY() == target.getBlockPosY() && this.digger.getBlockPosZ() == target.getBlockPosZ()) {
               return false;
            } else {
               return this.digger.getTicksExistedWithOffset() % 10 != 0 || !this.couldHitTargetByPathing();
            }
         }
      }
   }

   public void updateTask() {
      if (this.digger.destroy_pause_ticks > 0) {
         --this.digger.destroy_pause_ticks;
      } else {
         if (this.digger.destroy_block_cooloff == 10) {
            this.digger.swingArm();
         }

         if (--this.digger.destroy_block_cooloff <= 0) {
            this.digger.destroy_block_cooloff = this.digger.getCooloffForBlock();
            this.digger.partiallyDestroyBlock();
         }
      }
   }

   public void resetTask() {
      this.digger.cancelBlockDestruction();
   }
}
