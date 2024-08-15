package net.minecraft.client;

import java.util.concurrent.Callable;
import org.lwjgl.opengl.GL11;

class CallableGLInfo implements Callable {
   // $FF: synthetic field
   final Minecraft mc;

   CallableGLInfo(Minecraft var1) {
      this.mc = var1;
   }

   public String getTexturePack() {
      return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
   }

   // $FF: synthetic method
   public Object call() {
      return this.getTexturePack();
   }
}
