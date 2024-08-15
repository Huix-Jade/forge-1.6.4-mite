package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tessellator;

class SelectionListInvited extends SelectionListBase {
   // $FF: synthetic field
   final GuiScreenConfigureWorld theGuiScreenConfigureWorld;

   public SelectionListInvited(GuiScreenConfigureWorld var1) {
      super(GuiScreenConfigureWorld.getMinecraft(var1), GuiScreenConfigureWorld.func_96271_b(var1), GuiScreenConfigureWorld.func_96274_a(var1, 2), GuiScreenConfigureWorld.func_96269_c(var1), GuiScreenConfigureWorld.func_96274_a(var1, 9) - GuiScreenConfigureWorld.func_96274_a(var1, 2), 12);
      this.theGuiScreenConfigureWorld = var1;
   }

   protected int func_96608_a() {
      return GuiScreenConfigureWorld.func_96266_d(this.theGuiScreenConfigureWorld).field_96402_f.size() + 1;
   }

   protected void func_96615_a(int var1, boolean var2) {
      if (var1 < GuiScreenConfigureWorld.func_96266_d(this.theGuiScreenConfigureWorld).field_96402_f.size()) {
         GuiScreenConfigureWorld.func_96270_b(this.theGuiScreenConfigureWorld, var1);
      }
   }

   protected boolean func_96609_a(int var1) {
      return var1 == GuiScreenConfigureWorld.func_96263_e(this.theGuiScreenConfigureWorld);
   }

   protected int func_96613_b() {
      return this.func_96608_a() * 12;
   }

   protected void func_96611_c() {
   }

   protected void func_96610_a(int var1, int var2, int var3, int var4, Tessellator var5) {
      if (var1 < GuiScreenConfigureWorld.func_96266_d(this.theGuiScreenConfigureWorld).field_96402_f.size()) {
         this.func_98263_b(var1, var2, var3, var4, var5);
      }

   }

   private void func_98263_b(int var1, int var2, int var3, int var4, Tessellator var5) {
      String var6 = (String)GuiScreenConfigureWorld.func_96266_d(this.theGuiScreenConfigureWorld).field_96402_f.get(var1);
      this.theGuiScreenConfigureWorld.drawString(GuiScreenConfigureWorld.func_96273_f(this.theGuiScreenConfigureWorld), var6, var2 + 2, var3 + 1, 16777215);
   }
}
