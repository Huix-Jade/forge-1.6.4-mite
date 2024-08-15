package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.RNG;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIAvoidPotentialPredators extends EntityAIMovementTask {
   private double distance_sq_to_nearest_predator;
   private AxisAlignedBB bounding_box = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   private int max_path_length = 32;
   private List predators;

   public EntityAIAvoidPotentialPredators(EntityLiving task_owner, float movement_speed, boolean swim_if_necessary) {
      super(task_owner, movement_speed, swim_if_necessary);
   }

   public boolean shouldExecute() {
      if (this.task_owner.rand.nextInt(10) > 0) {
         return false;
      } else {
         this.bounding_box.setBounds(this.task_owner.posX - (double)this.max_path_length, this.task_owner.posY - 4.0, this.task_owner.posZ - (double)this.max_path_length, this.task_owner.posX + (double)this.max_path_length, this.task_owner.posY + 4.0, this.task_owner.posZ + (double)this.max_path_length);
         this.predators = this.world.getPredatorsWithinAABBForEntity(this.task_owner, this.bounding_box);
         if (this.task_owner instanceof EntityHorse) {
            EntityHorse entity_horse = (EntityHorse)this.task_owner;
            if (entity_horse.isShy()) {
               List nearby_players = this.world.getEntitiesWithinAABB(EntityPlayer.class, this.bounding_box);
               this.predators.addAll(nearby_players);
            }
         }

         this.distance_sq_to_nearest_predator = this.getDistanceSqToNearestPredator(MathHelper.floor_double(this.task_owner.posX), MathHelper.floor_double(this.task_owner.posY), MathHelper.floor_double(this.task_owner.posZ), this.predators);
         return this.distance_sq_to_nearest_predator < (double)(this.max_path_length * this.max_path_length / 4);
      }
   }

   double getDistanceSqToNearestPredator(int x, int y, int z, List predators) {
      double distance_sq_to_nearest_predator = Double.MAX_VALUE;
      Iterator i = predators.iterator();

      while(i.hasNext()) {
         Entity entity = (Entity)i.next();
         World var10000 = this.world;
         double distance_sq_to_predator = World.getDistanceSqFromDeltas((float)x - (float)entity.posX, (float)y - (float)entity.posY, (float)z - (float)entity.posZ);
         if (distance_sq_to_predator < distance_sq_to_nearest_predator) {
            distance_sq_to_nearest_predator = distance_sq_to_predator;
         }
      }

      return distance_sq_to_nearest_predator;
   }

   protected PathEntity getMovementPath() {
      int prey_x = MathHelper.floor_double(this.task_owner.posX);
      int prey_y = MathHelper.floor_double(this.task_owner.posY);
      int prey_z = MathHelper.floor_double(this.task_owner.posZ);
      double longest_distance_sq_to_nearest_predator = this.distance_sq_to_nearest_predator;
      PathEntity selected_path = null;

      for(int attempt = 0; attempt < 16; ++attempt) {
         int dx = RNG.int_max[++this.random_number_index & 32767] % (this.max_path_length * 2 + 1) - this.max_path_length;
         int dy = RNG.int_7_minus_3[++this.random_number_index & 32767];
         int dz = RNG.int_max[++this.random_number_index & 32767] % (this.max_path_length * 2 + 1) - this.max_path_length;
         int trial_x = prey_x + dx;
         int trial_y = prey_y + dy;
         int trial_z = prey_z + dz;

         int i;
         for(i = 0; i < 8 && this.world.isAirOrPassableBlock(trial_x, trial_y - 1, trial_z, false); ++i) {
            --trial_y;
         }

         for(i = 0; i < 8 && !this.world.isAirOrPassableBlock(trial_x, trial_y, trial_z, false); ++i) {
            ++trial_y;
         }

         double distance_sq_to_nearest_predator = this.getDistanceSqToNearestPredator(trial_x, trial_y, trial_z, this.predators);
         if (distance_sq_to_nearest_predator > longest_distance_sq_to_nearest_predator) {
            PathEntity path = this.task_owner.getNavigator().getPathToXYZ(trial_x, trial_y, trial_z, this.max_path_length);
            if (path != null) {
               PathPoint final_point = path.getFinalPathPoint();
               trial_x = final_point.xCoord;
               trial_y = final_point.yCoord;
               trial_z = final_point.zCoord;
               distance_sq_to_nearest_predator = this.getDistanceSqToNearestPredator(trial_x, trial_y, trial_z, this.predators);
               if (distance_sq_to_nearest_predator > longest_distance_sq_to_nearest_predator) {
                  longest_distance_sq_to_nearest_predator = distance_sq_to_nearest_predator;
                  selected_path = path;
               }
            }
         }
      }

      return selected_path;
   }

   private boolean isPredatorAttacking() {
      Iterator i = this.predators.iterator();

      EntityLivingBase entity_living_base;
      do {
         if (!i.hasNext()) {
            return false;
         }

         entity_living_base = (EntityLivingBase)i.next();
      } while(!(entity_living_base instanceof EntityLiving) || entity_living_base.getAsEntityLiving().getTarget() != this.task_owner);

      return true;
   }

   protected float getMovementSpeed() {
      return super.getMovementSpeed() * (this.isPredatorAttacking() ? 1.2F : 1.0F);
   }

   public void resetTask() {
      super.resetTask();
      this.predators = null;
   }
}
