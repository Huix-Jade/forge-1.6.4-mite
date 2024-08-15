package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class EntityAIWander extends EntityAIBase {
   private EntityCreature entity;
   private double xPosition;
   private double yPosition;
   private double zPosition;
   private double speed;

   public EntityAIWander(EntityCreature par1EntityCreature, double par2) {
      double limit = 0.3;
      double movement_speed = par1EntityCreature.getEntityAttributeValue(SharedMonsterAttributes.movementSpeed);
      double multiplied_speed = par2 * movement_speed;
      if (multiplied_speed > limit) {
         par2 = limit / movement_speed;
      }

      this.entity = par1EntityCreature;
      this.speed = par2;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      long ticks_since_harmed_by_cactus = this.entity.last_tick_harmed_by_cactus < 1L ? -1L : this.entity.worldObj.getTotalWorldTime() - this.entity.last_tick_harmed_by_cactus;
      boolean prompted_by_cactus = ticks_since_harmed_by_cactus >= 0L && ticks_since_harmed_by_cactus < 10L && this.entity.getNavigator().noPath();
      if (!prompted_by_cactus && this.entity.getRNG().nextInt(120) != 0) {
         return false;
      } else {
         Vec3 var1 = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);
         if (var1 == null) {
            return false;
         } else {
            if (this.entity instanceof EntityVillager) {
               int x = MathHelper.floor_double(var1.xCoord);
               int y = MathHelper.floor_double(var1.yCoord);
               int z = MathHelper.floor_double(var1.zCoord);
               PathEntity path = this.entity.getNavigator().getPathToXYZ(x, y, z, 16);
               if (path == null) {
                  return false;
               }

               PathPoint final_point = path.getFinalPathPoint();
               if (this.entity.worldObj.isInRain(final_point.xCoord, final_point.yCoord + 1, final_point.zCoord)) {
                  return false;
               }
            }

            this.xPosition = var1.xCoord;
            this.yPosition = var1.yCoord;
            this.zPosition = var1.zCoord;
            return true;
         }
      }
   }

   public boolean continueExecuting() {
      return !this.entity.getNavigator().noPath();
   }

   public void startExecuting() {
      this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
   }
}
