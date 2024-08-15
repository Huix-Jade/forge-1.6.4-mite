package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class CallableLevelTime implements Callable {
   final WorldInfo worldInfoInstance;

   CallableLevelTime(WorldInfo par1WorldInfo) {
      this.worldInfoInstance = par1WorldInfo;
   }

   public String callLevelTime() {
      return String.format("%d game time, %d day time", WorldInfo.func_85126_g(this.worldInfoInstance), WorldInfo.getWorldTimeOfDay(this.worldInfoInstance, 0));
   }

   public Object call() {
      return this.callLevelTime();
   }
}
