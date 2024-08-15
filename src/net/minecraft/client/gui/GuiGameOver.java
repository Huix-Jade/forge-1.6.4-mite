package net.minecraft.client.gui;

import java.util.Iterator;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class GuiGameOver extends GuiScreen {
   private int cooldownTimer;
   private GuiButton respawn_button;
   private int respawn_countdown = -1;

   public void initGui() {
      this.buttonList.clear();
      if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
         if (this.mc.isIntegratedServerRunning()) {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.getString("deathScreen.deleteWorld")));
         } else {
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.getString("deathScreen.leaveServer")));
         }
      } else {
         this.respawn_button = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 72, I18n.getString("deathScreen.respawn"));
         this.buttonList.add(this.respawn_button);
         this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 96, I18n.getString("deathScreen.titleScreen")));
         if (this.mc.getSession() == null) {
            ((GuiButton)this.buttonList.get(1)).enabled = false;
         }
      }

      if (this.cooldownTimer < 20) {
         GuiButton var2;
         for(Iterator var1 = this.buttonList.iterator(); var1.hasNext(); var2.enabled = false) {
            var2 = (GuiButton)var1.next();
         }

      }
   }

   protected void keyTyped(char par1, int par2) {
   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      switch (par1GuiButton.id) {
         case 1:
            this.mc.thePlayer.respawnPlayer();
            this.mc.displayGuiScreen((GuiScreen)null);
            break;
         case 2:
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld((WorldClient)null);
            this.mc.displayGuiScreen(new GuiMainMenu());
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
      GL11.glPushMatrix();
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      boolean var4 = this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled();
      String var5 = var4 ? I18n.getString("deathScreen.title.hardcore") : I18n.getString("deathScreen.title");
      this.drawCenteredString(this.fontRenderer, var5, this.width / 2 / 2, 30, 16777215);
      GL11.glPopMatrix();
      if (var4) {
         this.drawCenteredString(this.fontRenderer, I18n.getString("deathScreen.hardcoreInfo"), this.width / 2, 144, 16777215);
      } else if (this.respawn_countdown > 0) {
         this.drawCenteredString(this.fontRenderer, I18n.getString("deathScreen.respawnCountdown") + " " + EnumChatFormatting.YELLOW + this.respawn_countdown, this.width / 2, 100, 16777215);
      } else if (this.respawn_countdown == 0) {
         this.actionPerformed(this.respawn_button);
      }

      super.drawScreen(par1, par2, par3);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void updateScreen() {
      super.updateScreen();
      if (this.cooldownTimer < 20) {
         ++this.cooldownTimer;
      }

      GuiButton var2;
      if (this.cooldownTimer == 20) {
         for(Iterator var1 = this.buttonList.iterator(); var1.hasNext(); var2.enabled = true) {
            var2 = (GuiButton)var1.next();
         }
      }

   }

   public void setRespawnCountdown(int respawn_countdown) {
      this.respawn_countdown = respawn_countdown;
   }
}
