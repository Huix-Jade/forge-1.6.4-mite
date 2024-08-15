package net.minecraft.client;

import java.util.concurrent.Callable;

class CallableParticleScreenName implements Callable {
   // $FF: synthetic field
   final Minecraft theMinecraft;

   CallableParticleScreenName(Minecraft var1) {
      this.theMinecraft = var1;
   }

   public String callParticleScreenName() {
      return this.theMinecraft.currentScreen.getClass().getCanonicalName();
   }

   // $FF: synthetic method
   public Object call() {
      return this.callParticleScreenName();
   }
}
