package net.minecraft.client.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.FlatLayerInfo;
import org.lwjgl.opengl.GL11;

class GuiCreateFlatWorldListSlot extends GuiSlot {
   public int field_82454_a;
   // $FF: synthetic field
   final GuiCreateFlatWorld createFlatWorldGui;

   public GuiCreateFlatWorldListSlot(GuiCreateFlatWorld var1) {
      super(var1.mc, var1.width, var1.height, 43, var1.height - 60, 24);
      this.createFlatWorldGui = var1;
      this.field_82454_a = -1;
   }

   private void func_82452_a(int var1, int var2, ItemStack var3) {
      this.func_82451_d(var1 + 1, var2 + 1);
      GL11.glEnable(32826);
      if (var3 != null) {
         RenderHelper.enableGUIStandardItemLighting();
         GuiCreateFlatWorld.getRenderItem().renderItemIntoGUI(this.createFlatWorldGui.fontRenderer, this.createFlatWorldGui.mc.getTextureManager(), var3, var1 + 2, var2 + 2);
         RenderHelper.disableStandardItemLighting();
      }

      GL11.glDisable(32826);
   }

   private void func_82451_d(int var1, int var2) {
      this.func_82450_b(var1, var2, 0, 0);
   }

   private void func_82450_b(int var1, int var2, int var3, int var4) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.createFlatWorldGui.mc.getTextureManager().bindTexture(Gui.statIcons);
      float var5 = 0.0078125F;
      float var6 = 0.0078125F;
      boolean var7 = true;
      boolean var8 = true;
      Tessellator var9 = Tessellator.instance;
      var9.startDrawingQuads();
      var9.addVertexWithUV((double)(var1 + 0), (double)(var2 + 18), (double)this.createFlatWorldGui.zLevel, (double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F));
      var9.addVertexWithUV((double)(var1 + 18), (double)(var2 + 18), (double)this.createFlatWorldGui.zLevel, (double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 18) * 0.0078125F));
      var9.addVertexWithUV((double)(var1 + 18), (double)(var2 + 0), (double)this.createFlatWorldGui.zLevel, (double)((float)(var3 + 18) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F));
      var9.addVertexWithUV((double)(var1 + 0), (double)(var2 + 0), (double)this.createFlatWorldGui.zLevel, (double)((float)(var3 + 0) * 0.0078125F), (double)((float)(var4 + 0) * 0.0078125F));
      var9.draw();
   }

   protected int getSize() {
      return GuiCreateFlatWorld.func_82271_a(this.createFlatWorldGui).getFlatLayers().size();
   }

   protected void elementClicked(int var1, boolean var2) {
      this.field_82454_a = var1;
      this.createFlatWorldGui.func_82270_g();
   }

   protected boolean isSelected(int var1) {
      return var1 == this.field_82454_a;
   }

   protected void drawBackground() {
   }

   protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
      FlatLayerInfo var6 = (FlatLayerInfo)GuiCreateFlatWorld.func_82271_a(this.createFlatWorldGui).getFlatLayers().get(GuiCreateFlatWorld.func_82271_a(this.createFlatWorldGui).getFlatLayers().size() - var1 - 1);
      ItemStack var7 = var6.getFillBlock() == 0 ? null : new ItemStack(var6.getFillBlock(), 1, var6.getFillBlockMeta());
      String var8 = var7 == null ? "Air" : Item.itemsList[var6.getFillBlock()].getItemStackDisplayName(var7);
      this.func_82452_a(var2, var3, var7);
      this.createFlatWorldGui.fontRenderer.drawString(var8, var2 + 18 + 5, var3 + 3, 16777215);
      String var9;
      if (var1 == 0) {
         var9 = I18n.getStringParams("createWorld.customize.flat.layer.top", var6.getLayerCount());
      } else if (var1 == GuiCreateFlatWorld.func_82271_a(this.createFlatWorldGui).getFlatLayers().size() - 1) {
         var9 = I18n.getStringParams("createWorld.customize.flat.layer.bottom", var6.getLayerCount());
      } else {
         var9 = I18n.getStringParams("createWorld.customize.flat.layer", var6.getLayerCount());
      }

      this.createFlatWorldGui.fontRenderer.drawString(var9, var2 + 2 + 213 - this.createFlatWorldGui.fontRenderer.getStringWidth(var9), var3 + 3, 16777215);
   }

   protected int getScrollBarX() {
      return this.createFlatWorldGui.width - 70;
   }
}
