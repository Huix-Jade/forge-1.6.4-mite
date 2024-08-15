package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIRestrictOpenDoor extends EntityAIBase {
   private EntityCreature entityObj;
   private VillageDoorInfo frontDoor;

   public EntityAIRestrictOpenDoor(EntityCreature var1) {
      this.entityObj = var1;
   }

   public boolean shouldExecute() {
      if (this.entityObj.worldObj.isDaytime()) {
         return false;
      } else {
         Village var1 = this.entityObj.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ), 16);
         if (var1 == null) {
            return false;
         } else {
            this.frontDoor = var1.findNearestDoor(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ));
            if (this.frontDoor == null) {
               return false;
            } else {
               return (double)this.frontDoor.getInsideDistanceSquare(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ)) < 2.25;
            }
         }
      }
   }

   public boolean continueExecuting() {
      if (this.entityObj.worldObj.isDaytime()) {
         return false;
      } else {
         return !this.frontDoor.isDetachedFromVillageFlag && this.frontDoor.isInside(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posZ));
      }
   }

   public void startExecuting() {
      this.entityObj.getNavigator().setBreakDoors(false);
      this.entityObj.getNavigator().setEnterDoors(false);
   }

   public void resetTask() {
      this.entityObj.getNavigator().setBreakDoors(true);
      this.entityObj.getNavigator().setEnterDoors(true);
      this.frontDoor = null;
   }

   public void updateTask() {
      this.frontDoor.incrementDoorOpeningRestrictionCounter();
   }
}
