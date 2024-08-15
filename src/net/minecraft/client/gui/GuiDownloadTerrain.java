package net.minecraft.client.gui;

import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.packet.Packet0KeepAlive;

public class GuiDownloadTerrain extends GuiScreen {
   private NetClientHandler netHandler;
   private int updateCounter;

   public GuiDownloadTerrain(NetClientHandler var1) {
      this.netHandler = var1;
   }

   protected void keyTyped(char var1, int var2) {
   }

   public void initGui() {
      this.buttonList.clear();
   }

   public void updateScreen() {
      ++this.updateCounter;
      if (this.updateCounter % 20 == 0) {
         this.netHandler.addToSendQueue(new Packet0KeepAlive());
      }

      if (this.netHandler != null) {
         this.netHandler.processReadPackets();
      }

   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawBackground(0);
      this.drawCenteredString(this.fontRenderer, I18n.getString("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
      super.drawScreen(var1, var2, var3);
   }
}
