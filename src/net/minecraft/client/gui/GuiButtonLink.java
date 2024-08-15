package net.minecraft.client.gui;

import java.net.URI;

public class GuiButtonLink extends GuiButton {
   public GuiButtonLink(int var1, int var2, int var3, int var4, int var5, String var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public void func_96135_a(String var1) {
      try {
         URI var2 = new URI(var1);
         Class var3 = Class.forName("java.awt.Desktop");
         Object var4 = var3.getMethod("getDesktop").invoke((Object)null);
         var3.getMethod("browse", URI.class).invoke(var4, var2);
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

   }
}
