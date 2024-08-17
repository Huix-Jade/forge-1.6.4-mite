package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;

public final class RenderingScheme {
   public static final int VANILLA_164 = 0;
   public static final int MITE_164 = 1;
   public static final int MITE_164_EXPERIMENTAL_1 = 101;
   public static final int MITE_164_EXPERIMENTAL_2 = 102;
   private static String[] descriptor = new String[128];
   public static int current;

   public static String getSchemeDescriptor(int scheme_index) {
      return scheme_index >= 0 && scheme_index < descriptor.length ? descriptor[scheme_index] : null;
   }

   public static void setCurrent(int scheme_index) {
      if (getSchemeDescriptor(scheme_index) == null) {
         if (Minecraft.theMinecraft != null) {
            Minecraft.theMinecraft.getLogAgent().logWarning("Invalid rendering scheme (" + scheme_index + "), reverting to " + getSchemeDescriptor(1) + " (" + 1 + ")");
         }

         scheme_index = 1;
      } else {
         Minecraft.theMinecraft.getLogAgent().logInfo("Rendering scheme: " + getSchemeDescriptor(scheme_index));
      }

      current = scheme_index;
      Tessellator.instance = current == 0 ? new Tessellator() : new TessellatorMITE();
   }

   static {
      descriptor[0] = "Vanilla 1.6.4";
      descriptor[1] = "MITE 1.6.4";
      descriptor[101] = "MITE 1.6.4 Experimental #1";
      descriptor[102] = "MITE 1.6.4 Experimental #2";
      current = 1;
   }
}
