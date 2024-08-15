package net.minecraft.client.multiplayer;

import java.util.concurrent.Callable;

class WorldClientINNER3 implements Callable {
   // $FF: synthetic field
   final WorldClient theWorldClient;

   WorldClientINNER3(WorldClient var1) {
      this.theWorldClient = var1;
   }

   public String func_142026_a() {
      return WorldClient.func_142030_c(this.theWorldClient).thePlayer.func_142021_k();
   }

   // $FF: synthetic method
   public Object call() {
      return this.func_142026_a();
   }
}
