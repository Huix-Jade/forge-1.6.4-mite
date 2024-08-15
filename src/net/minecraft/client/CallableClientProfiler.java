package net.minecraft.client;

import java.util.concurrent.Callable;

class CallableClientProfiler implements Callable {
   // $FF: synthetic field
   final Minecraft theMinecraft;

   CallableClientProfiler(Minecraft var1) {
      this.theMinecraft = var1;
   }

   public String callClientProfilerInfo() {
      return Minecraft.func_142024_b(this.theMinecraft).getCurrentLanguage().toString();
   }

   // $FF: synthetic method
   public Object call() {
      return this.callClientProfilerInfo();
   }
}
