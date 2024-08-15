package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;

public class GuiYesNoMITE extends GuiScreen {
   protected GuiScreen parentScreen;
   protected String message1;
   private String message2;
   protected String buttonText1;
   protected String buttonText2;
   protected int worldNumber;
   GuiSmallButton button_yes;
   GuiSmallButton button_no;

   public GuiYesNoMITE(GuiScreen par1GuiScreen, String par2Str, String par3Str, int par4) {
      this.parentScreen = par1GuiScreen;
      this.message1 = par2Str;
      this.message2 = par3Str;
      this.worldNumber = par4;
      this.buttonText1 = I18n.getString("gui.yes");
      this.buttonText2 = I18n.getString("gui.no");
   }

   public GuiYesNoMITE(GuiScreen par1GuiScreen, String par2Str, String par3Str, String par4Str, String par5Str, int par6) {
      this.parentScreen = par1GuiScreen;
      this.message1 = par2Str;
      this.message2 = par3Str;
      this.buttonText1 = par4Str;
      this.buttonText2 = par5Str;
      this.worldNumber = par6;
   }

   public void initGui() {
      this.buttonList.add(this.button_yes = new GuiSmallButton(0, this.width / 2 - 155, this.height / 6 + 96, this.buttonText1));
      this.buttonList.add(this.button_no = new GuiSmallButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.buttonText2));
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      this.parentScreen.confirmClicked(par1GuiButton.id == 0, this.worldNumber);
   }

   protected int getMessage1YPos() {
      return this.height / 2 - 50;
   }

   protected int getMessage2YPos() {
      return this.getMessage1YPos() + 20;
   }

   protected boolean showMessage2() {
      return true;
   }

   public void drawScreen(int par1, int par2, float par3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.message1, this.width / 2, this.getMessage1YPos(), 16777215);
      if (this.showMessage2()) {
         this.drawCenteredString(this.fontRenderer, this.message2, this.width / 2, this.getMessage2YPos(), 16777215);
      }

      super.drawScreen(par1, par2, par3);
   }

   protected void keyTyped(char par1, int par2) {
      if (par2 == 1) {
         this.actionPerformed(this.button_no);
      } else {
         super.keyTyped(par1, par2);
      }

   }
}
