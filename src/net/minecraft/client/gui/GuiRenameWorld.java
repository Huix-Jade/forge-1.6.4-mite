package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

public class GuiRenameWorld extends GuiScreen {
   private GuiScreen parentGuiScreen;
   private GuiTextField theGuiTextField;
   private final String worldName;

   public GuiRenameWorld(GuiScreen var1, String var2) {
      this.parentGuiScreen = var1;
      this.worldName = var2;
   }

   public void updateScreen() {
      this.theGuiTextField.updateCursorCounter();
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.getString("selectWorld.renameButton")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.getString("gui.cancel")));
      ISaveFormat var1 = this.mc.getSaveLoader();
      WorldInfo var2 = var1.getWorldInfo(this.worldName);
      String var3 = var2.getWorldName();
      this.theGuiTextField = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
      this.theGuiTextField.setFocused(true);
      this.theGuiTextField.setText(var3);
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == 1) {
            this.mc.displayGuiScreen(this.parentGuiScreen);
         } else if (var1.id == 0) {
            ISaveFormat var2 = this.mc.getSaveLoader();
            var2.renameWorld(this.worldName, this.theGuiTextField.getText().trim());
            this.mc.displayGuiScreen(this.parentGuiScreen);
         }

      }
   }

   protected void keyTyped(char var1, int var2) {
      this.theGuiTextField.textboxKeyTyped(var1, var2);
      ((GuiButton)this.buttonList.get(0)).enabled = this.theGuiTextField.getText().trim().length() > 0;
      if (var2 == 28 || var2 == 156) {
         this.actionPerformed((GuiButton)this.buttonList.get(0));
      }

   }

   protected void mouseClicked(int var1, int var2, int var3) {
      super.mouseClicked(var1, var2, var3);
      this.theGuiTextField.mouseClicked(var1, var2, var3);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.getString("selectWorld.renameTitle"), this.width / 2, 20, 16777215);
      this.drawString(this.fontRenderer, I18n.getString("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
      this.theGuiTextField.drawTextBox();
      super.drawScreen(var1, var2, var3);
   }
}
