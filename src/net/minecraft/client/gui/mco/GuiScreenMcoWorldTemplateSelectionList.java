package net.minecraft.client.gui.mco;

import net.minecraft.client.gui.GuiScreenSelectLocation;
import net.minecraft.client.mco.WorldTemplate;
import net.minecraft.client.renderer.Tessellator;

class GuiScreenMcoWorldTemplateSelectionList extends GuiScreenSelectLocation {
   // $FF: synthetic field
   final GuiScreenMcoWorldTemplate field_111245_a;

   public GuiScreenMcoWorldTemplateSelectionList(GuiScreenMcoWorldTemplate var1) {
      super(GuiScreenMcoWorldTemplate.func_130066_c(var1), var1.width, var1.height, 32, var1.height - 64, 36);
      this.field_111245_a = var1;
   }

   protected int getSize() {
      return GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).size() + 1;
   }

   protected void elementClicked(int var1, boolean var2) {
      if (var1 < GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).size()) {
         GuiScreenMcoWorldTemplate.func_130064_a(this.field_111245_a, var1);
         GuiScreenMcoWorldTemplate.func_130065_a(this.field_111245_a, (WorldTemplate)null);
      }
   }

   protected boolean isSelected(int var1) {
      if (GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).size() == 0) {
         return false;
      } else if (var1 >= GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).size()) {
         return false;
      } else if (GuiScreenMcoWorldTemplate.func_130067_e(this.field_111245_a) != null) {
         return GuiScreenMcoWorldTemplate.func_130067_e(this.field_111245_a).field_110732_b.equals(((WorldTemplate)GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).get(var1)).field_110732_b);
      } else {
         return var1 == GuiScreenMcoWorldTemplate.func_130062_f(this.field_111245_a);
      }
   }

   protected boolean func_104086_b(int var1) {
      return false;
   }

   protected int func_130003_b() {
      return this.getSize() * 36;
   }

   protected void func_130004_c() {
      this.field_111245_a.drawDefaultBackground();
   }

   protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
      if (var1 < GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).size()) {
         this.func_111244_b(var1, var2, var3, var4, var5);
      }

   }

   private void func_111244_b(int var1, int var2, int var3, int var4, Tessellator var5) {
      WorldTemplate var6 = (WorldTemplate)GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).get(var1);
      this.field_111245_a.drawString(GuiScreenMcoWorldTemplate.func_110389_g(this.field_111245_a), var6.field_110732_b, var2 + 2, var3 + 1, 16777215);
      this.field_111245_a.drawString(GuiScreenMcoWorldTemplate.func_110387_h(this.field_111245_a), var6.field_110731_d, var2 + 2, var3 + 12, 7105644);
      this.field_111245_a.drawString(GuiScreenMcoWorldTemplate.func_110384_i(this.field_111245_a), var6.field_110733_c, var2 + 2 + 207 - GuiScreenMcoWorldTemplate.func_130063_j(this.field_111245_a).getStringWidth(var6.field_110733_c), var3 + 1, 5000268);
   }
}
