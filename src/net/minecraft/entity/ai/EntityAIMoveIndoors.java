package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.World;

public class EntityAIMoveIndoors extends EntityAIBase {
   private EntityCreature entityObj;
   private VillageDoorInfo doorInfo;
   private int insidePosX = -1;
   private int insidePosZ = -1;
   private float movement_speed;

   public EntityAIMoveIndoors(EntityCreature par1EntityCreature, float movement_speed) {
      this.entityObj = par1EntityCreature;
      this.setMutexBits(1);
      this.movement_speed = movement_speed;
   }

   public boolean shouldExecute() {
      if ((!this.entityObj.worldObj.isDaytime() || this.entityObj.worldObj.isPrecipitating(true)) && !this.entityObj.worldObj.provider.hasNoSky) {
         if (this.entityObj.getRNG().nextInt(50) != 0) {
            return false;
         } else if (this.insidePosX != -1 && this.entityObj.getDistanceSq((double)this.insidePosX, this.entityObj.posY, (double)this.insidePosZ) < 4.0) {
            return false;
         } else {
            Village var1 = this.entityObj.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ), 14);
            if (var1 == null) {
               return false;
            } else {
               this.doorInfo = var1.findNearestDoorUnrestricted(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ));
               if (this.doorInfo == null) {
                  return false;
               } else {
                  PathEntity path = this.entityObj.getNavigator().getPathToXYZ(this.doorInfo.getInsidePosX(), this.doorInfo.getInsidePosY(), this.doorInfo.getInsidePosZ(), 16);
                  if (path == null) {
                     return false;
                  } else {
                     PathPoint final_point = path.getFinalPathPoint();
                     World var10000 = this.entityObj.worldObj;
                     double distance = World.getDistanceSqFromDeltas((float)(this.doorInfo.getInsidePosX() - final_point.xCoord), (float)(this.doorInfo.getInsidePosY() - final_point.yCoord), (float)(this.doorInfo.getInsidePosZ() - final_point.zCoord));
                     return distance < 2.0;
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean continueExecuting() {
      return !this.entityObj.getNavigator().noPath();
   }

   public void startExecuting() {
      this.insidePosX = -1;
      if (this.entityObj.getDistanceSq((double)this.doorInfo.getInsidePosX(), (double)this.doorInfo.posY, (double)this.doorInfo.getInsidePosZ()) > 256.0) {
         Vec3 var1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.entityObj, 14, 3, this.entityObj.worldObj.getWorldVec3Pool().getVecFromPool((double)this.doorInfo.getInsidePosX() + 0.5, (double)this.doorInfo.getInsidePosY(), (double)this.doorInfo.getInsidePosZ() + 0.5));
         if (var1 != null) {
            this.entityObj.getNavigator().tryMoveToXYZ(var1.xCoord, var1.yCoord, var1.zCoord, (double)this.movement_speed);
         }
      } else {
         this.entityObj.getNavigator().tryMoveToXYZ((double)this.doorInfo.getInsidePosX() + 0.5, (double)this.doorInfo.getInsidePosY(), (double)this.doorInfo.getInsidePosZ() + 0.5, (double)this.movement_speed);
      }

   }

   public void resetTask() {
      this.insidePosX = this.doorInfo.getInsidePosX();
      this.insidePosZ = this.doorInfo.getInsidePosZ();
      this.doorInfo = null;
   }
}
