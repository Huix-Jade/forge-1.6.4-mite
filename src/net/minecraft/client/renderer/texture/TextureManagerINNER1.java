package net.minecraft.client.renderer.texture;

import java.util.concurrent.Callable;

class TextureManagerINNER1 implements Callable {
   // $FF: synthetic field
   final TextureObject theTextureObject;
   // $FF: synthetic field
   final TextureManager theTextureManager;

   TextureManagerINNER1(TextureManager var1, TextureObject var2) {
      this.theTextureManager = var1;
      this.theTextureObject = var2;
   }

   public String func_135060_a() {
      return this.theTextureObject.getClass().getName();
   }

   // $FF: synthetic method
   public Object call() {
      return this.func_135060_a();
   }
}
