package net.minecraft.crash;

import java.util.concurrent.Callable;

public class CallableMinecraftVersion implements Callable {
   // $FF: synthetic field
   final CrashReport theCrashReport;

   public CallableMinecraftVersion(CrashReport var1) {
      this.theCrashReport = var1;
   }

   public String minecraftVersion() {
      return "1.6.4-MITE";
   }

   // $FF: synthetic method
   public Object call() {
      return this.minecraftVersion();
   }
}
