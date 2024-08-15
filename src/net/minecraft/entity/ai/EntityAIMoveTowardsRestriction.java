package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsRestriction extends EntityAIBase {
   private EntityCreature theEntity;
   private double movePosX;
   private double movePosY;
   private double movePosZ;
   private double movementSpeed;

   public EntityAIMoveTowardsRestriction(EntityCreature var1, double var2) {
      this.theEntity = var1;
      this.movementSpeed = var2;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.theEntity.func_110173_bK()) {
         return false;
      } else {
         ChunkCoordinates var1 = this.theEntity.getHomePosition();
         Vec3 var2 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, this.theEntity.worldObj.getWorldVec3Pool().getVecFromPool((double)var1.posX, (double)var1.posY, (double)var1.posZ));
         if (var2 == null) {
            return false;
         } else {
            this.movePosX = var2.xCoord;
            this.movePosY = var2.yCoord;
            this.movePosZ = var2.zCoord;
            return true;
         }
      }
   }

   public boolean continueExecuting() {
      return !this.theEntity.getNavigator().noPath();
   }

   public void startExecuting() {
      this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.movementSpeed);
   }
}
