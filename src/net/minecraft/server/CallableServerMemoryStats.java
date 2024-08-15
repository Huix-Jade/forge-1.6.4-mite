package net.minecraft.server;

import java.util.concurrent.Callable;

public class CallableServerMemoryStats implements Callable {
   // $FF: synthetic field
   final MinecraftServer mcServer;

   public CallableServerMemoryStats(MinecraftServer var1) {
      this.mcServer = var1;
   }

   public String callServerMemoryStats() {
      return MinecraftServer.getServerConfigurationManager(this.mcServer).getCurrentPlayerCount() + " / " + MinecraftServer.getServerConfigurationManager(this.mcServer).getMaxPlayers() + "; " + MinecraftServer.getServerConfigurationManager(this.mcServer).playerEntityList;
   }

   // $FF: synthetic method
   public Object call() {
      return this.callServerMemoryStats();
   }
}
