package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TextureUtil {
   private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
   public static final DynamicTexture missingTexture = new DynamicTexture(16, 16);
   public static final int[] missingTextureData;

   public static int glGenTextures() {
      return GL11.glGenTextures();
   }

   public static int uploadTextureImage(int var0, BufferedImage var1) {
      return uploadTextureImageAllocate(var0, var1, false, false);
   }

   public static void uploadTexture(int var0, int[] var1, int var2, int var3) {
      bindTexture(var0);
      uploadTextureSub(var1, var2, var3, 0, 0, false, false);
   }

   public static void uploadTextureSub(int[] var0, int var1, int var2, int var3, int var4, boolean var5, boolean var6) {
      int var7 = 4194304 / var1;
      setTextureBlurred(var5);
      setTextureClamped(var6);

      int var10;
      for(int var8 = 0; var8 < var1 * var2; var8 += var1 * var10) {
         int var9 = var8 / var1;
         var10 = Math.min(var7, var2 - var9);
         int var11 = var1 * var10;
         copyToBufferPos(var0, var8, var11);
         GL11.glTexSubImage2D(3553, 0, var3, var4 + var9, var1, var10, 32993, 33639, dataBuffer);
      }

   }

   public static int uploadTextureImageAllocate(int var0, BufferedImage var1, boolean var2, boolean var3) {
      allocateTexture(var0, var1.getWidth(), var1.getHeight());
      return uploadTextureImageSub(var0, var1, 0, 0, var2, var3);
   }

   public static void allocateTexture(int var0, int var1, int var2) {
      bindTexture(var0);
      GL11.glTexImage2D(3553, 0, 6408, var1, var2, 0, 32993, 33639, (IntBuffer)null);
   }

   public static int uploadTextureImageSub(int var0, BufferedImage var1, int var2, int var3, boolean var4, boolean var5) {
      bindTexture(var0);
      uploadTextureImageSubImpl(var1, var2, var3, var4, var5);
      return var0;
   }

   private static void uploadTextureImageSubImpl(BufferedImage var0, int var1, int var2, boolean var3, boolean var4) {
      int var5 = var0.getWidth();
      int var6 = var0.getHeight();
      int var7 = 4194304 / var5;
      int[] var8 = new int[var7 * var5];
      setTextureBlurred(var3);
      setTextureClamped(var4);

      for(int var9 = 0; var9 < var5 * var6; var9 += var5 * var7) {
         int var10 = var9 / var5;
         int var11 = Math.min(var7, var6 - var10);
         int var12 = var5 * var11;
         var0.getRGB(0, var10, var5, var11, var8, 0, var5);
         copyToBuffer(var8, var12);
         GL11.glTexSubImage2D(3553, 0, var1, var2 + var10, var5, var11, 32993, 33639, dataBuffer);
      }

   }

   private static void setTextureClamped(boolean var0) {
      if (var0) {
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
      } else {
         GL11.glTexParameteri(3553, 10242, 10497);
         GL11.glTexParameteri(3553, 10243, 10497);
      }

   }

   private static void setTextureBlurred(boolean var0) {
      if (var0) {
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
      } else {
         GL11.glTexParameteri(3553, 10241, 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
      }

   }

   private static void copyToBuffer(int[] var0, int var1) {
      copyToBufferPos(var0, 0, var1);
   }

   private static void copyToBufferPos(int[] var0, int var1, int var2) {
      int[] var3 = var0;
      if (Minecraft.getMinecraft().gameSettings.anaglyph) {
         var3 = updateAnaglyph(var0);
      }

      dataBuffer.clear();
      dataBuffer.put(var3, var1, var2);
      dataBuffer.position(0).limit(var2);
   }

   public static void bindTexture(int var0) {
      GL11.glBindTexture(3553, var0);
   }

   public static int[] readImageData(ResourceManager var0, ResourceLocation var1) throws IOException {
      BufferedImage var2 = ImageIO.read(var0.getResource(var1).getInputStream());
      int var3 = var2.getWidth();
      int var4 = var2.getHeight();
      int[] var5 = new int[var3 * var4];
      var2.getRGB(0, 0, var3, var4, var5, 0, var3);
      return var5;
   }

   public static int[] updateAnaglyph(int[] var0) {
      int[] var1 = new int[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         int var3 = var0[var2] >> 24 & 255;
         int var4 = var0[var2] >> 16 & 255;
         int var5 = var0[var2] >> 8 & 255;
         int var6 = var0[var2] & 255;
         int var7 = (var4 * 30 + var5 * 59 + var6 * 11) / 100;
         int var8 = (var4 * 30 + var5 * 70) / 100;
         int var9 = (var4 * 30 + var6 * 70) / 100;
         var1[var2] = var3 << 24 | var7 << 16 | var8 << 8 | var9;
      }

      return var1;
   }

   static {
      missingTextureData = missingTexture.getTextureData();
      int var0 = -16777216;
      int var1 = -524040;
      int[] var2 = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
      int[] var3 = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
      int var4 = var2.length;

      for(int var5 = 0; var5 < 16; ++var5) {
         System.arraycopy(var5 < var4 ? var2 : var3, 0, missingTextureData, 16 * var5, var4);
         System.arraycopy(var5 < var4 ? var3 : var2, 0, missingTextureData, 16 * var5 + var4, var4);
      }

      missingTexture.updateDynamicTexture();
   }
}
