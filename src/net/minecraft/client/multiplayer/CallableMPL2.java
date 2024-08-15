package net.minecraft.client.multiplayer;

import java.util.concurrent.Callable;

class CallableMPL2 implements Callable {
   // $FF: synthetic field
   final WorldClient theWorldClient;

   CallableMPL2(WorldClient var1) {
      this.theWorldClient = var1;
   }

   public String getEntitySpawnQueueCountAndList() {
      return WorldClient.getEntitySpawnQueue(this.theWorldClient).size() + " total; " + WorldClient.getEntitySpawnQueue(this.theWorldClient).toString();
   }

   // $FF: synthetic method
   public Object call() {
      return this.getEntitySpawnQueueCountAndList();
   }
}
