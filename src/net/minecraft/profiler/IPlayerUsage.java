package net.minecraft.profiler;

import net.minecraft.logging.ILogAgent;

public interface IPlayerUsage {
   void addServerStatsToSnooper(PlayerUsageSnooper var1);

   void addServerTypeToSnooper(PlayerUsageSnooper var1);

   boolean isSnooperEnabled();

   ILogAgent getLogAgent();
}
