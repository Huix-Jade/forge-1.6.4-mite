package net.minecraft.client.renderer;

import java.util.concurrent.Callable;
import org.lwjgl.input.Mouse;

class CallableMouseLocation implements Callable {
   // $FF: synthetic field
   final int field_90026_a;
   // $FF: synthetic field
   final int field_90024_b;
   // $FF: synthetic field
   final EntityRenderer theEntityRenderer;

   CallableMouseLocation(EntityRenderer var1, int var2, int var3) {
      this.theEntityRenderer = var1;
      this.field_90026_a = var2;
      this.field_90024_b = var3;
   }

   public String callMouseLocation() {
      return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", this.field_90026_a, this.field_90024_b, Mouse.getX(), Mouse.getY());
   }

   // $FF: synthetic method
   public Object call() {
      return this.callMouseLocation();
   }
}
