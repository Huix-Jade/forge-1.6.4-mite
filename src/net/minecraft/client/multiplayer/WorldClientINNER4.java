package net.minecraft.client.multiplayer;

import java.util.concurrent.Callable;

class WorldClientINNER4 implements Callable {
   // $FF: synthetic field
   final WorldClient theWorldClient;

   WorldClientINNER4(WorldClient var1) {
      this.theWorldClient = var1;
   }

   public String func_142028_a() {
      return WorldClient.func_142030_c(this.theWorldClient).getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
   }

   // $FF: synthetic method
   public Object call() {
      return this.func_142028_a();
   }
}
