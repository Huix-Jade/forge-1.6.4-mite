package net.minecraft.client;

import java.util.concurrent.Callable;

class CallableType2 implements Callable {
   // $FF: synthetic field
   final Minecraft mc;

   CallableType2(Minecraft var1) {
      this.mc = var1;
   }

   public String func_82886_a() {
      return "Client (map_client.txt)";
   }

   // $FF: synthetic method
   public Object call() {
      return this.func_82886_a();
   }
}
