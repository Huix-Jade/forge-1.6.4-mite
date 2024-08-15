package net.minecraft.stats;

import java.util.Map;

class ThreadStatSyncherSend extends Thread {
   // $FF: synthetic field
   final Map field_77483_a;
   // $FF: synthetic field
   final StatsSyncher syncher;

   ThreadStatSyncherSend(StatsSyncher var1, Map var2) {
      this.syncher = var1;
      this.field_77483_a = var2;
   }

   public void run() {
      try {
         StatsSyncher.func_77414_a(this.syncher, this.field_77483_a, StatsSyncher.getUnsentDataFile(this.syncher), StatsSyncher.getUnsentTempFile(this.syncher), StatsSyncher.getUnsentOldFile(this.syncher));
      } catch (Exception var5) {
         var5.printStackTrace();
      } finally {
         StatsSyncher.setBusy(this.syncher, false);
      }

   }
}
