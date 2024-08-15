package net.minecraft.client.gui.mco;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.mco.WorldTemplate;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenMcoWorldTemplate extends GuiScreen {
   private final ScreenWithCallback field_110401_a;
   private WorldTemplate field_110398_b;
   private List field_110399_c = Collections.emptyList();
   private GuiScreenMcoWorldTemplateSelectionList field_110396_d;
   private int field_110397_e = -1;
   private GuiButton field_110400_p;

   public GuiScreenMcoWorldTemplate(ScreenWithCallback var1, WorldTemplate var2) {
      this.field_110401_a = var1;
      this.field_110398_b = var2;
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.field_110396_d = new GuiScreenMcoWorldTemplateSelectionList(this);
      (new GuiScreenMcoWorldTemplateDownloadThread(this)).start();
      this.func_110385_g();
   }

   private void func_110385_g() {
      this.buttonList.add(new GuiButton(0, this.width / 2 + 6, this.height - 52, 153, 20, I18n.getString("gui.cancel")));
      this.buttonList.add(this.field_110400_p = new GuiButton(1, this.width / 2 - 154, this.height - 52, 153, 20, I18n.getString("mco.template.button.select")));
   }

   public void updateScreen() {
      super.updateScreen();
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == 1) {
            this.func_110394_h();
         } else if (var1.id == 0) {
            this.field_110401_a.func_110354_a((Object)null);
            this.mc.displayGuiScreen(this.field_110401_a);
         } else {
            this.field_110396_d.actionPerformed(var1);
         }

      }
   }

   private void func_110394_h() {
      if (this.field_110397_e >= 0 && this.field_110397_e < this.field_110399_c.size()) {
         this.field_110401_a.func_110354_a(this.field_110399_c.get(this.field_110397_e));
         this.mc.displayGuiScreen(this.field_110401_a);
      }

   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.field_110396_d.drawScreen(var1, var2, var3);
      this.drawCenteredString(this.fontRenderer, I18n.getString("mco.template.title"), this.width / 2, 20, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   // $FF: synthetic method
   static Minecraft func_110382_a(GuiScreenMcoWorldTemplate var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static List func_110388_a(GuiScreenMcoWorldTemplate var0, List var1) {
      return var0.field_110399_c = var1;
   }

   // $FF: synthetic method
   static Minecraft func_110392_b(GuiScreenMcoWorldTemplate var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130066_c(GuiScreenMcoWorldTemplate var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static List func_110395_c(GuiScreenMcoWorldTemplate var0) {
      return var0.field_110399_c;
   }

   // $FF: synthetic method
   static int func_130064_a(GuiScreenMcoWorldTemplate var0, int var1) {
      return var0.field_110397_e = var1;
   }

   // $FF: synthetic method
   static WorldTemplate func_130065_a(GuiScreenMcoWorldTemplate var0, WorldTemplate var1) {
      return var0.field_110398_b = var1;
   }

   // $FF: synthetic method
   static WorldTemplate func_130067_e(GuiScreenMcoWorldTemplate var0) {
      return var0.field_110398_b;
   }

   // $FF: synthetic method
   static int func_130062_f(GuiScreenMcoWorldTemplate var0) {
      return var0.field_110397_e;
   }

   // $FF: synthetic method
   static FontRenderer func_110389_g(GuiScreenMcoWorldTemplate var0) {
      return var0.fontRenderer;
   }

   // $FF: synthetic method
   static FontRenderer func_110387_h(GuiScreenMcoWorldTemplate var0) {
      return var0.fontRenderer;
   }

   // $FF: synthetic method
   static FontRenderer func_110384_i(GuiScreenMcoWorldTemplate var0) {
      return var0.fontRenderer;
   }

   // $FF: synthetic method
   static FontRenderer func_130063_j(GuiScreenMcoWorldTemplate var0) {
      return var0.fontRenderer;
   }
}
