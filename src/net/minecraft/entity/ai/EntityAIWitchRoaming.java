package net.minecraft.entity.ai;

import net.minecraft.entity.monster.EntityWitch;

public class EntityAIWitchRoaming extends EntityAIBase {
   private EntityWitch witch;

   public EntityAIWitchRoaming(EntityWitch witch) {
      this.witch = witch;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      return !this.witch.worldObj.isDaytime() && !this.witch.worldObj.isPlayerNearby((double)this.witch.spawn_x, (double)this.witch.spawn_y, (double)this.witch.spawn_z, 16.0) && this.witch.getRNG().nextInt(100) <= 0;
   }

   public boolean continueExecuting() {
      if (!this.witch.worldObj.isDaytime() && !(this.witch.getDistanceSqToSpawnPoint() > 4096.0) && !this.witch.worldObj.isPlayerNearby((double)this.witch.spawn_x, (double)this.witch.spawn_y, (double)this.witch.spawn_z, 16.0) && this.witch.getRNG().nextInt(4000) != 0) {
         return !this.witch.getNavigator().noPath();
      } else {
         return false;
      }
   }

   public void startExecuting() {
      this.witch.getNavigator().setPath(this.witch.findPathAwayFromXYZ(this.witch.spawn_x, this.witch.spawn_y, this.witch.spawn_z, 16, 48, true), 1.0);
   }

   public void resetTask() {
      this.witch.getNavigator().clearPathEntity();
   }
}
