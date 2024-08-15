package net.minecraft.server.dedicated;

import java.util.concurrent.Callable;

class CallableType implements Callable {
   // $FF: synthetic field
   final DedicatedServer theDecitatedServer;

   CallableType(DedicatedServer var1) {
      this.theDecitatedServer = var1;
   }

   public String getType() {
      String var1 = this.theDecitatedServer.getServerModName();
      return !var1.equals("vanilla") ? "Definitely; Server brand changed to '" + var1 + "'" : "Unknown (can't tell)";
   }

   // $FF: synthetic method
   public Object call() {
      return this.getType();
   }
}
