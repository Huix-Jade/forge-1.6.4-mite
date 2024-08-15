package net.minecraft.client.mco;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenClientOutdated extends GuiScreen {
   private final GuiScreen previousScreen;

   public GuiScreenClientOutdated(GuiScreen var1) {
      this.previousScreen = var1;
   }

   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, "Back"));
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      String var4 = I18n.getString("mco.client.outdated.title");
      String var5 = I18n.getString("mco.client.outdated.msg");
      this.drawCenteredString(this.fontRenderer, var4, this.width / 2, this.height / 2 - 50, 16711680);
      this.drawCenteredString(this.fontRenderer, var5, this.width / 2, this.height / 2 - 30, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.id == 0) {
         this.mc.displayGuiScreen(this.previousScreen);
      }

   }

   protected void keyTyped(char var1, int var2) {
      if (var2 == 28 || var2 == 156) {
         this.mc.displayGuiScreen(this.previousScreen);
      }

   }
}
