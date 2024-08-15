package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tessellator;

class GuiSnooperList extends GuiSlot {
   // $FF: synthetic field
   final GuiSnooper snooperGui;

   public GuiSnooperList(GuiSnooper var1) {
      super(var1.mc, var1.width, var1.height, 80, var1.height - 40, var1.fontRenderer.FONT_HEIGHT + 1);
      this.snooperGui = var1;
   }

   protected int getSize() {
      return GuiSnooper.func_74095_a(this.snooperGui).size();
   }

   protected void elementClicked(int var1, boolean var2) {
   }

   protected boolean isSelected(int var1) {
      return false;
   }

   protected void drawBackground() {
   }

   protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
      this.snooperGui.fontRenderer.drawString((String)GuiSnooper.func_74095_a(this.snooperGui).get(var1), 10, var3, 16777215);
      this.snooperGui.fontRenderer.drawString((String)GuiSnooper.func_74094_b(this.snooperGui).get(var1), 230, var3, 16777215);
   }

   protected int getScrollBarX() {
      return this.snooperGui.width - 10;
   }
}
