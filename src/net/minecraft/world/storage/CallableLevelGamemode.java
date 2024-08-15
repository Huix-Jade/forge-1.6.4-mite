package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class CallableLevelGamemode implements Callable {
   // $FF: synthetic field
   final WorldInfo worldInfoInstance;

   CallableLevelGamemode(WorldInfo var1) {
      this.worldInfoInstance = var1;
   }

   public String callLevelGameModeInfo() {
      return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", WorldInfo.getGameType(this.worldInfoInstance).getName(), WorldInfo.getGameType(this.worldInfoInstance).getID(), WorldInfo.func_85117_p(this.worldInfoInstance), WorldInfo.func_85131_q(this.worldInfoInstance));
   }

   // $FF: synthetic method
   public Object call() {
      return this.callLevelGameModeInfo();
   }
}
