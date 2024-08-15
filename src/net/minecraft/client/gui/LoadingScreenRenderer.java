package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class LoadingScreenRenderer implements IProgressUpdate {
   private String field_73727_a = "";
   private Minecraft mc;
   private String currentlyDisplayedText = "";
   private long field_73723_d = Minecraft.getSystemTime();
   private boolean field_73724_e;

   public LoadingScreenRenderer(Minecraft var1) {
      this.mc = var1;
   }

   public void resetProgressAndMessage(String var1) {
      this.field_73724_e = false;
      this.func_73722_d(var1);
   }

   public void displayProgressMessage(String var1) {
      this.field_73724_e = true;
      this.func_73722_d(var1);
   }

   public void func_73722_d(String var1) {
      this.currentlyDisplayedText = var1;
      if (!this.mc.running) {
         if (!this.field_73724_e) {
            throw new MinecraftError();
         }
      } else {
         ScaledResolution var2 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
         GL11.glClear(256);
         GL11.glMatrixMode(5889);
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0, var2.getScaledWidth_double(), var2.getScaledHeight_double(), 0.0, 100.0, 300.0);
         GL11.glMatrixMode(5888);
         GL11.glLoadIdentity();
         GL11.glTranslatef(0.0F, 0.0F, -200.0F);
      }
   }

   public void resetProgresAndWorkingMessage(String var1) {
      if (!this.mc.running) {
         if (!this.field_73724_e) {
            throw new MinecraftError();
         }
      } else {
         this.field_73723_d = 0L;
         this.field_73727_a = var1;
         this.setLoadingProgress(-1);
         this.field_73723_d = 0L;
      }
   }

   public void setLoadingProgress(int var1) {
      if (!this.mc.running) {
         if (!this.field_73724_e) {
            throw new MinecraftError();
         }
      } else {
         long var2 = Minecraft.getSystemTime();
         if (var2 - this.field_73723_d >= 100L) {
            this.field_73723_d = var2;
            ScaledResolution var4 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int var5 = var4.getScaledWidth();
            int var6 = var4.getScaledHeight();
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, var4.getScaledWidth_double(), var4.getScaledHeight_double(), 0.0, 100.0, 300.0);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -200.0F);
            GL11.glClear(16640);
            Tessellator var7 = Tessellator.instance;
            this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
            float var8 = 32.0F;
            var7.startDrawingQuads();
            var7.setColorOpaque_I(4210752);
            var7.addVertexWithUV(0.0, (double)var6, 0.0, 0.0, (double)((float)var6 / var8));
            var7.addVertexWithUV((double)var5, (double)var6, 0.0, (double)((float)var5 / var8), (double)((float)var6 / var8));
            var7.addVertexWithUV((double)var5, 0.0, 0.0, (double)((float)var5 / var8), 0.0);
            var7.addVertexWithUV(0.0, 0.0, 0.0, 0.0, 0.0);
            var7.draw();
            if (var1 >= 0) {
               byte var9 = 100;
               byte var10 = 2;
               int var11 = var5 / 2 - var9 / 2;
               int var12 = var6 / 2 + 16;
               GL11.glDisable(3553);
               var7.startDrawingQuads();
               var7.setColorOpaque_I(8421504);
               var7.addVertex((double)var11, (double)var12, 0.0);
               var7.addVertex((double)var11, (double)(var12 + var10), 0.0);
               var7.addVertex((double)(var11 + var9), (double)(var12 + var10), 0.0);
               var7.addVertex((double)(var11 + var9), (double)var12, 0.0);
               var7.setColorOpaque_I(8454016);
               var7.addVertex((double)var11, (double)var12, 0.0);
               var7.addVertex((double)var11, (double)(var12 + var10), 0.0);
               var7.addVertex((double)(var11 + var1), (double)(var12 + var10), 0.0);
               var7.addVertex((double)(var11 + var1), (double)var12, 0.0);
               var7.draw();
               GL11.glEnable(3553);
            }

            this.mc.fontRenderer.drawStringWithShadow(this.currentlyDisplayedText, (var5 - this.mc.fontRenderer.getStringWidth(this.currentlyDisplayedText)) / 2, var6 / 2 - 4 - 16, 16777215);
            this.mc.fontRenderer.drawStringWithShadow(this.field_73727_a, (var5 - this.mc.fontRenderer.getStringWidth(this.field_73727_a)) / 2, var6 / 2 - 4 + 8, 16777215);
            Display.update();

            try {
               Thread.yield();
            } catch (Exception var13) {
            }

         }
      }
   }
}
