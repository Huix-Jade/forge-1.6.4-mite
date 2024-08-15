package net.minecraft.client;

import java.util.concurrent.Callable;

class CallableUpdatingScreenName implements Callable {
   // $FF: synthetic field
   final Minecraft theMinecraft;

   CallableUpdatingScreenName(Minecraft var1) {
      this.theMinecraft = var1;
   }

   public String callUpdatingScreenName() {
      return this.theMinecraft.currentScreen.getClass().getCanonicalName();
   }

   // $FF: synthetic method
   public Object call() {
      return this.callUpdatingScreenName();
   }
}
