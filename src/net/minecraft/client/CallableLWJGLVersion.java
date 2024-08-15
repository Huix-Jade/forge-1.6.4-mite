package net.minecraft.client;

import java.util.concurrent.Callable;
import org.lwjgl.Sys;

class CallableLWJGLVersion implements Callable {
   // $FF: synthetic field
   final Minecraft mc;

   CallableLWJGLVersion(Minecraft var1) {
      this.mc = var1;
   }

   public String getType() {
      return Sys.getVersion();
   }

   // $FF: synthetic method
   public Object call() {
      return this.getType();
   }
}
