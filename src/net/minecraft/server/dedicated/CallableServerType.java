package net.minecraft.server.dedicated;

import java.util.concurrent.Callable;

class CallableServerType implements Callable {
   // $FF: synthetic field
   final DedicatedServer theDedicatedServer;

   CallableServerType(DedicatedServer var1) {
      this.theDedicatedServer = var1;
   }

   public String callServerType() {
      return "Dedicated Server (map_server.txt)";
   }

   // $FF: synthetic method
   public Object call() {
      return this.callServerType();
   }
}
