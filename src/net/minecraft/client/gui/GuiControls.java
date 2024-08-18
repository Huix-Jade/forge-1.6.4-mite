package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.GuiControlsScrollPanel;

public class GuiControls extends GuiScreen {
   private GuiScreen parentScreen;
   protected String screenTitle = "Controls";
   private GameSettings options;
   private int buttonId = -1;
   private int page_index;

   private GuiControlsScrollPanel scrollPane;

   public GuiControls(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
      this.parentScreen = par1GuiScreen;
      this.options = par2GameSettings;
   }

   private int getLeftBorder() {
      return this.width / 2 - 155;
   }

   private int getKeybindButtonPosX(int index) {
      index %= 10;
      return this.getLeftBorder() + index % 2 * 160;
   }

   private int getKeybindButtonPosY(int index) {
      index %= 10;
      return this.height / 6 + 24 * (index / 2) + 6;
   }

   @Override
   public void initGui() {
      scrollPane = new GuiControlsScrollPanel(this, options, mc);
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 28, I18n.getString("gui.done")));
      scrollPane.registerScrollButtons(7, 8);
      this.screenTitle = I18n.getString("controls.title");
   }

   private boolean isKeybindButtonVisible(int index) {
      return index >= this.page_index * 10 && index < (this.page_index + 1) * 10;
   }

   private void setKeybindButtonVisibilities() {
      for(int i = 0; i < this.options.keyBindings.length; ++i) {
         ((GuiButton)this.buttonList.get(i)).drawButton = this.isKeybindButtonVisible(i);
      }

   }

   @Override
   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.id == 200) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }



//   protected void mouseClicked(int par1, int par2, int par3) {
//      if (this.buttonId >= 0) {
//         this.options.setKeyBinding(this.buttonId, -100 + par3);
//         ((GuiButton)this.buttonList.get(this.buttonId)).displayString = this.options.getOptionDisplayString(this.buttonId);
//         this.buttonId = -1;
//         KeyBinding.resetKeyBindingArrayAndHash();
//      } else {
//         super.mouseClicked(par1, par2, par3);
//      }
//
//   }

   protected void keyTyped(char par1, int par2) {
      if (scrollPane.keyTyped(par1, par2))
      {
         super.keyTyped(par1, par2);
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      this.drawDefaultBackground();
      /* Forge Start: Moved all rendering to GuiControlsScrollPanel
      this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 16777215);
      int var4 = this.getLeftBorder();
      int var5 = 0;

      while(true) {
         while(var5 < this.options.keyBindings.length) {
            if (!this.isKeybindButtonVisible(var5)) {
               ++var5;
            } else {
               boolean var6 = false;

               for(int var7 = 0; var7 < this.options.keyBindings.length; ++var7) {
                  if (var7 != var5 && this.options.keyBindings[var5].keyCode == this.options.keyBindings[var7].keyCode) {
                     var6 = true;
                     break;
                  }
               }

               if (this.buttonId == var5) {
                  ((GuiButton)this.buttonList.get(var5)).displayString = "" + EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + "??? " + EnumChatFormatting.WHITE + "<";
               } else if (var6) {
                  ((GuiButton)this.buttonList.get(var5)).displayString = EnumChatFormatting.RED + this.options.getOptionDisplayString(var5);
               } else {
                  ((GuiButton)this.buttonList.get(var5)).displayString = this.options.getOptionDisplayString(var5);
               }

               this.drawString(this.fontRenderer, this.options.getKeyBindingDescription(var5), this.getKeybindButtonPosX(var5) + 70 + 6, this.getKeybindButtonPosY(var5) + 7, -1);
               ++var5;
            }
         }
      */
      scrollPane.drawScreen(par1, par2, par3);
      drawCenteredString(fontRenderer, screenTitle, width / 2, 4, 0xffffff);
      //Forge End
      super.drawScreen(par1, par2, par3);

   }
}

