package net.minecraft.crash;

import java.util.concurrent.Callable;
import net.minecraft.util.AxisAlignedBB;

class CallableCrashMemoryReport implements Callable {
   // $FF: synthetic field
   final CrashReport theCrashReport;

   CallableCrashMemoryReport(CrashReport var1) {
      this.theCrashReport = var1;
   }

   public String getMemoryReport() {
      int var1 = AxisAlignedBB.getAABBPool().getlistAABBsize();
      int var2 = 56 * var1;
      int var3 = var2 / 1024 / 1024;
      int var4 = AxisAlignedBB.getAABBPool().getnextPoolIndex();
      int var5 = 56 * var4;
      int var6 = var5 / 1024 / 1024;
      return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; " + var6 + " MB) used";
   }

   // $FF: synthetic method
   public Object call() {
      return this.getMemoryReport();
   }
}
