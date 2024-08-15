package net.minecraft.client;

import java.util.concurrent.Callable;

class CallableLaunchedVersion implements Callable {
   // $FF: synthetic field
   final Minecraft mc;

   CallableLaunchedVersion(Minecraft var1) {
      this.mc = var1;
   }

   public String getLWJGLVersion() {
      return Minecraft.getLaunchedVersion(this.mc);
   }

   // $FF: synthetic method
   public Object call() {
      return this.getLWJGLVersion();
   }
}
