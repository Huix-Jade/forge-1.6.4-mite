package net.minecraft.client.gui.mco;

import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;

class GuiScreenMcoWorldTemplateDownloadThread extends Thread {
   // $FF: synthetic field
   final GuiScreenMcoWorldTemplate field_111256_a;

   GuiScreenMcoWorldTemplateDownloadThread(GuiScreenMcoWorldTemplate var1) {
      this.field_111256_a = var1;
   }

   public void run() {
      McoClient var1 = new McoClient(GuiScreenMcoWorldTemplate.func_110382_a(this.field_111256_a).getSession());

      try {
         GuiScreenMcoWorldTemplate.func_110388_a(this.field_111256_a, var1.func_111231_d().field_110736_a);
      } catch (ExceptionMcoService var3) {
         GuiScreenMcoWorldTemplate.func_110392_b(this.field_111256_a).getLogAgent().logSevere(var3.toString());
      }

   }
}
