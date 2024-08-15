package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class CallableLevelSeed implements Callable {
   // $FF: synthetic field
   final WorldInfo worldInfoInstance;

   CallableLevelSeed(WorldInfo var1) {
      this.worldInfoInstance = var1;
   }

   public String callLevelSeed() {
      return String.valueOf(this.worldInfoInstance.getSeed());
   }

   // $FF: synthetic method
   public Object call() {
      return this.callLevelSeed();
   }
}
