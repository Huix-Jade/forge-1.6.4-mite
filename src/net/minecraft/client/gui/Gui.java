package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Gui {
   public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
   protected float zLevel;

   protected void drawHorizontalLine(int par1, int par2, int par3, int par4) {
      if (par2 < par1) {
         int var5 = par1;
         par1 = par2;
         par2 = var5;
      }

      drawRect(par1, par3, par2 + 1, par3 + 1, par4);
   }

   protected void drawVerticalLine(int par1, int par2, int par3, int par4) {
      if (par3 < par2) {
         int var5 = par2;
         par2 = par3;
         par3 = var5;
      }

      drawRect(par1, par2 + 1, par1 + 1, par3, par4);
   }

   public static void drawRect(int par0, int par1, int par2, int par3, int par4) {
      int var5;
      if (par0 < par2) {
         var5 = par0;
         par0 = par2;
         par2 = var5;
      }

      if (par1 < par3) {
         var5 = par1;
         par1 = par3;
         par3 = var5;
      }

      float var10 = (float)(par4 >> 24 & 255) / 255.0F;
      float var6 = (float)(par4 >> 16 & 255) / 255.0F;
      float var7 = (float)(par4 >> 8 & 255) / 255.0F;
      float var8 = (float)(par4 & 255) / 255.0F;
      Tessellator var9 = Tessellator.instance;
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(var6, var7, var8, var10);
      var9.startDrawingQuads();
      var9.addVertex((double)par0, (double)par3, 0.0);
      var9.addVertex((double)par2, (double)par3, 0.0);
      var9.addVertex((double)par2, (double)par1, 0.0);
      var9.addVertex((double)par0, (double)par1, 0.0);
      var9.draw();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
   }

   protected void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {
      float var7 = (float)(par5 >> 24 & 255) / 255.0F;
      float var8 = (float)(par5 >> 16 & 255) / 255.0F;
      float var9 = (float)(par5 >> 8 & 255) / 255.0F;
      float var10 = (float)(par5 & 255) / 255.0F;
      float var11 = (float)(par6 >> 24 & 255) / 255.0F;
      float var12 = (float)(par6 >> 16 & 255) / 255.0F;
      float var13 = (float)(par6 >> 8 & 255) / 255.0F;
      float var14 = (float)(par6 & 255) / 255.0F;
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glDisable(3008);
      GL11.glBlendFunc(770, 771);
      GL11.glShadeModel(7425);
      Tessellator var15 = Tessellator.instance;
      var15.startDrawingQuads();
      var15.setColorRGBA_F(var8, var9, var10, var7);
      var15.addVertex((double)par3, (double)par2, (double)this.zLevel);
      var15.addVertex((double)par1, (double)par2, (double)this.zLevel);
      var15.setColorRGBA_F(var12, var13, var14, var11);
      var15.addVertex((double)par1, (double)par4, (double)this.zLevel);
      var15.addVertex((double)par3, (double)par4, (double)this.zLevel);
      var15.draw();
      GL11.glShadeModel(7424);
      GL11.glDisable(3042);
      GL11.glEnable(3008);
      GL11.glEnable(3553);
   }

   public void drawCenteredString(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5) {
      par1FontRenderer.drawStringWithShadow(par2Str, par3 - par1FontRenderer.getStringWidth(par2Str) / 2, par4, par5);
   }

   public void drawString(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5) {
      par1FontRenderer.drawStringWithShadow(par2Str, par3, par4, par5);
   }

   public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6) {
      float var7 = 0.00390625F;
      float var8 = 0.00390625F;
      Tessellator var9 = Tessellator.instance;
      var9.startDrawingQuads();
      var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + par6) * var8));
      var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
      var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + 0) * var8));
      var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + 0) * var8));
      var9.draw();
   }

   public void drawTexturedModalRect2(int par1, int par2, int par5, int par6) {
      Tessellator var9 = Tessellator.instance;
      var9.startDrawingQuads();
      var9.addVertexWithUV((double)par1, (double)(par2 + par6), (double)this.zLevel, 0.0, 1.0);
      var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)this.zLevel, 1.0, 1.0);
      var9.addVertexWithUV((double)(par1 + par5), (double)par2, (double)this.zLevel, 1.0, 0.0);
      var9.addVertexWithUV((double)par1, (double)par2, (double)this.zLevel, 0.0, 0.0);
      var9.draw();
   }

   public void drawTexturedModelRectFromIcon(int par1, int par2, Icon par3Icon, int par4, int par5) {
      Tessellator var6 = Tessellator.instance;
      var6.startDrawingQuads();
      var6.addVertexWithUV((double)(par1 + 0), (double)(par2 + par5), (double)this.zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMaxV());
      var6.addVertexWithUV((double)(par1 + par4), (double)(par2 + par5), (double)this.zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMaxV());
      var6.addVertexWithUV((double)(par1 + par4), (double)(par2 + 0), (double)this.zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMinV());
      var6.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMinV());
      var6.draw();
   }
}
