package net.minecraft.client.gui;

import java.io.UnsupportedEncodingException;
import net.minecraft.client.gui.mco.GuiScreenResetWorld;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenEditOnlineWorld extends GuiScreen {
   private GuiScreen field_96204_a;
   private GuiScreen field_96202_b;
   private GuiTextField field_96203_c;
   private GuiTextField field_96201_d;
   private McoServer field_96205_n;
   private GuiButton field_96206_o;
   private int field_104054_p;
   private int field_104053_q;
   private int field_104052_r;
   private GuiScreenOnlineServersSubscreen field_104051_s;

   public GuiScreenEditOnlineWorld(GuiScreen var1, GuiScreen var2, McoServer var3) {
      this.field_96204_a = var1;
      this.field_96202_b = var2;
      this.field_96205_n = var3;
   }

   public void updateScreen() {
      this.field_96201_d.updateCursorCounter();
      this.field_96203_c.updateCursorCounter();
   }

   public void initGui() {
      this.field_104054_p = this.width / 4;
      this.field_104053_q = this.width / 4 - 2;
      this.field_104052_r = this.width / 2 + 4;
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(this.field_96206_o = new GuiButton(0, this.field_104054_p, this.height / 4 + 120 + 22, this.field_104053_q, 20, I18n.getString("mco.configure.world.buttons.done")));
      this.buttonList.add(new GuiButton(1, this.field_104052_r, this.height / 4 + 120 + 22, this.field_104053_q, 20, I18n.getString("gui.cancel")));
      this.field_96201_d = new GuiTextField(this.fontRenderer, this.field_104054_p, 56, 212, 20);
      this.field_96201_d.setFocused(true);
      this.field_96201_d.setMaxStringLength(32);
      this.field_96201_d.setText(this.field_96205_n.func_96398_b());
      this.field_96203_c = new GuiTextField(this.fontRenderer, this.field_104054_p, 96, 212, 20);
      this.field_96203_c.setMaxStringLength(32);
      this.field_96203_c.setText(this.field_96205_n.func_96397_a());
      this.field_104051_s = new GuiScreenOnlineServersSubscreen(this.width, this.height, this.field_104054_p, 122, this.field_96205_n.field_110729_i, this.field_96205_n.field_110728_j);
      this.buttonList.addAll(this.field_104051_s.field_104079_a);
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == 1) {
            this.mc.displayGuiScreen(this.field_96204_a);
         } else if (var1.id == 0) {
            this.func_96200_g();
         } else if (var1.id == 2) {
            this.mc.displayGuiScreen(new GuiScreenResetWorld(this, this.field_96205_n));
         } else {
            this.field_104051_s.func_104069_a(var1);
         }

      }
   }

   private void func_96200_g() {
      McoClient var1 = new McoClient(this.mc.getSession());

      try {
         String var2 = this.field_96203_c.getText() != null && !this.field_96203_c.getText().trim().equals("") ? this.field_96203_c.getText() : null;
         var1.func_96384_a(this.field_96205_n.field_96408_a, this.field_96201_d.getText(), var2, this.field_104051_s.field_104076_e, this.field_104051_s.field_104073_f);
         this.field_96205_n.func_96399_a(this.field_96201_d.getText());
         this.field_96205_n.func_96400_b(this.field_96203_c.getText());
         this.field_96205_n.field_110729_i = this.field_104051_s.field_104076_e;
         this.field_96205_n.field_110728_j = this.field_104051_s.field_104073_f;
         this.mc.displayGuiScreen(new GuiScreenConfigureWorld(this.field_96202_b, this.field_96205_n));
      } catch (ExceptionMcoService var3) {
         this.mc.getLogAgent().logSevere(var3.toString());
      } catch (UnsupportedEncodingException var4) {
         this.mc.getLogAgent().logWarning("Realms: " + var4.getLocalizedMessage());
      }

   }

   protected void keyTyped(char var1, int var2) {
      this.field_96201_d.textboxKeyTyped(var1, var2);
      this.field_96203_c.textboxKeyTyped(var1, var2);
      if (var2 == 15) {
         this.field_96201_d.setFocused(!this.field_96201_d.isFocused());
         this.field_96203_c.setFocused(!this.field_96203_c.isFocused());
      }

      if (var2 == 28 || var2 == 156) {
         this.func_96200_g();
      }

      this.field_96206_o.enabled = this.field_96201_d.getText() != null && !this.field_96201_d.getText().trim().equals("");
   }

   protected void mouseClicked(int var1, int var2, int var3) {
      super.mouseClicked(var1, var2, var3);
      this.field_96203_c.mouseClicked(var1, var2, var3);
      this.field_96201_d.mouseClicked(var1, var2, var3);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.getString("mco.configure.world.edit.title"), this.width / 2, 17, 16777215);
      this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.name"), this.field_104054_p, 43, 10526880);
      this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.description"), this.field_104054_p, 84, 10526880);
      this.field_96201_d.drawTextBox();
      this.field_96203_c.drawTextBox();
      this.field_104051_s.func_104071_a(this, this.fontRenderer);
      super.drawScreen(var1, var2, var3);
   }
}
