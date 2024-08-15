package net.minecraft.crash;

import java.util.concurrent.Callable;

class CallableJavaInfo2 implements Callable {
   // $FF: synthetic field
   final CrashReport theCrashReport;

   CallableJavaInfo2(CrashReport var1) {
      this.theCrashReport = var1;
   }

   public String getJavaVMInfoAsString() {
      return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
   }

   // $FF: synthetic method
   public Object call() {
      return this.getJavaVMInfoAsString();
   }
}
