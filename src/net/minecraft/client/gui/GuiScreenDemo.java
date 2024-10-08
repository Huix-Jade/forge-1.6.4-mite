package net.minecraft.client.gui;

import java.net.URI;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiScreenDemo extends GuiScreen {
   private static final ResourceLocation field_110407_a = new ResourceLocation("textures/gui/demo_background.png");

   public void initGui() {
      this.buttonList.clear();
      byte var1 = -16;
      this.buttonList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + var1, 114, 20, I18n.getString("demo.help.buy")));
      this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 2 + 62 + var1, 114, 20, I18n.getString("demo.help.later")));
   }

   protected void actionPerformed(GuiButton var1) {
      switch (var1.id) {
         case 1:
            var1.enabled = false;

            try {
               Class var2 = Class.forName("java.awt.Desktop");
               Object var3 = var2.getMethod("getDesktop").invoke((Object)null);
               var2.getMethod("browse", URI.class).invoke(var3, new URI("http://www.minecraft.net/store?source=demo"));
            } catch (Throwable var4) {
               var4.printStackTrace();
            }
            break;
         case 2:
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
      }

   }

   public void updateScreen() {
      super.updateScreen();
   }

   public void drawDefaultBackground() {
      super.drawDefaultBackground();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(field_110407_a);
      int var1 = (this.width - 248) / 2;
      int var2 = (this.height - 166) / 2;
      this.drawTexturedModalRect(var1, var2, 0, 0, 248, 166);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      int var4 = (this.width - 248) / 2 + 10;
      int var5 = (this.height - 166) / 2 + 8;
      this.fontRenderer.drawString(I18n.getString("demo.help.title"), var4, var5, 2039583);
      var5 += 12;
      GameSettings var6 = this.mc.gameSettings;
      this.fontRenderer.drawString(I18n.getStringParams("demo.help.movementShort", GameSettings.getKeyDisplayString(var6.keyBindForward.keyCode), GameSettings.getKeyDisplayString(var6.keyBindLeft.keyCode), GameSettings.getKeyDisplayString(var6.keyBindBack.keyCode), GameSettings.getKeyDisplayString(var6.keyBindRight.keyCode)), var4, var5, 5197647);
      this.fontRenderer.drawString(I18n.getString("demo.help.movementMouse"), var4, var5 + 12, 5197647);
      this.fontRenderer.drawString(I18n.getStringParams("demo.help.jump", GameSettings.getKeyDisplayString(var6.keyBindJump.keyCode)), var4, var5 + 24, 5197647);
      this.fontRenderer.drawString(I18n.getStringParams("demo.help.inventory", GameSettings.getKeyDisplayString(var6.keyBindInventory.keyCode)), var4, var5 + 36, 5197647);
      this.fontRenderer.drawSplitString(I18n.getString("demo.help.fullWrapped"), var4, var5 + 68, 218, 2039583);
      super.drawScreen(var1, var2, var3);
   }
}
