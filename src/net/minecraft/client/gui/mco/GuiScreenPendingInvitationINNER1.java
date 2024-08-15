package net.minecraft.client.gui.mco;

import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;

class GuiScreenPendingInvitationINNER1 extends Thread {
   // $FF: synthetic field
   final GuiScreenPendingInvitation field_130121_a;

   GuiScreenPendingInvitationINNER1(GuiScreenPendingInvitation var1) {
      this.field_130121_a = var1;
   }

   public void run() {
      McoClient var1 = new McoClient(GuiScreenPendingInvitation.func_130048_a(this.field_130121_a).getSession());

      try {
         GuiScreenPendingInvitation.func_130043_a(this.field_130121_a, var1.func_130108_f().field_130096_a);
      } catch (ExceptionMcoService var3) {
         GuiScreenPendingInvitation.func_130044_b(this.field_130121_a).getLogAgent().logSevere(var3.toString());
      }

   }
}
