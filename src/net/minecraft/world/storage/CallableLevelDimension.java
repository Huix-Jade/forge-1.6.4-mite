package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class CallableLevelDimension implements Callable {
   // $FF: synthetic field
   final WorldInfo worldInfoInstance;

   CallableLevelDimension(WorldInfo var1) {
      this.worldInfoInstance = var1;
   }

   public String callLevelDimension() {
      return String.valueOf(WorldInfo.func_85122_i(this.worldInfoInstance));
   }

   // $FF: synthetic method
   public Object call() {
      return this.callLevelDimension();
   }
}
