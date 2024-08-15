package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.raycast.Raycast;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving {
   public static final UUID field_110179_h = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
   public static final AttributeModifier field_110181_i;
   private PathEntity pathToEntity;
   protected Entity entityToAttack;
   protected boolean hasAttacked;
   protected int fleeingTick;
   private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);
   private float maximumHomeDistance = -1.0F;
   private EntityAIBase field_110178_bs = new EntityAIMoveTowardsRestriction(this, 1.0);
   private boolean field_110180_bt;

   public EntityCreature(World par1World) {
      super(par1World);
   }

   protected boolean isMovementCeased() {
      return false;
   }

   public final boolean avoidsPathingThroughWater() {
      return this.getNavigator().getAvoidsWater();
   }

   protected void updateEntityActionState() {
      this.worldObj.theProfiler.startSection("ai");
      if (this.fleeingTick > 0 && --this.fleeingTick == 0) {
         AttributeInstance var1 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
         var1.removeModifier(field_110181_i);
      }

      this.hasAttacked = this.isMovementCeased();
      float var21 = this.getMaxTargettingRange();
      boolean use_R100_pathing_adjustments = true;
      if (use_R100_pathing_adjustments && var21 > 24.0F) {
         var21 = 24.0F;
      }

      if (this.fleeing) {
         if (this.pathToEntity == null || this.considerStopFleeing()) {
            this.fleeing = false;
            this.pathToEntity = null;
         }
      } else if (this.has_decided_to_flee && !this.considerStopFleeing()) {
         Entity last_attacking_entity = this.getLastHarmingEntity();
         PathEntity path = this.findPathAwayFromXYZ(MathHelper.floor_double(last_attacking_entity.posX), MathHelper.floor_double(last_attacking_entity.posY), MathHelper.floor_double(last_attacking_entity.posZ), 16, 48, false);
         if (path != null) {
            this.fleeing = true;
            this.setEntityToAttack((Entity)null);
            this.pathToEntity = path;
            this.onFleeing();
         }
      }

      int ticks_existed_with_offset = this.getTicksExistedWithOffset();
      if (this.target_entity_item != null && this.target_entity_item.isDead) {
         this.target_entity_item = null;
      }

      boolean var4;
      boolean can_path_through_closed_wooden_doors;
      float max_path_length;
      if (!this.fleeing) {
         if (this.entityToAttack != null && (this.entityToAttack.isDead || this.entityToAttack.isEntityLivingBase() && this.entityToAttack.getAsEntityLivingBase().getHealth() <= 0.0F)) {
            this.entityToAttack = null;
         }

         if (this.entityToAttack == null) {
            if (this instanceof EntityEnderman || ticks_existed_with_offset % 10 == 0) {
               this.entityToAttack = this.findPlayerToAttack(var21);
            }

            if (this.entityToAttack != null) {
               this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, var21, true, false, this.avoidsPathingThroughWater(), true);
            } else if (this.rand.nextFloat() < 0.01F) {
               this.entityToAttack = this.findNonPlayerToAttack(var21);
            }

            if (this.entityToAttack == null && (this.target_entity_item == null || this.target_entity_item.isDead || this.pathToEntity == null || this.pathToEntity.isFinished()) && this.food_or_repair_item_pickup_cooldown == 0 && ticks_existed_with_offset % 20 == 0) {
               this.target_entity_item = this.findTargetEntityItem(var21 * 0.75F);
               if (this.target_entity_item != null) {
                  max_path_length = Math.min(this.getDistanceToEntity(this.target_entity_item) * 2.0F, var21 * 0.75F);
                  if (max_path_length < 4.0F) {
                     max_path_length = 4.0F;
                  }

                  if (ticks_existed_with_offset % 100 == 0) {
                     max_path_length = var21 * 0.75F;
                  }

                  var4 = true;
                  can_path_through_closed_wooden_doors = false;
                  boolean avoid_water = this.avoidsPathingThroughWater();
                  boolean entity_can_swim = true;
                  this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.target_entity_item, max_path_length, var4, can_path_through_closed_wooden_doors, avoid_water, entity_can_swim);
               }
            }
         } else if (this.entityToAttack.isEntityAlive()) {
            max_path_length = this.entityToAttack.getDistanceToEntity(this);
            var4 = false;
            if (this instanceof EntityBlaze) {
               Raycast raycast = (new Raycast(this.worldObj)).setForPiercingProjectile((Entity)null);
               if (raycast.checkForNoBlockCollision(this.getPrimaryPointOfAttack(), this.entityToAttack.getCenterPoint())) {
                  var4 = true;
               }
            }

            if (var4 || this.hasLineOfStrike(this.entityToAttack)) {
               this.attackEntity(this.entityToAttack, max_path_length);
            }
         } else {
            this.entityToAttack = null;
         }
      }

      this.worldObj.theProfiler.endSection();
      if (!this.hasAttacked && this.entityToAttack != null && ticks_existed_with_offset % 8 == 0) {
         max_path_length = Math.min(this.getDistanceToEntity(this.entityToAttack) * 2.0F, var21);
         if (max_path_length < 4.0F) {
            max_path_length = 4.0F;
         }

         if (ticks_existed_with_offset % 40 == 0) {
            max_path_length = var21;
         }

         PathEntity path = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, max_path_length, true, false, this.avoidsPathingThroughWater(), true);
         if (path != null) {
            this.pathToEntity = path;
         }
      } else if (!this.fleeing && !this.hasAttacked && this.pathToEntity == null && this.entityToAttack == null && this.rand.nextInt(150) == 0) {
         this.updateWanderPath();
      } else {
         long ticks_since_harmed_by_cactus = this.last_tick_harmed_by_cactus < 1L ? -1L : this.worldObj.getTotalWorldTime() - this.last_tick_harmed_by_cactus;
         can_path_through_closed_wooden_doors = ticks_since_harmed_by_cactus >= 0L && ticks_since_harmed_by_cactus < 10L && (this.pathToEntity == null || this.pathToEntity.isFinished());
         if (can_path_through_closed_wooden_doors) {
            this.updateWanderPath();
         }
      }

      boolean var3 = this.isInWater();
      var4 = this.handleLavaMovement();
      this.rotationPitch = 0.0F;
      this.isJumping = false;
      if (this.pathToEntity != null && this.rand.nextInt(this.fleeing ? 1000 : 100) > 0 && (!use_R100_pathing_adjustments || !this.pathToEntity.isFinished())) {
         this.worldObj.theProfiler.startSection("followpath");
         Vec3 var5 = this.pathToEntity.getPosition(this);

         for(double var6 = (double)(this.width * 2.0F); var5 != null && var5.squareDistanceTo(this.posX, var5.yCoord, this.posZ) < var6 * var6; var5 = this.pathToEntity.getPosition(this)) {
            this.pathToEntity.incrementPathIndex();
            if (this.pathToEntity.isFinished()) {
               if (!this.pathToEntity.include_last_point) {
                  var5 = null;
               }

               this.pathToEntity = null;
               if (this.entityToAttack != null) {
                  float maxPathLength = Math.min(this.getDistanceToEntity(this.entityToAttack) * 2.0F, var21);
                  if (maxPathLength < 4.0F) {
                     maxPathLength = 4.0F;
                  }

                  this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, maxPathLength, true, false, this.avoidsPathingThroughWater(), true);
                  break;
               }

               if (this.target_entity_item == null || this.target_entity_item.isDead || !((double)this.getDistanceToEntity(this.target_entity_item) <= 4.0) || this.isItemWithinPickupDistance(this.target_entity_item) && this.target_entity_item.canRaycastToEntity(this)) {
                  break;
               }

               this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.target_entity_item, 4.0F, true, false, this.avoidsPathingThroughWater(), true);
               if (this.pathToEntity != null) {
                  this.pathToEntity.include_last_point = true;
               }
               break;
            }
         }

         this.isJumping = false;
         if (var5 != null) {
            double var8 = var5.xCoord - this.posX;
            double var10 = var5.zCoord - this.posZ;
            double var12 = this.worldObj.getBlockCollisionTopY(var5.getBlockX(), var5.getBlockY(), var5.getBlockZ(), this) - this.boundingBox.minY;
            float var14 = (float)(Math.atan2(var10, var8) * 180.0 / Math.PI) - 90.0F;
            float var15 = MathHelper.wrapAngleTo180_float(var14 - this.rotationYaw);
            this.moveForward = (float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
            if (this.isFrenzied()) {
               this.moveForward *= 1.2F;
            }

            if (var15 > 30.0F) {
               var15 = 30.0F;
            }

            if (var15 < -30.0F) {
               var15 = -30.0F;
            }

            this.rotationYaw += var15;
            this.rotationYaw = (float)MathHelper.getYawInDegrees(this.posX, this.posZ, var5.xCoord, var5.zCoord);
            if (this.isCollidedHorizontally && var12 > (double)this.stepHeight) {
               this.isJumping = true;
            }
         }

         if (this.entityToAttack != null && this instanceof EntityArachnid && ((EntityArachnid)this).canClimbWalls()) {
            this.faceEntity(this.entityToAttack, 30.0F, 30.0F);
            if (this.isInsideOfMaterial(Material.tree_leaves) && this.entityToAttack.boundingBox.minY > this.boundingBox.minY) {
               this.isJumping = true;
            }
         }

         if (this.isCollidedHorizontally && !this.hasPath()) {
            this.isJumping = true;
         }

         if (this.rand.nextFloat() < 0.8F && (var3 || var4)) {
            this.isJumping = true;
         }

         this.worldObj.theProfiler.endSection();
      } else {
         if (this.pathToEntity != null) {
            this.entityToAttack = this.findPlayerToAttack(var21);
            if (this.entityToAttack == null) {
               this.entityToAttack = this.findNonPlayerToAttack(var21);
            }

            if (this.entityToAttack != null) {
               this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, var21, true, false, this.avoidsPathingThroughWater(), true);
               super.updateEntityActionState();
               return;
            }
         }

         super.updateEntityActionState();
         this.pathToEntity = null;
      }

   }

   protected void updateWanderPath() {
      this.worldObj.theProfiler.startSection("stroll");
      PathEntity path_entity = null;
      float heaviest_weight = -100.0F;
      int entity_x = MathHelper.floor_double(this.posX);
      int entity_y = MathHelper.floor_double(this.posY);
      int entity_z = MathHelper.floor_double(this.posZ);
      int selected_block_x = entity_x;
      int selected_block_y = entity_y;
      int selected_block_z = entity_z;
      int attempts = 10;
      int horizontal_range = 6;
      int vertical_range = 3;
      boolean intensive_search = false;
      float max_path_distance = 10.0F;

      for(int var6 = 0; var6 < attempts; ++var6) {
         int x = entity_x + this.rand.nextInt(horizontal_range * 2 + 1) - horizontal_range;
         int y = entity_y + this.rand.nextInt(vertical_range * 2 + 1) - vertical_range;
         int z = entity_z + this.rand.nextInt(horizontal_range * 2 + 1) - horizontal_range;
         float weight = this.getBlockPathWeight(x, y, z);
         if (path_entity == null || weight > heaviest_weight) {
            if (intensive_search) {
               path_entity = this.worldObj.getEntityPathToXYZ(this, x, y, z, max_path_distance, true, false, this.avoidsPathingThroughWater(), true);
               if (path_entity != null) {
                  heaviest_weight = weight;
               }
            } else {
               heaviest_weight = weight;
               selected_block_x = x;
               selected_block_y = y;
               selected_block_z = z;
            }
         }
      }

      if (path_entity != null) {
         this.pathToEntity = path_entity;
      } else if (!intensive_search && heaviest_weight > -100.0F) {
         this.pathToEntity = this.worldObj.getEntityPathToXYZ(this, selected_block_x, selected_block_y, selected_block_z, max_path_distance, true, false, this.avoidsPathingThroughWater(), true);
      }

      this.worldObj.theProfiler.endSection();
   }

   protected void attackEntity(Entity par1Entity, float par2) {
   }

   public float getBlockPathWeight(int par1, int par2, int par3) {
      return 0.0F;
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      int var1 = MathHelper.floor_double(this.posX);
      int var2 = MathHelper.floor_double(this.boundingBox.minY);
      int var3 = MathHelper.floor_double(this.posZ);
      return super.getCanSpawnHere(perform_light_check) && this.getBlockPathWeight(var1, var2, var3) >= 0.0F;
   }

   public boolean hasPath() {
      return this.pathToEntity != null;
   }

   public void setPathToEntity(PathEntity par1PathEntity) {
      this.pathToEntity = par1PathEntity;
   }

   public PathEntity getPathToEntity() {
      return this.pathToEntity;
   }

   public Entity getEntityToAttack() {
      if (this.isAIEnabled()) {
         Minecraft.setErrorMessage("Why is getEntityToAttack() being called for " + this.getEntityName());
      }

      if (this.entityToAttack != null && this.entityToAttack.isDead) {
         this.setEntityToAttack((Entity)null);
      }

      return this.entityToAttack;
   }

   public void setEntityToAttack(Entity par1Entity) {
      if (this.isAIEnabled()) {
         Minecraft.setErrorMessage("Why is setEntityToAttack() being called for " + this.getEntityName());
      }

      if (par1Entity != null && par1Entity.isDead) {
         par1Entity = null;
      }

      this.entityToAttack = par1Entity;
   }

   public boolean func_110173_bK() {
      return this.func_110176_b(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
   }

   public boolean func_110176_b(int par1, int par2, int par3) {
      return this.maximumHomeDistance == -1.0F ? true : this.homePosition.getDistanceSquared(par1, par2, par3) < this.maximumHomeDistance * this.maximumHomeDistance;
   }

   public void setHomeArea(int par1, int par2, int par3, int par4) {
      this.homePosition.set(par1, par2, par3);
      this.maximumHomeDistance = (float)par4;
   }

   public ChunkCoordinates getHomePosition() {
      return this.homePosition;
   }

   public float func_110174_bM() {
      return this.maximumHomeDistance;
   }

   public void detachHome() {
      this.maximumHomeDistance = -1.0F;
   }

   public boolean hasHome() {
      return this.maximumHomeDistance != -1.0F;
   }

   protected void func_110159_bB() {
      super.func_110159_bB();
      if (this.getLeashed() && this.getLeashedToEntity() != null && this.getLeashedToEntity().worldObj == this.worldObj) {
         Entity var1 = this.getLeashedToEntity();
         this.setHomeArea((int)var1.posX, (int)var1.posY, (int)var1.posZ, 5);
         float var2 = this.getDistanceToEntity(var1);
         if (this instanceof EntityTameable && ((EntityTameable)this).isSitting()) {
            if (var2 > 10.0F) {
               this.clearLeashed(true, true);
               this.worldObj.playSoundAtEntity(var1, "random.pop", 0.2F, 0.7F);
            }

            return;
         }

         if (!this.field_110180_bt) {
            this.tasks.addTask(2, this.field_110178_bs);
            this.getNavigator().setAvoidsWater(false);
            this.field_110180_bt = true;
         }

         this.func_142017_o(var2);
         if (var2 > 4.0F) {
            this.getNavigator().tryMoveToEntityLiving(var1, 1.0);
         }

         if (var2 > 6.0F) {
            double var3 = (var1.posX - this.posX) / (double)var2;
            double var5 = (var1.posY - this.posY) / (double)var2;
            double var7 = (var1.posZ - this.posZ) / (double)var2;
            this.motionX += var3 * Math.abs(var3) * 0.4;
            this.motionY += var5 * Math.abs(var5) * 0.4;
            this.motionZ += var7 * Math.abs(var7) * 0.4;
         }

         if (var2 > 10.0F) {
            this.clearLeashed(true, true);
            this.worldObj.playSoundAtEntity(var1, "random.pop", 0.2F, 0.7F);
         }
      } else if (!this.getLeashed() && this.field_110180_bt) {
         this.field_110180_bt = false;
         this.tasks.removeTask(this.field_110178_bs);
         this.getNavigator().setAvoidsWater(true);
         this.detachHome();
      }

   }

   protected void func_142017_o(float par1) {
   }

   public EntityItem findTargetEntityItem(float max_distance) {
      return null;
   }

   static {
      field_110181_i = (new AttributeModifier(field_110179_h, "Fleeing speed bonus", 2.0, 2)).setSaved(false);
   }
}
