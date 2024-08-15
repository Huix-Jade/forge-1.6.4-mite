package net.minecraft.server.integrated;

import java.util.concurrent.Callable;

class CallableType3 implements Callable {
   // $FF: synthetic field
   final IntegratedServer theIntegratedServer;

   CallableType3(IntegratedServer var1) {
      this.theIntegratedServer = var1;
   }

   public String getType() {
      return "Integrated Server (map_client.txt)";
   }

   // $FF: synthetic method
   public Object call() {
      return this.getType();
   }
}
