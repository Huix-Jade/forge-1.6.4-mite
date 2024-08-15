package net.minecraft.client;

import java.util.concurrent.Callable;

class CallableClientMemoryStats implements Callable {
   // $FF: synthetic field
   final Minecraft theMinecraft;

   CallableClientMemoryStats(Minecraft var1) {
      this.theMinecraft = var1;
   }

   public String callClientMemoryStats() {
      return this.theMinecraft.mcProfiler.profilingEnabled ? this.theMinecraft.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
   }

   // $FF: synthetic method
   public Object call() {
      return this.callClientMemoryStats();
   }
}
