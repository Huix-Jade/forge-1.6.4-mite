package net.minecraft.client.gui.mco;

import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;

class GuiScreenBackupDownloadThread extends Thread {
   // $FF: synthetic field
   final GuiScreenBackup field_111250_a;

   GuiScreenBackupDownloadThread(GuiScreenBackup var1) {
      this.field_111250_a = var1;
   }

   public void run() {
      McoClient var1 = new McoClient(GuiScreenBackup.func_110366_a(this.field_111250_a).getSession());

      try {
         GuiScreenBackup.func_110373_a(this.field_111250_a, var1.func_111232_c(GuiScreenBackup.func_110367_b(this.field_111250_a)).field_111223_a);
      } catch (ExceptionMcoService var3) {
         GuiScreenBackup.func_130030_c(this.field_111250_a).getLogAgent().logSevere(var3.toString());
      }

   }
}
