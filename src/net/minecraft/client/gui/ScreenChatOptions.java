package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.EnumOptions;
import net.minecraft.client.settings.GameSettings;

public class ScreenChatOptions extends GuiScreen {
   private static final EnumOptions[] allScreenChatOptions;
   private static final EnumOptions[] allMultiplayerOptions;
   private final GuiScreen theGuiScreen;
   private final GameSettings theSettings;
   private String theChatOptions;
   private String field_82268_n;
   private int field_82269_o;

   public ScreenChatOptions(GuiScreen var1, GameSettings var2) {
      this.theGuiScreen = var1;
      this.theSettings = var2;
   }

   public void initGui() {
      int var1 = 0;
      this.theChatOptions = I18n.getString("options.chat.title");
      this.field_82268_n = I18n.getString("options.multiplayer.title");
      EnumOptions[] var2 = allScreenChatOptions;
      int var3 = var2.length;

      int var4;
      EnumOptions var5;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         if (var5.getEnumFloat()) {
            this.buttonList.add(new GuiSlider(var5.returnEnumOrdinal(), this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), var5, this.theSettings.getKeyBinding(var5), this.theSettings.getOptionFloatValue(var5)));
         } else {
            this.buttonList.add(new GuiSmallButton(var5.returnEnumOrdinal(), this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), var5, this.theSettings.getKeyBinding(var5)));
         }

         ++var1;
      }

      if (var1 % 2 == 1) {
         ++var1;
      }

      this.field_82269_o = this.height / 6 + 24 * (var1 >> 1);
      var1 += 2;
      var2 = allMultiplayerOptions;
      var3 = var2.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         if (var5.getEnumFloat()) {
            this.buttonList.add(new GuiSlider(var5.returnEnumOrdinal(), this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), var5, this.theSettings.getKeyBinding(var5), this.theSettings.getOptionFloatValue(var5)));
         } else {
            this.buttonList.add(new GuiSmallButton(var5.returnEnumOrdinal(), this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), var5, this.theSettings.getKeyBinding(var5)));
         }

         ++var1;
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.getString("gui.done")));
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id < 100 && var1 instanceof GuiSmallButton) {
            this.theSettings.setOptionValue(((GuiSmallButton)var1).returnEnumOptions(), 1);
            var1.displayString = this.theSettings.getKeyBinding(EnumOptions.getEnumOptions(var1.id));
         }

         if (var1.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.theGuiScreen);
         }

      }
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.theChatOptions, this.width / 2, 20, 16777215);
      this.drawCenteredString(this.fontRenderer, this.field_82268_n, this.width / 2, this.field_82269_o + 7, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   static {
      allScreenChatOptions = new EnumOptions[]{EnumOptions.CHAT_VISIBILITY, EnumOptions.CHAT_COLOR, EnumOptions.CHAT_LINKS, EnumOptions.CHAT_OPACITY, EnumOptions.CHAT_LINKS_PROMPT, EnumOptions.CHAT_SCALE, EnumOptions.CHAT_HEIGHT_FOCUSED, EnumOptions.CHAT_HEIGHT_UNFOCUSED, EnumOptions.CHAT_WIDTH};
      allMultiplayerOptions = new EnumOptions[]{EnumOptions.SHOW_CAPE};
   }
}
