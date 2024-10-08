package net.minecraft.client.gui;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenAddServer extends GuiScreen {
   private GuiScreen parentGui;
   private GuiTextField serverAddress;
   private GuiTextField serverName;
   private ServerData newServerData;
   private GuiButton button_cancel;
   private String context;

   public GuiScreenAddServer(GuiScreen par1GuiScreen, ServerData par2ServerData, String context) {
      this.parentGui = par1GuiScreen;
      this.newServerData = par2ServerData;
      this.context = context;
   }

   public void updateScreen() {
      this.serverName.updateCursorCounter();
      this.serverAddress.updateCursorCounter();
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.getString("addServer.add")));
      this.buttonList.add(this.button_cancel = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.getString("gui.cancel")));
      this.buttonList.add(new GuiButton(2, this.width / 2 - 100, 144, I18n.getString("addServer.hideAddress") + ": " + (this.newServerData.isHidingAddress() ? I18n.getString("gui.yes") : I18n.getString("gui.no"))));
      this.serverName = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 66, 200, 20);
      this.serverName.setFocused(true);
      this.serverName.setText(this.newServerData.serverName);
      this.serverAddress = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 106, 200, 20);
      this.serverAddress.setMaxStringLength(128);
      this.serverAddress.setText(this.newServerData.serverIP);
      ((GuiButton)this.buttonList.get(0)).enabled = this.serverAddress.getText().length() > 0 && this.serverAddress.getText().split(":").length > 0 && this.serverName.getText().length() > 0;
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.enabled) {
         if (par1GuiButton.id == 1) {
            this.parentGui.confirmClicked(false, 0);
         } else if (par1GuiButton.id == 0) {
            this.newServerData.serverName = this.serverName.getText();
            this.newServerData.serverIP = this.serverAddress.getText();
            this.parentGui.confirmClicked(true, 0);
         } else if (par1GuiButton.id == 2) {
            this.newServerData.setHideAddress(!this.newServerData.isHidingAddress());
            ((GuiButton)this.buttonList.get(2)).displayString = I18n.getString("addServer.hideAddress") + ": " + (this.newServerData.isHidingAddress() ? I18n.getString("gui.yes") : I18n.getString("gui.no"));
         }
      }

   }

   protected void keyTyped(char par1, int par2) {
      if (par2 == 1) {
         this.actionPerformed(this.button_cancel);
      } else {
         this.serverName.textboxKeyTyped(par1, par2);
         this.serverAddress.textboxKeyTyped(par1, par2);
         if (par2 == 15) {
            this.serverName.setFocused(!this.serverName.isFocused());
            this.serverAddress.setFocused(!this.serverAddress.isFocused());
         }

         if (par2 == 28 || par2 == 156) {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
         }

         ((GuiButton)this.buttonList.get(0)).enabled = this.serverAddress.getText().length() > 0 && this.serverAddress.getText().split(":").length > 0 && this.serverName.getText().length() > 0;
      }
   }

   protected void mouseClicked(int par1, int par2, int par3) {
      super.mouseClicked(par1, par2, par3);
      this.serverAddress.mouseClicked(par1, par2, par3);
      this.serverName.mouseClicked(par1, par2, par3);
   }

   public void drawScreen(int par1, int par2, float par3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.getString("addServer.title." + this.context), this.width / 2, 20, 16777215);
      this.drawString(this.fontRenderer, I18n.getString("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
      this.drawString(this.fontRenderer, I18n.getString("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
      this.serverName.drawTextBox();
      this.serverAddress.drawTextBox();
      super.drawScreen(par1, par2, par3);
   }
}
