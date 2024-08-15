package net.minecraft.client.gui;

import java.util.Iterator;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapCoord;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class MapItemRenderer {
   private static final ResourceLocation field_111277_a = new ResourceLocation("textures/map/map_icons.png");
   private final DynamicTexture bufferedImage;
   private int[] intArray = new int[16384];
   private GameSettings gameSettings;
   private final ResourceLocation field_111276_e;

   public MapItemRenderer(GameSettings var1, TextureManager var2) {
      this.gameSettings = var1;
      this.bufferedImage = new DynamicTexture(128, 128);
      this.field_111276_e = var2.getDynamicTextureLocation("map", this.bufferedImage);
      this.intArray = this.bufferedImage.getTextureData();

      for(int var4 = 0; var4 < this.intArray.length; ++var4) {
         this.intArray[var4] = 0;
      }

   }

   public void renderMap(EntityPlayer var1, TextureManager var2, MapData var3) {
      byte var5;
      int var8;
      for(int var4 = 0; var4 < 16384; ++var4) {
         var5 = var3.colors[var4];
         if (var5 / 4 == 0) {
            this.intArray[var4] = (var4 + var4 / 128 & 1) * 8 + 16 << 24;
         } else {
            int var6 = MapColor.mapColorArray[var5 / 4].colorValue;
            int var7 = var5 & 3;
            var8 = 220;
            if (var7 == 2) {
               var8 = 255;
            }

            if (var7 == 0) {
               var8 = 180;
            }

            int var9 = (var6 >> 16 & 255) * var8 / 255;
            int var10 = (var6 >> 8 & 255) * var8 / 255;
            int var11 = (var6 & 255) * var8 / 255;
            this.intArray[var4] = -16777216 | var9 << 16 | var10 << 8 | var11;
         }
      }

      this.bufferedImage.updateDynamicTexture();
      byte var15 = 0;
      var5 = 0;
      Tessellator var16 = Tessellator.instance;
      float var17 = 0.0F;
      var2.bindTexture(this.field_111276_e);
      GL11.glEnable(3042);
      GL11.glBlendFunc(1, 771);
      GL11.glDisable(3008);
      var16.startDrawingQuads();
      var16.addVertexWithUV((double)((float)(var15 + 0) + var17), (double)((float)(var5 + 128) - var17), -0.009999999776482582, 0.0, 1.0);
      var16.addVertexWithUV((double)((float)(var15 + 128) - var17), (double)((float)(var5 + 128) - var17), -0.009999999776482582, 1.0, 1.0);
      var16.addVertexWithUV((double)((float)(var15 + 128) - var17), (double)((float)(var5 + 0) + var17), -0.009999999776482582, 1.0, 0.0);
      var16.addVertexWithUV((double)((float)(var15 + 0) + var17), (double)((float)(var5 + 0) + var17), -0.009999999776482582, 0.0, 0.0);
      var16.draw();
      GL11.glEnable(3008);
      GL11.glDisable(3042);
      var2.bindTexture(field_111277_a);
      var8 = 0;

      for(Iterator var18 = var3.playersVisibleOnMap.values().iterator(); var18.hasNext(); ++var8) {
         MapCoord var19 = (MapCoord)var18.next();
         GL11.glPushMatrix();
         GL11.glTranslatef((float)var15 + (float)var19.centerX / 2.0F + 64.0F, (float)var5 + (float)var19.centerZ / 2.0F + 64.0F, -0.02F);
         GL11.glRotatef((float)(var19.iconRotation * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
         GL11.glScalef(4.0F, 4.0F, 3.0F);
         GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
         float var20 = (float)(var19.iconSize % 4 + 0) / 4.0F;
         float var12 = (float)(var19.iconSize / 4 + 0) / 4.0F;
         float var13 = (float)(var19.iconSize % 4 + 1) / 4.0F;
         float var14 = (float)(var19.iconSize / 4 + 1) / 4.0F;
         var16.startDrawingQuads();
         var16.addVertexWithUV(-1.0, 1.0, (double)((float)var8 * 0.001F), (double)var20, (double)var12);
         var16.addVertexWithUV(1.0, 1.0, (double)((float)var8 * 0.001F), (double)var13, (double)var12);
         var16.addVertexWithUV(1.0, -1.0, (double)((float)var8 * 0.001F), (double)var13, (double)var14);
         var16.addVertexWithUV(-1.0, -1.0, (double)((float)var8 * 0.001F), (double)var20, (double)var14);
         var16.draw();
         GL11.glPopMatrix();
      }

      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.0F, -0.04F);
      GL11.glScalef(1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }
}
