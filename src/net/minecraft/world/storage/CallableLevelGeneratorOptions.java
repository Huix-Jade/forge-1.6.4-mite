package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class CallableLevelGeneratorOptions implements Callable {
   // $FF: synthetic field
   final WorldInfo worldInfoInstance;

   CallableLevelGeneratorOptions(WorldInfo var1) {
      this.worldInfoInstance = var1;
   }

   public String callLevelGeneratorOptions() {
      return WorldInfo.getWorldGeneratorOptions(this.worldInfoInstance);
   }

   // $FF: synthetic method
   public Object call() {
      return this.callLevelGeneratorOptions();
   }
}
