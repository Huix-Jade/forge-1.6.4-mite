package net.minecraft.client.renderer;

import java.util.concurrent.Callable;
import net.minecraft.client.gui.ScaledResolution;

class CallableScreenSize implements Callable {
   // $FF: synthetic field
   final ScaledResolution theScaledResolution;
   // $FF: synthetic field
   final EntityRenderer theEntityRenderer;

   CallableScreenSize(EntityRenderer var1, ScaledResolution var2) {
      this.theEntityRenderer = var1;
      this.theScaledResolution = var2;
   }

   public String callScreenSize() {
      return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", this.theScaledResolution.getScaledWidth(), this.theScaledResolution.getScaledHeight(), EntityRenderer.getRendererMinecraft(this.theEntityRenderer).displayWidth, EntityRenderer.getRendererMinecraft(this.theEntityRenderer).displayHeight, this.theScaledResolution.getScaleFactor());
   }

   // $FF: synthetic method
   public Object call() {
      return this.callScreenSize();
   }
}
