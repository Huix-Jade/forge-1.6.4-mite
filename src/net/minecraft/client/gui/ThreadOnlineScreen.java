package net.minecraft.client.gui;

import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;

class ThreadOnlineScreen extends Thread {
   // $FF: synthetic field
   final GuiScreenOnlineServers field_98173_a;

   ThreadOnlineScreen(GuiScreenOnlineServers var1) {
      this.field_98173_a = var1;
   }

   public void run() {
      try {
         McoServer var1 = GuiScreenOnlineServers.func_140011_a(this.field_98173_a, GuiScreenOnlineServers.func_140041_a(this.field_98173_a));
         if (var1 != null) {
            McoClient var2 = new McoClient(GuiScreenOnlineServers.func_98075_b(this.field_98173_a).getSession());
            GuiScreenOnlineServers.func_140040_h().func_140058_a(var1);
            GuiScreenOnlineServers.func_140013_c(this.field_98173_a).remove(var1);
            var2.func_140055_c(var1.field_96408_a);
            GuiScreenOnlineServers.func_140040_h().func_140058_a(var1);
            GuiScreenOnlineServers.func_140013_c(this.field_98173_a).remove(var1);
            GuiScreenOnlineServers.func_140017_d(this.field_98173_a);
         }
      } catch (ExceptionMcoService var3) {
         GuiScreenOnlineServers.func_98076_f(this.field_98173_a).getLogAgent().logSevere(var3.toString());
      }

   }
}
