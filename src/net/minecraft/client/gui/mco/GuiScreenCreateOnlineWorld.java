package net.minecraft.client.gui.mco;

import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenLongRunningTask;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.mco.WorldTemplate;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenCreateOnlineWorld extends ScreenWithCallback {
   private GuiScreen field_96260_a;
   private GuiTextField field_96257_c;
   private GuiTextField field_96255_b;
   private String field_98108_c;
   private String field_98109_n;
   private static int field_96253_d;
   private static int field_96261_n = 1;
   private static int field_110357_r = 2;
   private boolean field_96256_r;
   private String field_96254_s = "You must enter a name!";
   private WorldTemplate field_110356_u;

   public GuiScreenCreateOnlineWorld(GuiScreen var1) {
      super.buttonList = Collections.synchronizedList(new ArrayList());
      this.field_96260_a = var1;
   }

   public void updateScreen() {
      this.field_96257_c.updateCursorCounter();
      this.field_98108_c = this.field_96257_c.getText();
      this.field_96255_b.updateCursorCounter();
      this.field_98109_n = this.field_96255_b.getText();
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(field_96253_d, this.width / 2 - 100, this.height / 4 + 120 + 17, 97, 20, I18n.getString("mco.create.world")));
      this.buttonList.add(new GuiButton(field_96261_n, this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20, I18n.getString("gui.cancel")));
      this.field_96257_c = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 65, 200, 20);
      this.field_96257_c.setFocused(true);
      if (this.field_98108_c != null) {
         this.field_96257_c.setText(this.field_98108_c);
      }

      this.field_96255_b = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 111, 200, 20);
      if (this.field_98109_n != null) {
         this.field_96255_b.setText(this.field_98109_n);
      }

      if (this.field_110356_u == null) {
         this.buttonList.add(new GuiButton(field_110357_r, this.width / 2 - 100, 147, 200, 20, I18n.getString("mco.template.default.name")));
      } else {
         this.field_96255_b.setText("");
         this.field_96255_b.setEnabled(false);
         this.field_96255_b.setFocused(false);
         this.buttonList.add(new GuiButton(field_110357_r, this.width / 2 - 100, 147, 200, 20, I18n.getString("mco.template.name") + ": " + this.field_110356_u.field_110732_b));
      }

   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == field_96261_n) {
            this.mc.displayGuiScreen(this.field_96260_a);
         } else if (var1.id == field_96253_d) {
            this.func_96252_h();
         } else if (var1.id == field_110357_r) {
            this.mc.displayGuiScreen(new GuiScreenMcoWorldTemplate(this, this.field_110356_u));
         }

      }
   }

   private void func_96252_h() {
      if (this.func_96249_i()) {
         TaskWorldCreation var1 = new TaskWorldCreation(this, this.field_96257_c.getText(), "Minecraft Realms Server", this.field_98109_n, this.field_110356_u);
         GuiScreenLongRunningTask var2 = new GuiScreenLongRunningTask(this.mc, this.field_96260_a, var1);
         var2.func_98117_g();
         this.mc.displayGuiScreen(var2);
      }

   }

   private boolean func_96249_i() {
      this.field_96256_r = this.field_96257_c.getText() == null || this.field_96257_c.getText().trim().equals("");
      return !this.field_96256_r;
   }

   protected void keyTyped(char var1, int var2) {
      this.field_96257_c.textboxKeyTyped(var1, var2);
      this.field_96255_b.textboxKeyTyped(var1, var2);
      if (var2 == 15) {
         this.field_96257_c.setFocused(!this.field_96257_c.isFocused());
         this.field_96255_b.setFocused(!this.field_96255_b.isFocused());
      }

      if (var2 == 28 || var2 == 156) {
         this.actionPerformed((GuiButton)this.buttonList.get(0));
      }

   }

   protected void mouseClicked(int var1, int var2, int var3) {
      super.mouseClicked(var1, var2, var3);
      this.field_96257_c.mouseClicked(var1, var2, var3);
      this.field_96255_b.mouseClicked(var1, var2, var3);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.getString("mco.selectServer.create"), this.width / 2, 11, 16777215);
      this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.name"), this.width / 2 - 100, 52, 10526880);
      this.drawString(this.fontRenderer, I18n.getString("mco.create.world.seed"), this.width / 2 - 100, 98, 10526880);
      if (this.field_96256_r) {
         this.drawCenteredString(this.fontRenderer, this.field_96254_s, this.width / 2, 167, 16711680);
      }

      this.field_96257_c.drawTextBox();
      this.field_96255_b.drawTextBox();
      super.drawScreen(var1, var2, var3);
   }

   public void func_110355_a(WorldTemplate var1) {
      this.field_110356_u = var1;
   }

   // $FF: synthetic method
   static Minecraft func_96248_a(GuiScreenCreateOnlineWorld var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static GuiScreen func_96247_b(GuiScreenCreateOnlineWorld var0) {
      return var0.field_96260_a;
   }

   // $FF: synthetic method
   static Minecraft func_96246_c(GuiScreenCreateOnlineWorld var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130026_d(GuiScreenCreateOnlineWorld var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130027_e(GuiScreenCreateOnlineWorld var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130028_f(GuiScreenCreateOnlineWorld var0) {
      return var0.mc;
   }

	@Override
	void func_110354_a(Object var1) {
		this.func_110355_a((WorldTemplate)var1);
	}
}
