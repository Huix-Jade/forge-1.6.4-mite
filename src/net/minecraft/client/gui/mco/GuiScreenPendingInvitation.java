package net.minecraft.client.gui.mco;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenPendingInvitation extends GuiScreen {
   private final GuiScreen field_130061_a;
   private GuiScreenPendingInvitationList field_130059_b;
   private List field_130060_c = Lists.newArrayList();
   private int field_130058_d = -1;

   public GuiScreenPendingInvitation(GuiScreen var1) {
      this.field_130061_a = var1;
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.field_130059_b = new GuiScreenPendingInvitationList(this);
      (new GuiScreenPendingInvitationINNER1(this)).start();
      this.func_130050_g();
   }

   private void func_130050_g() {
      this.buttonList.add(new GuiButton(1, this.width / 2 - 154, this.height - 52, 153, 20, I18n.getString("mco.invites.button.accept")));
      this.buttonList.add(new GuiButton(2, this.width / 2 + 6, this.height - 52, 153, 20, I18n.getString("mco.invites.button.reject")));
      this.buttonList.add(new GuiButton(0, this.width / 2 - 75, this.height - 28, 153, 20, I18n.getString("gui.back")));
   }

   public void updateScreen() {
      super.updateScreen();
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == 1) {
            this.func_130051_i();
         } else if (var1.id == 0) {
            this.mc.displayGuiScreen(this.field_130061_a);
         } else if (var1.id == 2) {
            this.func_130057_h();
         } else {
            this.field_130059_b.actionPerformed(var1);
         }

      }
   }

   private void func_130057_h() {
      if (this.field_130058_d >= 0 && this.field_130058_d < this.field_130060_c.size()) {
         (new GuiScreenPendingInvitationINNER2(this)).start();
      }

   }

   private void func_130051_i() {
      if (this.field_130058_d >= 0 && this.field_130058_d < this.field_130060_c.size()) {
         (new GuiScreenPendingInvitationINNER3(this)).start();
      }

   }

   private void func_130047_j() {
      int var1 = this.field_130058_d;
      if (this.field_130060_c.size() - 1 == this.field_130058_d) {
         --this.field_130058_d;
      }

      this.field_130060_c.remove(var1);
      if (this.field_130060_c.size() == 0) {
         this.field_130058_d = -1;
      }

   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.field_130059_b.drawScreen(var1, var2, var3);
      this.drawCenteredString(this.fontRenderer, I18n.getString("mco.invites.title"), this.width / 2, 20, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   // $FF: synthetic method
   static Minecraft func_130048_a(GuiScreenPendingInvitation var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static List func_130043_a(GuiScreenPendingInvitation var0, List var1) {
      return var0.field_130060_c = var1;
   }

   // $FF: synthetic method
   static Minecraft func_130044_b(GuiScreenPendingInvitation var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130041_c(GuiScreenPendingInvitation var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static int func_130049_d(GuiScreenPendingInvitation var0) {
      return var0.field_130058_d;
   }

   // $FF: synthetic method
   static List func_130042_e(GuiScreenPendingInvitation var0) {
      return var0.field_130060_c;
   }

   // $FF: synthetic method
   static void func_130040_f(GuiScreenPendingInvitation var0) {
      var0.func_130047_j();
   }

   // $FF: synthetic method
   static Minecraft func_130056_g(GuiScreenPendingInvitation var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130046_h(GuiScreenPendingInvitation var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130055_i(GuiScreenPendingInvitation var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130054_j(GuiScreenPendingInvitation var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static int func_130053_a(GuiScreenPendingInvitation var0, int var1) {
      return var0.field_130058_d = var1;
   }

   // $FF: synthetic method
   static FontRenderer func_130045_k(GuiScreenPendingInvitation var0) {
      return var0.fontRenderer;
   }

   // $FF: synthetic method
   static FontRenderer func_130052_l(GuiScreenPendingInvitation var0) {
      return var0.fontRenderer;
   }
}
