package net.minecraft.profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import net.minecraft.util.HttpUtil;

class PlayerUsageSnooperThread extends TimerTask {
   // $FF: synthetic field
   final PlayerUsageSnooper snooper;

   PlayerUsageSnooperThread(PlayerUsageSnooper var1) {
      this.snooper = var1;
   }

   public void run() {
      if (PlayerUsageSnooper.getStatsCollectorFor(this.snooper).isSnooperEnabled()) {
         HashMap var1;
         synchronized(PlayerUsageSnooper.getSyncLockFor(this.snooper)) {
            var1 = new HashMap(PlayerUsageSnooper.getDataMapFor(this.snooper));
            var1.put("snooper_count", PlayerUsageSnooper.getSelfCounterFor(this.snooper));
         }

         HttpUtil.sendPost(PlayerUsageSnooper.getStatsCollectorFor(this.snooper).getLogAgent(), PlayerUsageSnooper.getServerUrlFor(this.snooper), (Map)var1, true);
      }
   }
}
