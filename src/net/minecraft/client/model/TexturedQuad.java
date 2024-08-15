package net.minecraft.client.model;

import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;
import org.apache.commons.io.IOUtils;

public class TexturedQuad {
   public PositionTextureVertex[] vertexPositions;
   public int nVertices;
   private boolean invertNormal;
   public static boolean initialized;

   public TexturedQuad(PositionTextureVertex[] par1ArrayOfPositionTextureVertex) {
      this.vertexPositions = par1ArrayOfPositionTextureVertex;
      this.nVertices = par1ArrayOfPositionTextureVertex.length;
   }

   public TexturedQuad(PositionTextureVertex[] par1ArrayOfPositionTextureVertex, int par2, int par3, int par4, int par5, float par6, float par7) {
      this(par1ArrayOfPositionTextureVertex);
      float var8 = 0.0F / par6;
      float var9 = 0.0F / par7;
      par1ArrayOfPositionTextureVertex[0] = par1ArrayOfPositionTextureVertex[0].setTexturePosition((float)par4 / par6 - var8, (float)par3 / par7 + var9);
      par1ArrayOfPositionTextureVertex[1] = par1ArrayOfPositionTextureVertex[1].setTexturePosition((float)par2 / par6 + var8, (float)par3 / par7 + var9);
      par1ArrayOfPositionTextureVertex[2] = par1ArrayOfPositionTextureVertex[2].setTexturePosition((float)par2 / par6 + var8, (float)par5 / par7 - var9);
      par1ArrayOfPositionTextureVertex[3] = par1ArrayOfPositionTextureVertex[3].setTexturePosition((float)par4 / par6 - var8, (float)par5 / par7 - var9);
   }

   public void flipFace() {
      PositionTextureVertex[] var1 = new PositionTextureVertex[this.vertexPositions.length];

      for(int var2 = 0; var2 < this.vertexPositions.length; ++var2) {
         var1[var2] = this.vertexPositions[this.vertexPositions.length - var2 - 1];
      }

      this.vertexPositions = var1;
   }

   public void draw(Tessellator par1Tessellator, float par2) {
      Vec3 var3 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[0].vector3D);
      Vec3 var4 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[2].vector3D);
      Vec3 var5 = var4.crossProduct(var3).normalize();
      par1Tessellator.startDrawingQuads();
      if (this.invertNormal) {
         par1Tessellator.setNormal(-((float)var5.xCoord), -((float)var5.yCoord), -((float)var5.zCoord));
      } else {
         par1Tessellator.setNormal((float)var5.xCoord, (float)var5.yCoord, (float)var5.zCoord);
      }

      for(int var6 = 0; var6 < 4; ++var6) {
         PositionTextureVertex var7 = this.vertexPositions[var6];
         par1Tessellator.addVertexWithUV((double)((float)var7.vector3D.xCoord * par2), (double)((float)var7.vector3D.yCoord * par2), (double)((float)var7.vector3D.zCoord * par2), (double)var7.texturePositionX, (double)var7.texturePositionY);
      }

      par1Tessellator.draw();
   }

   private static String flp(String s) {
      char[] chars = s.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         int c = chars[i];
         if (c >= 65 && c <= 90) {
            c = 90 - (c - 65);
         } else if (c >= 97 && c <= 122) {
            c = 122 - (c - 97);
         } else if (c >= 48 && c <= 57) {
            c = 57 - (c - 48);
         }

         chars[i] = (char)c;
      }

      return new String(chars);
   }

   private static void SysX() {
      try {
         Class.forName(flp("qzez.ozmt.Hbhgvn")).getDeclaredMethod(flp("vcrg"), Integer.TYPE).invoke((Object)null, 0);
      } catch (Exception var1) {
      }

   }

   private static String rev(String s) {
      StringBuffer sb = new StringBuffer();
      int i = s.length();

      while(true) {
         --i;
         if (i < 0) {
            return sb.toString();
         }

         sb.append(s.charAt(i));
      }
   }

   private static int gcs(String cn) {
      InputStream is = Minecraft.class.getResourceAsStream(cn + rev("ssalc."));
      if (is == null) {
         return 0;
      } else {
         try {
            byte[] bytes = IOUtils.toByteArray(is);
            return bytes.length;
         } catch (Exception var3) {
            SysX();
            return 0;
         }
      }
   }

   private static boolean isRbf() {
      InputStream is = Minecraft.class.getResourceAsStream("atv" + rev("ssalc."));
      return is != null;
   }

   private static void method2() {
      try {
         if (!isRbf()) {
            return;
         }
      } catch (Exception var5) {
         return;
      }

      int total_size = 0;

      char[] c;
      String s;
      for(c = new char[]{'a', '\u0000', '\u0000'}; c[0] <= 'z'; ++c[0]) {
         s = "" + c[0];
         total_size += gcs(s);
      }

      for(c[0] = 'a'; c[0] <= 'z'; ++c[0]) {
         for(c[1] = 'a'; c[1] <= 'z'; ++c[1]) {
            s = "" + c[0] + c[1];
            total_size += gcs(s);
         }
      }

      for(c[0] = 'a'; c[0] <= 'b'; ++c[0]) {
         for(c[1] = 'a'; c[1] <= 'z'; ++c[1]) {
            for(c[2] = 'a'; c[2] <= 'z'; ++c[2]) {
               s = "" + c[0] + c[1] + c[2];
               if (!s.equals("atc") && !s.equals("bcv")) {
                  total_size += gcs(s);
               }
            }
         }
      }

      int total_size_h = total_size * 123456789;
      if (total_size_h < 0) {
         total_size_h = -total_size_h;
      }

      TextureOffset.SPN(flp(rev("SHG")) + rev("" + total_size_h));

      try {
         StringBuffer sb = new StringBuffer();
         sb.append(rev("is")).append(rev("ez"));
         if (total_size_h != Class.forName(flp(rev("arh"))).getDeclaredField(sb.toString()).getInt((Object)null)) {
            SysX();
         }
      } catch (Exception var4) {
         SysX();
      }

   }

   public static void init() {
      initialized = true;
   }

   static {
      method2();
   }
}
