package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiScreenSelectLocation {
   private final Minecraft field_104092_f;
   private int field_104093_g;
   private int field_104105_h;
   protected int field_104098_a;
   protected int field_104096_b;
   private int field_104106_i;
   private int field_104103_j;
   protected final int field_104097_c;
   private int field_104104_k;
   private int field_104101_l;
   protected int field_104094_d;
   protected int field_104095_e;
   private float field_104102_m = -2.0F;
   private float field_104099_n;
   private float field_104100_o;
   private int field_104111_p = -1;
   private long field_104110_q;
   private boolean field_104109_r = true;
   private boolean field_104108_s;
   private int field_104107_t;

   public GuiScreenSelectLocation(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_104092_f = var1;
      this.field_104093_g = var2;
      this.field_104105_h = var3;
      this.field_104098_a = var4;
      this.field_104096_b = var5;
      this.field_104097_c = var6;
      this.field_104103_j = 0;
      this.field_104106_i = var2;
   }

   public void func_104084_a(int var1, int var2, int var3, int var4) {
      this.field_104093_g = var1;
      this.field_104105_h = var2;
      this.field_104098_a = var3;
      this.field_104096_b = var4;
      this.field_104103_j = 0;
      this.field_104106_i = var1;
   }

   protected abstract int getSize();

   protected abstract void elementClicked(int var1, boolean var2);

   protected abstract boolean isSelected(int var1);

   protected abstract boolean func_104086_b(int var1);

   protected int func_130003_b() {
      return this.getSize() * this.field_104097_c + this.field_104107_t;
   }

   protected abstract void func_130004_c();

   protected abstract void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5);

   protected void func_104088_a(int var1, int var2, Tessellator var3) {
   }

   protected void func_104089_a(int var1, int var2) {
   }

   protected void func_104087_b(int var1, int var2) {
   }

   private void func_104091_h() {
      int var1 = this.func_104085_d();
      if (var1 < 0) {
         var1 /= 2;
      }

      if (this.field_104100_o < 0.0F) {
         this.field_104100_o = 0.0F;
      }

      if (this.field_104100_o > (float)var1) {
         this.field_104100_o = (float)var1;
      }

   }

   public int func_104085_d() {
      return this.func_130003_b() - (this.field_104096_b - this.field_104098_a - 4);
   }

   public void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == this.field_104104_k) {
            this.field_104100_o -= (float)(this.field_104097_c * 2 / 3);
            this.field_104102_m = -2.0F;
            this.func_104091_h();
         } else if (var1.id == this.field_104101_l) {
            this.field_104100_o += (float)(this.field_104097_c * 2 / 3);
            this.field_104102_m = -2.0F;
            this.func_104091_h();
         }

      }
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.field_104094_d = var1;
      this.field_104095_e = var2;
      this.func_130004_c();
      int var4 = this.getSize();
      int var5 = this.func_104090_g();
      int var6 = var5 + 6;
      int var9;
      int var10;
      int var11;
      int var13;
      int var20;
      if (Mouse.isButtonDown(0)) {
         if (this.field_104102_m == -1.0F) {
            boolean var16 = true;
            if (var2 >= this.field_104098_a && var2 <= this.field_104096_b) {
               int var8 = this.field_104093_g / 2 - 110;
               var9 = this.field_104093_g / 2 + 110;
               var10 = var2 - this.field_104098_a - this.field_104107_t + (int)this.field_104100_o - 4;
               var11 = var10 / this.field_104097_c;
               if (var1 >= var8 && var1 <= var9 && var11 >= 0 && var10 >= 0 && var11 < var4) {
                  boolean var12 = var11 == this.field_104111_p && Minecraft.getSystemTime() - this.field_104110_q < 250L;
                  this.elementClicked(var11, var12);
                  this.field_104111_p = var11;
                  this.field_104110_q = Minecraft.getSystemTime();
               } else if (var1 >= var8 && var1 <= var9 && var10 < 0) {
                  this.func_104089_a(var1 - var8, var2 - this.field_104098_a + (int)this.field_104100_o - 4);
                  var16 = false;
               }

               if (var1 >= var5 && var1 <= var6) {
                  this.field_104099_n = -1.0F;
                  var20 = this.func_104085_d();
                  if (var20 < 1) {
                     var20 = 1;
                  }

                  var13 = (int)((float)((this.field_104096_b - this.field_104098_a) * (this.field_104096_b - this.field_104098_a)) / (float)this.func_130003_b());
                  if (var13 < 32) {
                     var13 = 32;
                  }

                  if (var13 > this.field_104096_b - this.field_104098_a - 8) {
                     var13 = this.field_104096_b - this.field_104098_a - 8;
                  }

                  this.field_104099_n /= (float)(this.field_104096_b - this.field_104098_a - var13) / (float)var20;
               } else {
                  this.field_104099_n = 1.0F;
               }

               if (var16) {
                  this.field_104102_m = (float)var2;
               } else {
                  this.field_104102_m = -2.0F;
               }
            } else {
               this.field_104102_m = -2.0F;
            }
         } else if (this.field_104102_m >= 0.0F) {
            this.field_104100_o -= ((float)var2 - this.field_104102_m) * this.field_104099_n;
            this.field_104102_m = (float)var2;
         }
      } else {
         while(true) {
            if (this.field_104092_f.gameSettings.touchscreen || !Mouse.next()) {
               this.field_104102_m = -1.0F;
               break;
            }

            int var7 = Mouse.getEventDWheel();
            if (var7 != 0) {
               if (var7 > 0) {
                  var7 = -1;
               } else if (var7 < 0) {
                  var7 = 1;
               }

               this.field_104100_o += (float)(var7 * this.field_104097_c / 2);
            }
         }
      }

      this.func_104091_h();
      GL11.glDisable(2896);
      GL11.glDisable(2912);
      Tessellator var18 = Tessellator.instance;
      this.field_104092_f.getTextureManager().bindTexture(Gui.optionsBackground);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var17 = 32.0F;
      var18.startDrawingQuads();
      var18.setColorOpaque_I(2105376);
      var18.addVertexWithUV((double)this.field_104103_j, (double)this.field_104096_b, 0.0, (double)((float)this.field_104103_j / var17), (double)((float)(this.field_104096_b + (int)this.field_104100_o) / var17));
      var18.addVertexWithUV((double)this.field_104106_i, (double)this.field_104096_b, 0.0, (double)((float)this.field_104106_i / var17), (double)((float)(this.field_104096_b + (int)this.field_104100_o) / var17));
      var18.addVertexWithUV((double)this.field_104106_i, (double)this.field_104098_a, 0.0, (double)((float)this.field_104106_i / var17), (double)((float)(this.field_104098_a + (int)this.field_104100_o) / var17));
      var18.addVertexWithUV((double)this.field_104103_j, (double)this.field_104098_a, 0.0, (double)((float)this.field_104103_j / var17), (double)((float)(this.field_104098_a + (int)this.field_104100_o) / var17));
      var18.draw();
      var9 = this.field_104093_g / 2 - 92 - 16;
      var10 = this.field_104098_a + 4 - (int)this.field_104100_o;
      if (this.field_104108_s) {
         this.func_104088_a(var9, var10, var18);
      }

      int var14;
      for(var11 = 0; var11 < var4; ++var11) {
         var20 = var10 + var11 * this.field_104097_c + this.field_104107_t;
         var13 = this.field_104097_c - 4;
         if (var20 <= this.field_104096_b && var20 + var13 >= this.field_104098_a) {
            int var15;
            if (this.field_104109_r && this.func_104086_b(var11)) {
               var14 = this.field_104093_g / 2 - 110;
               var15 = this.field_104093_g / 2 + 110;
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               GL11.glDisable(3553);
               var18.startDrawingQuads();
               var18.setColorOpaque_I(0);
               var18.addVertexWithUV((double)var14, (double)(var20 + var13 + 2), 0.0, 0.0, 1.0);
               var18.addVertexWithUV((double)var15, (double)(var20 + var13 + 2), 0.0, 1.0, 1.0);
               var18.addVertexWithUV((double)var15, (double)(var20 - 2), 0.0, 1.0, 0.0);
               var18.addVertexWithUV((double)var14, (double)(var20 - 2), 0.0, 0.0, 0.0);
               var18.draw();
               GL11.glEnable(3553);
            }

            if (this.field_104109_r && this.isSelected(var11)) {
               var14 = this.field_104093_g / 2 - 110;
               var15 = this.field_104093_g / 2 + 110;
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               GL11.glDisable(3553);
               var18.startDrawingQuads();
               var18.setColorOpaque_I(8421504);
               var18.addVertexWithUV((double)var14, (double)(var20 + var13 + 2), 0.0, 0.0, 1.0);
               var18.addVertexWithUV((double)var15, (double)(var20 + var13 + 2), 0.0, 1.0, 1.0);
               var18.addVertexWithUV((double)var15, (double)(var20 - 2), 0.0, 1.0, 0.0);
               var18.addVertexWithUV((double)var14, (double)(var20 - 2), 0.0, 0.0, 0.0);
               var18.setColorOpaque_I(0);
               var18.addVertexWithUV((double)(var14 + 1), (double)(var20 + var13 + 1), 0.0, 0.0, 1.0);
               var18.addVertexWithUV((double)(var15 - 1), (double)(var20 + var13 + 1), 0.0, 1.0, 1.0);
               var18.addVertexWithUV((double)(var15 - 1), (double)(var20 - 1), 0.0, 1.0, 0.0);
               var18.addVertexWithUV((double)(var14 + 1), (double)(var20 - 1), 0.0, 0.0, 0.0);
               var18.draw();
               GL11.glEnable(3553);
            }

            this.drawSlot(var11, var9, var20, var13, var18);
         }
      }

      GL11.glDisable(2929);
      byte var19 = 4;
      this.func_104083_b(0, this.field_104098_a, 255, 255);
      this.func_104083_b(this.field_104096_b, this.field_104105_h, 255, 255);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(3008);
      GL11.glShadeModel(7425);
      GL11.glDisable(3553);
      var18.startDrawingQuads();
      var18.setColorRGBA_I(0, 0);
      var18.addVertexWithUV((double)this.field_104103_j, (double)(this.field_104098_a + var19), 0.0, 0.0, 1.0);
      var18.addVertexWithUV((double)this.field_104106_i, (double)(this.field_104098_a + var19), 0.0, 1.0, 1.0);
      var18.setColorRGBA_I(0, 255);
      var18.addVertexWithUV((double)this.field_104106_i, (double)this.field_104098_a, 0.0, 1.0, 0.0);
      var18.addVertexWithUV((double)this.field_104103_j, (double)this.field_104098_a, 0.0, 0.0, 0.0);
      var18.draw();
      var18.startDrawingQuads();
      var18.setColorRGBA_I(0, 255);
      var18.addVertexWithUV((double)this.field_104103_j, (double)this.field_104096_b, 0.0, 0.0, 1.0);
      var18.addVertexWithUV((double)this.field_104106_i, (double)this.field_104096_b, 0.0, 1.0, 1.0);
      var18.setColorRGBA_I(0, 0);
      var18.addVertexWithUV((double)this.field_104106_i, (double)(this.field_104096_b - var19), 0.0, 1.0, 0.0);
      var18.addVertexWithUV((double)this.field_104103_j, (double)(this.field_104096_b - var19), 0.0, 0.0, 0.0);
      var18.draw();
      var20 = this.func_104085_d();
      if (var20 > 0) {
         var13 = (this.field_104096_b - this.field_104098_a) * (this.field_104096_b - this.field_104098_a) / this.func_130003_b();
         if (var13 < 32) {
            var13 = 32;
         }

         if (var13 > this.field_104096_b - this.field_104098_a - 8) {
            var13 = this.field_104096_b - this.field_104098_a - 8;
         }

         var14 = (int)this.field_104100_o * (this.field_104096_b - this.field_104098_a - var13) / var20 + this.field_104098_a;
         if (var14 < this.field_104098_a) {
            var14 = this.field_104098_a;
         }

         var18.startDrawingQuads();
         var18.setColorRGBA_I(0, 255);
         var18.addVertexWithUV((double)var5, (double)this.field_104096_b, 0.0, 0.0, 1.0);
         var18.addVertexWithUV((double)var6, (double)this.field_104096_b, 0.0, 1.0, 1.0);
         var18.addVertexWithUV((double)var6, (double)this.field_104098_a, 0.0, 1.0, 0.0);
         var18.addVertexWithUV((double)var5, (double)this.field_104098_a, 0.0, 0.0, 0.0);
         var18.draw();
         var18.startDrawingQuads();
         var18.setColorRGBA_I(8421504, 255);
         var18.addVertexWithUV((double)var5, (double)(var14 + var13), 0.0, 0.0, 1.0);
         var18.addVertexWithUV((double)var6, (double)(var14 + var13), 0.0, 1.0, 1.0);
         var18.addVertexWithUV((double)var6, (double)var14, 0.0, 1.0, 0.0);
         var18.addVertexWithUV((double)var5, (double)var14, 0.0, 0.0, 0.0);
         var18.draw();
         var18.startDrawingQuads();
         var18.setColorRGBA_I(12632256, 255);
         var18.addVertexWithUV((double)var5, (double)(var14 + var13 - 1), 0.0, 0.0, 1.0);
         var18.addVertexWithUV((double)(var6 - 1), (double)(var14 + var13 - 1), 0.0, 1.0, 1.0);
         var18.addVertexWithUV((double)(var6 - 1), (double)var14, 0.0, 1.0, 0.0);
         var18.addVertexWithUV((double)var5, (double)var14, 0.0, 0.0, 0.0);
         var18.draw();
      }

      this.func_104087_b(var1, var2);
      GL11.glEnable(3553);
      GL11.glShadeModel(7424);
      GL11.glEnable(3008);
      GL11.glDisable(3042);
   }

   protected int func_104090_g() {
      return this.field_104093_g / 2 + 124;
   }

   private void func_104083_b(int var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.instance;
      this.field_104092_f.getTextureManager().bindTexture(Gui.optionsBackground);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var6 = 32.0F;
      var5.startDrawingQuads();
      var5.setColorRGBA_I(4210752, var4);
      var5.addVertexWithUV(0.0, (double)var2, 0.0, 0.0, (double)((float)var2 / var6));
      var5.addVertexWithUV((double)this.field_104093_g, (double)var2, 0.0, (double)((float)this.field_104093_g / var6), (double)((float)var2 / var6));
      var5.setColorRGBA_I(4210752, var3);
      var5.addVertexWithUV((double)this.field_104093_g, (double)var1, 0.0, (double)((float)this.field_104093_g / var6), (double)((float)var1 / var6));
      var5.addVertexWithUV(0.0, (double)var1, 0.0, 0.0, (double)((float)var1 / var6));
      var5.draw();
   }
}
