package net.minecraft.stats;

import java.io.*;
import java.util.Map;
import net.minecraft.util.Session;

public class StatsSyncher {
   private volatile boolean isBusy;
   private volatile Map field_77430_b;
   private volatile Map field_77431_c;
   private StatFileWriter statFileWriter;
   private File unsentDataFile;
   private File dataFile;
   private File unsentTempFile;
   private File tempFile;
   private File unsentOldFile;
   private File oldFile;
   private Session theSession;
   private int field_77433_l;
   private int field_77434_m;

   public StatsSyncher(Session var1, StatFileWriter var2, File var3) {
      String var4 = var1.getUsername();
      String var5 = var4.toLowerCase();
      this.unsentDataFile = new File(var3, "stats_" + var5 + "_unsent.dat");
      this.dataFile = new File(var3, "stats_" + var5 + ".dat");
      this.unsentOldFile = new File(var3, "stats_" + var5 + "_unsent.old");
      this.oldFile = new File(var3, "stats_" + var5 + ".old");
      this.unsentTempFile = new File(var3, "stats_" + var5 + "_unsent.tmp");
      this.tempFile = new File(var3, "stats_" + var5 + ".tmp");
      if (!var5.equals(var4)) {
         this.func_77412_a(var3, "stats_" + var4 + "_unsent.dat", this.unsentDataFile);
         this.func_77412_a(var3, "stats_" + var4 + ".dat", this.dataFile);
         this.func_77412_a(var3, "stats_" + var4 + "_unsent.old", this.unsentOldFile);
         this.func_77412_a(var3, "stats_" + var4 + ".old", this.oldFile);
         this.func_77412_a(var3, "stats_" + var4 + "_unsent.tmp", this.unsentTempFile);
         this.func_77412_a(var3, "stats_" + var4 + ".tmp", this.tempFile);
      }

      this.statFileWriter = var2;
      this.theSession = var1;
      if (this.unsentDataFile.exists()) {
         var2.writeStats(this.func_77417_a(this.unsentDataFile, this.unsentTempFile, this.unsentOldFile));
      }

      this.beginReceiveStats();
   }

   private void func_77412_a(File var1, String var2, File var3) {
      File var4 = new File(var1, var2);
      if (var4.exists() && !var4.isDirectory() && !var3.exists()) {
         var4.renameTo(var3);
      }

   }

   private Map func_77417_a(File var1, File var2, File var3) {
      if (var1.exists()) {
         return this.func_77413_a(var1);
      } else if (var3.exists()) {
         return this.func_77413_a(var3);
      } else {
         return var2.exists() ? this.func_77413_a(var2) : null;
      }
   }

   private Map func_77413_a(File var1) {
      BufferedReader var2 = null;

      try {
         var2 = new BufferedReader(new FileReader(var1));
         String var3 = "";
         StringBuilder var4 = new StringBuilder();

         while((var3 = var2.readLine()) != null) {
            var4.append(var3);
         }

         Map var5 = StatFileWriter.func_77453_b(var4.toString());
         return var5;
      } catch (Exception var15) {
         var15.printStackTrace();
      } finally {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Exception var14) {
               var14.printStackTrace();
            }
         }

      }

      return null;
   }

   private void func_77421_a(Map var1, File var2, File var3, File var4) throws IOException {
      PrintWriter var5 = new PrintWriter(new FileWriter(var3, false));

      try {
         var5.print(StatFileWriter.func_77441_a(this.theSession.getUsername(), "local", var1));
      } finally {
         var5.close();
      }

      if (var4.exists()) {
         var4.delete();
      }

      if (var2.exists()) {
         var2.renameTo(var4);
      }

      var3.renameTo(var2);
   }

   public void beginReceiveStats() {
      if (this.isBusy) {
         throw new IllegalStateException("Can't get stats from server while StatsSyncher is busy!");
      } else {
         this.field_77433_l = 100;
         this.isBusy = true;
         (new ThreadStatSyncherReceive(this)).start();
      }
   }

   public void beginSendStats(Map var1) {
      if (this.isBusy) {
         throw new IllegalStateException("Can't save stats while StatsSyncher is busy!");
      } else {
         this.field_77433_l = 100;
         this.isBusy = true;
         (new ThreadStatSyncherSend(this, var1)).start();
      }
   }

   public void syncStatsFileWithMap(Map var1) {
      int var2 = 30;

      while(this.isBusy) {
         --var2;
         if (var2 <= 0) {
            break;
         }

         try {
            Thread.sleep(100L);
         } catch (InterruptedException var10) {
            var10.printStackTrace();
         }
      }

      this.isBusy = true;

      try {
         this.func_77421_a(var1, this.unsentDataFile, this.unsentTempFile, this.unsentOldFile);
      } catch (Exception var8) {
         var8.printStackTrace();
      } finally {
         this.isBusy = false;
      }

   }

   public boolean func_77425_c() {
      return this.field_77433_l <= 0 && !this.isBusy && this.field_77431_c == null;
   }

   public void func_77422_e() {
      if (this.field_77433_l > 0) {
         --this.field_77433_l;
      }

      if (this.field_77434_m > 0) {
         --this.field_77434_m;
      }

      if (this.field_77431_c != null) {
         this.statFileWriter.func_77448_c(this.field_77431_c);
         this.field_77431_c = null;
      }

      if (this.field_77430_b != null) {
         this.statFileWriter.func_77452_b(this.field_77430_b);
         this.field_77430_b = null;
      }

   }

   // $FF: synthetic method
   static Map func_77419_a(StatsSyncher var0) {
      return var0.field_77430_b;
   }

   // $FF: synthetic method
   static File func_77408_b(StatsSyncher var0) {
      return var0.dataFile;
   }

   // $FF: synthetic method
   static File func_77407_c(StatsSyncher var0) {
      return var0.tempFile;
   }

   // $FF: synthetic method
   static File func_77411_d(StatsSyncher var0) {
      return var0.oldFile;
   }

   // $FF: synthetic method
   static void func_77414_a(StatsSyncher var0, Map var1, File var2, File var3, File var4) throws IOException {
      var0.func_77421_a(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   static Map func_77416_a(StatsSyncher var0, Map var1) {
      return var0.field_77430_b = var1;
   }

   // $FF: synthetic method
   static Map func_77410_a(StatsSyncher var0, File var1, File var2, File var3) {
      return var0.func_77417_a(var1, var2, var3);
   }

   // $FF: synthetic method
   static boolean setBusy(StatsSyncher var0, boolean var1) {
      return var0.isBusy = var1;
   }

   // $FF: synthetic method
   static File getUnsentDataFile(StatsSyncher var0) {
      return var0.unsentDataFile;
   }

   // $FF: synthetic method
   static File getUnsentTempFile(StatsSyncher var0) {
      return var0.unsentTempFile;
   }

   // $FF: synthetic method
   static File getUnsentOldFile(StatsSyncher var0) {
      return var0.unsentOldFile;
   }
}
