package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class CallableLevelWeather implements Callable {
   final WorldInfo worldInfoInstance;

   CallableLevelWeather(WorldInfo par1WorldInfo) {
      this.worldInfoInstance = par1WorldInfo;
   }

   public String callLevelWeatherInfo() {
      return "Information removed as of R132";
   }

   public Object call() {
      return this.callLevelWeatherInfo();
   }
}
