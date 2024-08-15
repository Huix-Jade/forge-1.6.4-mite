package net.minecraft.client.gui.mco;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenConfirmation;
import net.minecraft.client.gui.GuiScreenLongRunningTask;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.mco.GuiScreenConfirmationType;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.mco.WorldTemplate;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenResetWorld extends ScreenWithCallback {
   private GuiScreen field_96152_a;
   private McoServer field_96150_b;
   private GuiTextField field_96151_c;
   private final int field_96149_d = 1;
   private final int field_96153_n = 2;
   private static int field_110360_p = 3;
   private WorldTemplate field_110359_q;
   private GuiButton field_96154_o;

   public GuiScreenResetWorld(GuiScreen var1, McoServer var2) {
      this.field_96152_a = var1;
      this.field_96150_b = var2;
   }

   public void updateScreen() {
      this.field_96151_c.updateCursorCounter();
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(this.field_96154_o = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, 97, 20, I18n.getString("mco.configure.world.buttons.reset")));
      this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height / 4 + 120 + 12, 97, 20, I18n.getString("gui.cancel")));
      this.field_96151_c = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 99, 200, 20);
      this.field_96151_c.setFocused(true);
      this.field_96151_c.setMaxStringLength(32);
      this.field_96151_c.setText("");
      if (this.field_110359_q == null) {
         this.buttonList.add(new GuiButton(field_110360_p, this.width / 2 - 100, 125, 200, 20, I18n.getString("mco.template.default.name")));
      } else {
         this.field_96151_c.setText("");
         this.field_96151_c.setEnabled(false);
         this.field_96151_c.setFocused(false);
         this.buttonList.add(new GuiButton(field_110360_p, this.width / 2 - 100, 125, 200, 20, I18n.getString("mco.template.name") + ": " + this.field_110359_q.field_110732_b));
      }

   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void keyTyped(char var1, int var2) {
      this.field_96151_c.textboxKeyTyped(var1, var2);
      if (var2 == 28 || var2 == 156) {
         this.actionPerformed(this.field_96154_o);
      }

   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == 2) {
            this.mc.displayGuiScreen(this.field_96152_a);
         } else if (var1.id == 1) {
            String var2 = I18n.getString("mco.configure.world.reset.question.line1");
            String var3 = I18n.getString("mco.configure.world.reset.question.line2");
            this.mc.displayGuiScreen(new GuiScreenConfirmation(this, GuiScreenConfirmationType.Warning, var2, var3, 1));
         } else if (var1.id == field_110360_p) {
            this.mc.displayGuiScreen(new GuiScreenMcoWorldTemplate(this, this.field_110359_q));
         }

      }
   }

   public void confirmClicked(boolean var1, int var2) {
      if (var1 && var2 == 1) {
         this.func_140006_g();
      } else {
         this.mc.displayGuiScreen(this);
      }

   }

   private void func_140006_g() {
      TaskResetWorld var1 = new TaskResetWorld(this, this.field_96150_b.field_96408_a, this.field_96151_c.getText(), this.field_110359_q);
      GuiScreenLongRunningTask var2 = new GuiScreenLongRunningTask(this.mc, this.field_96152_a, var1);
      var2.func_98117_g();
      this.mc.displayGuiScreen(var2);
   }

   protected void mouseClicked(int var1, int var2, int var3) {
      super.mouseClicked(var1, var2, var3);
      this.field_96151_c.mouseClicked(var1, var2, var3);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.getString("mco.reset.world.title"), this.width / 2, 17, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.getString("mco.reset.world.warning"), this.width / 2, 56, 16711680);
      this.drawString(this.fontRenderer, I18n.getString("mco.reset.world.seed"), this.width / 2 - 100, 86, 10526880);
      this.field_96151_c.drawTextBox();
      super.drawScreen(var1, var2, var3);
   }

   void func_110358_a(WorldTemplate var1) {
      this.field_110359_q = var1;
   }

   // $FF: synthetic method
   static GuiScreen func_96148_a(GuiScreenResetWorld var0) {
      return var0.field_96152_a;
   }

   // $FF: synthetic method
   static Minecraft func_96147_b(GuiScreenResetWorld var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130025_c(GuiScreenResetWorld var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130024_d(GuiScreenResetWorld var0) {
      return var0.mc;
   }

   @Override
   void func_110354_a(Object var1) {
      this.func_110358_a((WorldTemplate)var1);
   }
}
