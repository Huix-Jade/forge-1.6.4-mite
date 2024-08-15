package net.minecraft.crash;

import java.util.concurrent.Callable;
import net.minecraft.world.gen.layer.IntCache;

class CallableIntCache implements Callable {
   // $FF: synthetic field
   final CrashReport theCrashReport;

   CallableIntCache(CrashReport var1) {
      this.theCrashReport = var1;
   }

   public String func_85083_a() {
      return IntCache.func_85144_b();
   }

   // $FF: synthetic method
   public Object call() {
      return this.func_85083_a();
   }
}
