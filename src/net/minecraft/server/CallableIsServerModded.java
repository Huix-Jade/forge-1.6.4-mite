package net.minecraft.server;

import java.util.concurrent.Callable;

public class CallableIsServerModded implements Callable {
   // $FF: synthetic field
   final MinecraftServer mcServer;

   public CallableIsServerModded(MinecraftServer var1) {
      this.mcServer = var1;
   }

   public String func_96558_a() {
      return this.mcServer.theProfiler.profilingEnabled ? this.mcServer.theProfiler.getNameOfLastSection() : "N/A (disabled)";
   }

   // $FF: synthetic method
   public Object call() {
      return this.func_96558_a();
   }
}
