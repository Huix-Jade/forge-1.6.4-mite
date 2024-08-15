package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;

public class GuiLanguage extends GuiScreen {
   protected GuiScreen parentGui;
   private GuiSlotLanguage languageList;
   private final GameSettings theGameSettings;
   private final LanguageManager field_135014_d;
   private GuiSmallButton doneButton;

   public GuiLanguage(GuiScreen var1, GameSettings var2, LanguageManager var3) {
      this.parentGui = var1;
      this.theGameSettings = var2;
      this.field_135014_d = var3;
   }

   public void initGui() {
      this.buttonList.add(this.doneButton = new GuiSmallButton(6, this.width / 2 - 75, this.height - 38, I18n.getString("gui.done")));
      this.languageList = new GuiSlotLanguage(this);
      this.languageList.registerScrollButtons(7, 8);
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         switch (var1.id) {
            case 5:
               break;
            case 6:
               this.mc.displayGuiScreen(this.parentGui);
               break;
            default:
               this.languageList.actionPerformed(var1);
         }

      }
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.languageList.drawScreen(var1, var2, var3);
      this.drawCenteredString(this.fontRenderer, I18n.getString("options.language"), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRenderer, "(" + I18n.getString("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
      super.drawScreen(var1, var2, var3);
   }

   // $FF: synthetic method
   static LanguageManager func_135011_a(GuiLanguage var0) {
      return var0.field_135014_d;
   }

   // $FF: synthetic method
   static GameSettings getGameSettings(GuiLanguage var0) {
      return var0.theGameSettings;
   }

   // $FF: synthetic method
   static GuiSmallButton getDoneButton(GuiLanguage var0) {
      return var0.doneButton;
   }
}
