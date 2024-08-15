package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.packet.Packet19EntityAction;
import net.minecraft.stats.StatList;

public class GuiSleepMP extends GuiChat {
   public void initGui() {
      super.initGui();
      if (Minecraft.is_dedicated_server_running) {
         int center_x = this.width / 2;
         this.buttonList.add(new GuiButton(1, center_x - 91, this.height - 44, 89, 20, I18n.getString("multiplayer.stopSleeping")));
         this.buttonList.add(new GuiButton(2, center_x + 2, this.height - 44, 89, 20, I18n.getString("menu.disconnect")));
      }

   }

   protected void keyTyped(char par1, int par2) {
      if (par2 == 1) {
         this.wakeEntity();
      } else if (par2 != 28 && par2 != 156) {
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

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.id == 1) {
         this.wakeEntity();
      } else if (par1GuiButton.id == 2) {
         par1GuiButton.enabled = false;
         this.mc.statFileWriter.readStat(StatList.leaveGameStat, 1);
         this.mc.theWorld.sendQuittingDisconnectingPacket();
         this.mc.loadWorld((WorldClient)null);
         this.mc.displayGuiScreen(new GuiMainMenu());
      } else {
         super.actionPerformed(par1GuiButton);
      }

   }

   private void wakeEntity() {
      NetClientHandler var1 = this.mc.thePlayer.sendQueue;
      var1.addToSendQueue(new Packet19EntityAction(this.mc.thePlayer, 3));
   }
}
