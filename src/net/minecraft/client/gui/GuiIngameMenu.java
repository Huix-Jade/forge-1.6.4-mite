package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.main.Main;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Translator;

public class GuiIngameMenu extends GuiScreen {
   private int updateCounter2;
   private int updateCounter;

   public void initGui() {
      this.updateCounter2 = 0;
      this.buttonList.clear();
      byte var1 = -16;
      boolean var2 = true;
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + var1, I18n.getString("menu.returnToMenu")));
      if (!this.mc.isIntegratedServerRunning()) {
         ((GuiButton)this.buttonList.get(0)).displayString = I18n.getString("menu.disconnect");
         ((GuiButton)this.buttonList.get(0)).is_disconnect_button = true;
      }

      GuiButton button_return = new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + var1, I18n.getString("menu.returnToGame"));
      GuiButton button_options = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + var1, 98, 20, I18n.getString("menu.options"));
      GuiButton button_lan = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + var1, 98, 20, I18n.getString("menu.shareToLan"));
      GuiButton button_achievements = new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + var1, 98, 20, I18n.getString("gui.achievements"));
      GuiButton button_stats = new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + var1, 98, 20, I18n.getString("gui.stats"));
      if (this.mc.thePlayer.isGhost()) {
         button_return.enabled = false;
         button_options.enabled = false;
         button_achievements.enabled = false;
         button_stats.enabled = false;
         button_return.yPosition = -100;
         button_options.yPosition = -100;
         button_achievements.yPosition = -100;
         button_stats.yPosition = -100;
         button_lan.yPosition = -100;
      }

      Minecraft var10001 = this.mc;
      button_lan.enabled = Minecraft.isSingleplayer() && !this.mc.getIntegratedServer().getPublic() && !Minecraft.isInTournamentMode();
      this.buttonList.add(button_return);
      this.buttonList.add(button_options);
      this.buttonList.add(button_lan);
      this.buttonList.add(button_achievements);
      this.buttonList.add(button_stats);
   }

   protected void keyTyped(char par1, int par2) {
      if (!Main.is_MITE_DS) {
         super.keyTyped(par1, par2);
      }

   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      switch (par1GuiButton.id) {
         case 0:
            if (!this.mc.thePlayer.isGhost()) {
               this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            }
            break;
         case 1:
            par1GuiButton.enabled = false;
            this.mc.statFileWriter.readStat(StatList.leaveGameStat, 1);
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld((WorldClient)null);
            this.mc.displayGuiScreen(new GuiMainMenu());
         case 2:
         case 3:
         default:
            break;
         case 4:
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
            this.mc.sndManager.resumeAllSounds();
            break;
         case 5:
            if (!this.mc.thePlayer.isGhost()) {
               this.mc.displayGuiScreen(new GuiAchievements(this.mc.statFileWriter));
            }
            break;
         case 6:
            if (!this.mc.thePlayer.isGhost()) {
               this.mc.displayGuiScreen(new GuiStats(this, this.mc.statFileWriter));
            }
            break;
         case 7:
            if (Minecraft.isInTournamentMode()) {
               this.mc.ingameGUI.getChatGUI().printChatMessage(ChatMessageComponent.createFromTranslationKey("commands.publish.failed").toStringWithFormatting(true));
               this.mc.displayGuiScreen((GuiScreen)null);
               this.mc.setIngameFocus();
               this.mc.sndManager.resumeAllSounds();
               return;
            }

            this.mc.displayGuiScreen(new GuiShareToLan(this));
      }

   }

   public void updateScreen() {
      super.updateScreen();
      ++this.updateCounter;
   }

   public void drawScreen(int par1, int par2, float par3) {
      this.drawDefaultBackground();
      if (Main.is_MITE_DS && this.mc.getIntegratedServer().getPublic()) {
         this.drawCenteredString(this.fontRenderer, "Dedicated Server Is Now Running", this.width / 2, 60, 16777215);
         this.drawCenteredString(this.fontRenderer, "Players Connected: " + (MinecraftServer.getServer().getCurrentPlayerCount() - 1), this.width / 2, 80, 16777215);
      } else {
         this.drawCenteredString(this.fontRenderer, Translator.get("menu.title"), this.width / 2, 40, 16777215);
      }

      super.drawScreen(par1, par2, par3);
   }
}
