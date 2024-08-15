package net.minecraft.client.gui.mco;

import net.minecraft.client.gui.GuiScreenSelectLocation;
import net.minecraft.client.mco.PendingInvite;
import net.minecraft.client.renderer.Tessellator;

class GuiScreenPendingInvitationList extends GuiScreenSelectLocation {
   // $FF: synthetic field
   final GuiScreenPendingInvitation field_130120_a;

   public GuiScreenPendingInvitationList(GuiScreenPendingInvitation var1) {
      super(GuiScreenPendingInvitation.func_130054_j(var1), var1.width, var1.height, 32, var1.height - 64, 36);
      this.field_130120_a = var1;
   }

   protected int getSize() {
      return GuiScreenPendingInvitation.func_130042_e(this.field_130120_a).size() + 1;
   }

   protected void elementClicked(int var1, boolean var2) {
      if (var1 < GuiScreenPendingInvitation.func_130042_e(this.field_130120_a).size()) {
         GuiScreenPendingInvitation.func_130053_a(this.field_130120_a, var1);
      }
   }

   protected boolean isSelected(int var1) {
      return var1 == GuiScreenPendingInvitation.func_130049_d(this.field_130120_a);
   }

   protected boolean func_104086_b(int var1) {
      return false;
   }

   protected int func_130003_b() {
      return this.getSize() * 36;
   }

   protected void func_130004_c() {
      this.field_130120_a.drawDefaultBackground();
   }

   protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
      if (var1 < GuiScreenPendingInvitation.func_130042_e(this.field_130120_a).size()) {
         this.func_130119_b(var1, var2, var3, var4, var5);
      }

   }

   private void func_130119_b(int var1, int var2, int var3, int var4, Tessellator var5) {
      PendingInvite var6 = (PendingInvite)GuiScreenPendingInvitation.func_130042_e(this.field_130120_a).get(var1);
      this.field_130120_a.drawString(GuiScreenPendingInvitation.func_130045_k(this.field_130120_a), var6.field_130092_b, var2 + 2, var3 + 1, 16777215);
      this.field_130120_a.drawString(GuiScreenPendingInvitation.func_130052_l(this.field_130120_a), var6.field_130093_c, var2 + 2, var3 + 12, 7105644);
   }
}
