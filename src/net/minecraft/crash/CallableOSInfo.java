package net.minecraft.crash;

import java.util.concurrent.Callable;

class CallableOSInfo implements Callable {
   // $FF: synthetic field
   final CrashReport theCrashReport;

   CallableOSInfo(CrashReport var1) {
      this.theCrashReport = var1;
   }

   public String getOsAsString() {
      return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
   }

   // $FF: synthetic method
   public Object call() {
      return this.getOsAsString();
   }
}
