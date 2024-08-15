package net.minecraft.client.renderer;

import net.minecraft.client.renderer.texture.StitchHolder;

public class StitcherException extends RuntimeException {
   private final StitchHolder field_98149_a;

   public StitcherException(StitchHolder var1, String var2) {
      super(var2);
      this.field_98149_a = var1;
   }
}
