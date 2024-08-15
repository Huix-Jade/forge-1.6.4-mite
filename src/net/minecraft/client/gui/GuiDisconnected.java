package net.minecraft.client.gui;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Translator;
import net.minecraft.world.World;

public class GuiDisconnected extends GuiScreen {
   private String errorMessage;
   private String errorDetail;
   private Object[] field_74247_c;
   private List field_74245_d;
   private final GuiScreen field_98095_n;
   private long next_alarm_ms;
   public static int message_type;

   public GuiDisconnected(GuiScreen par1GuiScreen, String par2Str, String par3Str, Object... par4ArrayOfObj) {
      this.field_98095_n = par1GuiScreen;
      this.errorMessage = I18n.getString(par2Str);
      this.errorDetail = par3Str;
      this.field_74247_c = par4ArrayOfObj;
   }

   protected void keyTyped(char par1, int par2) {
   }

   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.getString("gui.toMenu")));
      if (this.field_74247_c != null) {
         this.field_74245_d = this.fontRenderer.listFormattedStringToWidth(I18n.getStringParams(this.errorDetail, this.field_74247_c), this.width - 50);
      } else {
         this.field_74245_d = this.fontRenderer.listFormattedStringToWidth(I18n.getString(this.errorDetail), this.width - 50);
      }

   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.id == 0) {
         Minecraft.soonest_reconnection_time = 0L;
         this.mc.displayGuiScreen(this.field_98095_n);
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.errorMessage, this.width / 2, this.height / 2 - 50, 11184810);
      int var4 = this.height / 2 - 30;
      if (Minecraft.soonest_reconnection_time > 0L) {
         this.drawCenteredString(this.fontRenderer, Translator.get("disconnect.disconnectionPenalty"), this.width / 2, var4, 16777215);
         var4 += this.fontRenderer.FONT_HEIGHT * 5;
         String am_pm = World.getHourOfDayAMPM(Minecraft.adjusted_hour_of_disconnection);
         int hour_of_latest_reconnection = World.getHourOfLatestReconnection();
         if (am_pm.equals("NOON")) {
            am_pm = "noon";
         }

         String msg = "";
         if (Minecraft.adjusted_hour_of_disconnection == hour_of_latest_reconnection) {
            msg = Translator.getFormatted("disconnect.reconnectAt", am_pm);
         } else if (message_type == 1) {
            msg = Translator.getFormatted("disconnect.reconnectBetween", am_pm, World.getHourOfDayAMPM(hour_of_latest_reconnection));
         } else if (message_type == 2) {
            msg = Translator.getFormatted("disconnect.reconnectAtOrBetween", World.getHourOfDayAMPM(hour_of_latest_reconnection), am_pm, World.getHourOfDayAMPM(hour_of_latest_reconnection));
         }

         this.drawCenteredString(this.fontRenderer, msg + ".", this.width / 2, var4, 11184810);
      } else if (this.field_74245_d != null) {
         for(Iterator var5 = this.field_74245_d.iterator(); var5.hasNext(); var4 += this.fontRenderer.FONT_HEIGHT) {
            String var6 = (String)var5.next();
            this.drawCenteredString(this.fontRenderer, var6, this.width / 2, var4, 16777215);
         }
      }

      super.drawScreen(par1, par2, par3);
   }
}
