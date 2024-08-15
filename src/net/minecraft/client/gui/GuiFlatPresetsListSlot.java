package net.minecraft.client.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

class GuiFlatPresetsListSlot extends GuiSlot {
   public int field_82459_a;
   // $FF: synthetic field
   final GuiFlatPresets flatPresetsGui;

   public GuiFlatPresetsListSlot(GuiFlatPresets var1) {
      super(var1.mc, var1.width, var1.height, 80, var1.height - 37, 24);
      this.flatPresetsGui = var1;
      this.field_82459_a = -1;
   }

   private void func_82457_a(int var1, int var2, int var3) {
      this.func_82456_d(var1 + 1, var2 + 1);
      GL11.glEnable(32826);
      RenderHelper.enableGUIStandardItemLighting();
      GuiFlatPresets.getPresetIconRenderer().renderItemIntoGUI(this.flatPresetsGui.fontRenderer, this.flatPresetsGui.mc.getTextureManager(), new ItemStack(var3, 1, 0), var1 + 2, var2 + 2);
      RenderHelper.disableStandardItemLighting();
      GL11.glDisable(32826);
   }

   private void func_82456_d(int var1, int var2) {
      this.func_82455_b(var1, var2, 0, 0);
   }

   private void func_82455_b(int var1, int var2, int var3, int var4) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.flatPresetsGui.mc.getTextureManager().bindTexture(Gui.statIcons);
      float var5 = 0.0078125F;
      float var6 = 0.0078125F;
      boolean var7 = true;
      boolean var8 = true;
      Tessellator var9 = Tessellator.instance;
      var9.startDrawingQuads();
      var9.addVertexWithUV((double)(var1 + 0), (double)(var2 + 18), (double)this.flatPresetsGui.zLevel, (double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F));
      var9.addVertexWithUV((double)(var1 + 18), (double)(var2 + 18), (double)this.flatPresetsGui.zLevel, (double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F));
      var9.addVertexWithUV((double)(var1 + 18), (double)(var2 + 0), (double)this.flatPresetsGui.zLevel, (double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F));
      var9.addVertexWithUV((double)(var1 + 0), (double)(var2 + 0), (double)this.flatPresetsGui.zLevel, (double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F));
      var9.draw();
   }

   protected int getSize() {
      return GuiFlatPresets.getPresets().size();
   }

   protected void elementClicked(int var1, boolean var2) {
      this.field_82459_a = var1;
      this.flatPresetsGui.func_82296_g();
      GuiFlatPresets.func_82298_b(this.flatPresetsGui).setText(((GuiFlatPresetsItem)GuiFlatPresets.getPresets().get(GuiFlatPresets.func_82292_a(this.flatPresetsGui).field_82459_a)).presetData);
   }

   protected boolean isSelected(int var1) {
      return var1 == this.field_82459_a;
   }

   protected void drawBackground() {
   }

   protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
      GuiFlatPresetsItem var6 = (GuiFlatPresetsItem)GuiFlatPresets.getPresets().get(var1);
      this.func_82457_a(var2, var3, var6.iconId);
      this.flatPresetsGui.fontRenderer.drawString(var6.presetName, var2 + 18 + 5, var3 + 6, 16777215);
   }
}
