package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public final class GLHelper {
   private static final int max_stack_depth = 16;
   private static final Object[] states_stack = new Object[16];
   private static int stack_depth;

   private static void pushState(int pname) {
      states_stack[stack_depth++] = new GLState(pname);
   }

   private static void popState(int pname) {
      GLState state = (GLState)states_stack[--stack_depth];
      states_stack[stack_depth] = null;
      if (state.pname != pname) {
         Minecraft.setErrorMessage("popState: pname mismatch");
         (new Exception()).printStackTrace();
      } else {
         state.restore();
      }
   }

   public static void restore(int pname) {
      popState(pname);
   }

   public static void enable(int pname) {
      pushState(pname);
      GL11.glEnable(pname);
   }

   public static void disable(int pname) {
      pushState(pname);
      GL11.glDisable(pname);
   }
}
