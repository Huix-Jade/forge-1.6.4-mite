package net.minecraft.crash;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.util.MathHelper;

public class CrashReportCategory {
   private final CrashReport theCrashReport;
   private final String field_85076_b;
   private final List field_85077_c = new ArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];

   public CrashReportCategory(CrashReport var1, String var2) {
      this.theCrashReport = var1;
      this.field_85076_b = var2;
   }

   public static String func_85074_a(double var0, double var2, double var4) {
      return String.format("%.2f,%.2f,%.2f - %s", var0, var2, var4, getLocationInfo(MathHelper.floor_double(var0), MathHelper.floor_double(var2), MathHelper.floor_double(var4)));
   }

   public static String getLocationInfo(int var0, int var1, int var2) {
      StringBuilder var3 = new StringBuilder();

      try {
         var3.append(String.format("World: (%d,%d,%d)", var0, var1, var2));
      } catch (Throwable var16) {
         var3.append("(Error finding world loc)");
      }

      var3.append(", ");

      int var4;
      int var5;
      int var6;
      int var7;
      int var8;
      int var9;
      int var10;
      int var11;
      int var12;
      try {
         var4 = var0 >> 4;
         var5 = var2 >> 4;
         var6 = var0 & 15;
         var7 = var1 >> 4;
         var8 = var2 & 15;
         var9 = var4 << 4;
         var10 = var5 << 4;
         var11 = (var4 + 1 << 4) - 1;
         var12 = (var5 + 1 << 4) - 1;
         var3.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", var6, var7, var8, var4, var5, var9, var10, var11, var12));
      } catch (Throwable var15) {
         var3.append("(Error finding chunk loc)");
      }

      var3.append(", ");

      try {
         var4 = var0 >> 9;
         var5 = var2 >> 9;
         var6 = var4 << 5;
         var7 = var5 << 5;
         var8 = (var4 + 1 << 5) - 1;
         var9 = (var5 + 1 << 5) - 1;
         var10 = var4 << 9;
         var11 = var5 << 9;
         var12 = (var4 + 1 << 9) - 1;
         int var13 = (var5 + 1 << 9) - 1;
         var3.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", var4, var5, var6, var7, var8, var9, var10, var11, var12, var13));
      } catch (Throwable var14) {
         var3.append("(Error finding world loc)");
      }

      return var3.toString();
   }

   public void addCrashSectionCallable(String var1, Callable var2) {
      try {
         this.addCrashSection(var1, var2.call());
      } catch (Throwable var4) {
         this.addCrashSectionThrowable(var1, var4);
      }

   }

   public void addCrashSection(String var1, Object var2) {
      this.field_85077_c.add(new CrashReportCategoryEntry(var1, var2));
   }

   public void addCrashSectionThrowable(String var1, Throwable var2) {
      this.addCrashSection(var1, var2);
   }

   public int func_85073_a(int var1) {
      StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();

//      this.stackTrace = new StackTraceElement[astacktraceelement.length - 3 - var1];
//      System.arraycopy(astacktraceelement, 3 + var1, this.stackTrace, 0, this.stackTrace.length);


      //BugFix: Causes AIOOB for stacks < 3 + par1
      int len = astacktraceelement.length - 3 - var1;
      if (len <= 0) len = astacktraceelement.length;
      this.stackTrace = new StackTraceElement[len];
      System.arraycopy(astacktraceelement, astacktraceelement.length - len, this.stackTrace, 0, this.stackTrace.length);

      return this.stackTrace.length;
   }

   public boolean func_85069_a(StackTraceElement var1, StackTraceElement var2) {
      if (this.stackTrace.length != 0 && var1 != null) {
         StackTraceElement var3 = this.stackTrace[0];
         if (var3.isNativeMethod() == var1.isNativeMethod() && var3.getClassName().equals(var1.getClassName()) && var3.getFileName().equals(var1.getFileName()) && var3.getMethodName().equals(var1.getMethodName())) {
            if (var2 != null != this.stackTrace.length > 1) {
               return false;
            } else if (var2 != null && !this.stackTrace[1].equals(var2)) {
               return false;
            } else {
               this.stackTrace[0] = var1;
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void func_85070_b(int var1) {
      StackTraceElement[] var2 = new StackTraceElement[this.stackTrace.length - var1];
      System.arraycopy(this.stackTrace, 0, var2, 0, var2.length);
      this.stackTrace = var2;
   }

   public void func_85072_a(StringBuilder var1) {
      var1.append("-- ").append(this.field_85076_b).append(" --\n");
      var1.append("Details:");
      Iterator var2 = this.field_85077_c.iterator();

      while(var2.hasNext()) {
         CrashReportCategoryEntry var3 = (CrashReportCategoryEntry)var2.next();
         var1.append("\n\t");
         var1.append(var3.func_85089_a());
         var1.append(": ");
         var1.append(var3.func_85090_b());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         var1.append("\nStacktrace:");
         StackTraceElement[] var6 = this.stackTrace;
         int var7 = var6.length;

         for(int var4 = 0; var4 < var7; ++var4) {
            StackTraceElement var5 = var6[var4];
            var1.append("\n\tat ");
            var1.append(var5.toString());
         }
      }

   }

   public static void addBlockCrashInfo(CrashReportCategory var0, int var1, int var2, int var3, int var4, int var5) {
      var0.addCrashSectionCallable("Block type", new CallableBlockType(var4));
      var0.addCrashSectionCallable("Block data value", new CallableBlockDataValue(var5));
      var0.addCrashSectionCallable("Block location", new CallableBlockLocation(var1, var2, var3));
   }
}
