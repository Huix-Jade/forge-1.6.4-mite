package net.minecraft.client.gui;

import net.minecraft.client.main.Main;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;

public class GuiMITEDS extends GuiChat {
   public void initGui() {
      super.initGui();
      GuiButton button_leave = new GuiButton(1, this.width / 2 - 100, this.height - 40, I18n.getString("menu.returnToMenu"));
      button_leave.yPosition = 80;
      this.buttonList.add(button_leave);
   }

   protected void keyTyped(char par1, int par2) {
      if (par2 != 1) {
         if (par2 != 28 && par2 != 156) {
            super.keyTyped(par1, par2);
         } else {
            String var3 = this.inputField.getText().trim();
            if (var3.length() > 0) {
               this.mc.thePlayer.sendChatMessage(var3);
            }

            this.inputField.setText("");
            this.mc.ingameGUI.getChatGUI().resetScroll();
         }
      }

   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.id == 1) {
         par1GuiButton.enabled = false;
         this.mc.statFileWriter.readStat(StatList.leaveGameStat, 1);
         this.mc.theWorld.sendQuittingDisconnectingPacket();
         this.mc.loadWorld((WorldClient)null);
         this.mc.displayGuiScreen(new GuiMainMenu());
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
      this.mc.ingameGUI.drawChatForMITEDS();
      if (Main.is_MITE_DS && this.mc.getIntegratedServer().getPublic()) {
         this.drawCenteredString(this.fontRenderer, "Dedicated Server Is Now Running", this.width / 2, 30, 16777215);
      } else {
         this.drawCenteredString(this.fontRenderer, "Problem Starting Dedicated Server", this.width / 2, 30, 16777215);
      }

      this.drawCenteredString(this.fontRenderer, "Players Connected: " + (MinecraftServer.getServer().getCurrentPlayerCount() - 1), this.width / 2, 50, 16777215);
      super.drawScreen(par1, par2, par3);
   }
}
