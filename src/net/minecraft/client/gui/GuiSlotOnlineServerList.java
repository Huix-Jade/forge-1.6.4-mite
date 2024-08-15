package net.minecraft.client.gui;

import net.minecraft.client.mco.McoServer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

class GuiSlotOnlineServerList extends GuiScreenSelectLocation {
   // $FF: synthetic field
   final GuiScreenOnlineServers field_96294_a;

   public GuiSlotOnlineServerList(GuiScreenOnlineServers var1) {
      super(GuiScreenOnlineServers.func_140037_f(var1), var1.width, var1.height, 32, var1.height - 64, 36);
      this.field_96294_a = var1;
   }

   protected int getSize() {
      return GuiScreenOnlineServers.func_140013_c(this.field_96294_a).size() + 1;
   }

   protected void elementClicked(int var1, boolean var2) {
      if (var1 < GuiScreenOnlineServers.func_140013_c(this.field_96294_a).size()) {
         McoServer var3 = (McoServer)GuiScreenOnlineServers.func_140013_c(this.field_96294_a).get(var1);
         GuiScreenOnlineServers.func_140036_b(this.field_96294_a, var3.field_96408_a);
         if (!GuiScreenOnlineServers.func_140015_g(this.field_96294_a).getSession().getUsername().equals(var3.field_96405_e)) {
            GuiScreenOnlineServers.func_140038_h(this.field_96294_a).displayString = I18n.getString("mco.selectServer.leave");
         } else {
            GuiScreenOnlineServers.func_140038_h(this.field_96294_a).displayString = I18n.getString("mco.selectServer.configure");
         }

         GuiScreenOnlineServers.func_140033_i(this.field_96294_a).enabled = var3.field_96404_d.equals("OPEN") && !var3.field_98166_h;
         if (var2 && GuiScreenOnlineServers.func_140033_i(this.field_96294_a).enabled) {
            GuiScreenOnlineServers.func_140008_c(this.field_96294_a, GuiScreenOnlineServers.func_140041_a(this.field_96294_a));
         }

      }
   }

   protected boolean isSelected(int var1) {
      return var1 == GuiScreenOnlineServers.func_140027_d(this.field_96294_a, GuiScreenOnlineServers.func_140041_a(this.field_96294_a));
   }

   protected boolean func_104086_b(int var1) {
      try {
         return var1 >= 0 && var1 < GuiScreenOnlineServers.func_140013_c(this.field_96294_a).size() && ((McoServer)GuiScreenOnlineServers.func_140013_c(this.field_96294_a).get(var1)).field_96405_e.toLowerCase().equals(GuiScreenOnlineServers.func_104032_j(this.field_96294_a).getSession().getUsername());
      } catch (Exception var3) {
         return false;
      }
   }

   protected int func_130003_b() {
      return this.getSize() * 36;
   }

   protected void func_130004_c() {
      this.field_96294_a.drawDefaultBackground();
   }

   protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
      if (var1 < GuiScreenOnlineServers.func_140013_c(this.field_96294_a).size()) {
         this.func_96292_b(var1, var2, var3, var4, var5);
      }

   }

   private void func_96292_b(int var1, int var2, int var3, int var4, Tessellator var5) {
      McoServer var6 = (McoServer)GuiScreenOnlineServers.func_140013_c(this.field_96294_a).get(var1);
      this.field_96294_a.drawString(GuiScreenOnlineServers.func_140023_k(this.field_96294_a), var6.func_96398_b(), var2 + 2, var3 + 1, 16777215);
      short var7 = 207;
      byte var8 = 1;
      if (var6.field_98166_h) {
         GuiScreenOnlineServers.func_104031_c(this.field_96294_a, var2 + var7, var3 + var8, this.field_104094_d, this.field_104095_e);
      } else if (var6.field_96404_d.equals("CLOSED")) {
         GuiScreenOnlineServers.func_140035_b(this.field_96294_a, var2 + var7, var3 + var8, this.field_104094_d, this.field_104095_e);
      } else if (var6.field_96405_e.equals(GuiScreenOnlineServers.func_140014_l(this.field_96294_a).getSession().getUsername()) && var6.field_104063_i < 7) {
         this.func_96293_a(var1, var2 - 14, var3, var6);
         GuiScreenOnlineServers.func_140031_a(this.field_96294_a, var2 + var7, var3 + var8, this.field_104094_d, this.field_104095_e, var6.field_104063_i);
      } else if (var6.field_96404_d.equals("OPEN")) {
         GuiScreenOnlineServers.func_140020_c(this.field_96294_a, var2 + var7, var3 + var8, this.field_104094_d, this.field_104095_e);
         this.func_96293_a(var1, var2 - 14, var3, var6);
      }

      this.field_96294_a.drawString(GuiScreenOnlineServers.func_140039_m(this.field_96294_a), var6.func_96397_a(), var2 + 2, var3 + 12, 7105644);
      this.field_96294_a.drawString(GuiScreenOnlineServers.func_98079_k(this.field_96294_a), var6.field_96405_e, var2 + 2, var3 + 12 + 11, 5000268);
   }

   private void func_96293_a(int var1, int var2, int var3, McoServer var4) {
      if (var4.field_96403_g != null) {
         synchronized(GuiScreenOnlineServers.func_140029_i()) {
            if (GuiScreenOnlineServers.func_140018_j() < 5 && (!var4.field_96411_l || var4.field_102022_m)) {
               (new ThreadConnectToOnlineServer(this, var4)).start();
            }
         }

         if (var4.field_96414_k != null) {
            this.field_96294_a.drawString(GuiScreenOnlineServers.func_110402_q(this.field_96294_a), var4.field_96414_k, var2 + 215 - GuiScreenOnlineServers.func_140010_p(this.field_96294_a).getStringWidth(var4.field_96414_k), var3 + 1, 8421504);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GuiScreenOnlineServers.func_142023_q(this.field_96294_a).getTextureManager().bindTexture(Gui.icons);
      }
   }
}
