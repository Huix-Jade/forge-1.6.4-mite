package net.minecraft.client.renderer;

import java.util.concurrent.Callable;

class CallableScreenName implements Callable {
   // $FF: synthetic field
   final EntityRenderer entityRender;

   CallableScreenName(EntityRenderer var1) {
      this.entityRender = var1;
   }

   public String callScreenName() {
      return EntityRenderer.getRendererMinecraft(this.entityRender).currentScreen.getClass().getCanonicalName();
   }

   // $FF: synthetic method
   public Object call() {
      return this.callScreenName();
   }
}
