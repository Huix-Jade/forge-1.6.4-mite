package net.minecraft.network;

import java.util.concurrent.Callable;

class CallableConnectionName implements Callable {
   // $FF: synthetic field
   final NetServerHandler field_111201_a;
   // $FF: synthetic field
   final NetworkListenThread field_111200_b;

   CallableConnectionName(NetworkListenThread var1, NetServerHandler var2) {
      this.field_111200_b = var1;
      this.field_111201_a = var2;
   }

   public String func_111199_a() {
      return this.field_111201_a.toString();
   }

   // $FF: synthetic method
   public Object call() {
      return this.func_111199_a();
   }
}
