package net.minecraft.crash;

import java.util.concurrent.Callable;

class CallableJavaInfo implements Callable {
   // $FF: synthetic field
   final CrashReport theCrashReport;

   CallableJavaInfo(CrashReport var1) {
      this.theCrashReport = var1;
   }

   public String getJavaInfoAsString() {
      return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
   }

   // $FF: synthetic method
   public Object call() {
      return this.getJavaInfoAsString();
   }
}
