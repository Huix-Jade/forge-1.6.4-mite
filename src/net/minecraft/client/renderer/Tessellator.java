package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class Tessellator {

   private static int nativeBufferSize = 0x200000;
   private static int trivertsInBuffer = (nativeBufferSize / 48) * 6;
   public static boolean renderingWorldRenderer = false;
//   public boolean defaultTexture = false;
   private int rawBufferSize = 0;
   public int textureID = 0;

   private static boolean convertQuadsToTriangles;
   private static boolean tryVBO;
   public static ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(nativeBufferSize * 4);
   public static IntBuffer intBuffer = byteBuffer.asIntBuffer();
   public static FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
   public static ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
   public int[] rawBuffer;
   public int vertexCount;
   public double textureU;
   public double textureV;
   public int brightness;
   public int color;
   public boolean hasColor;
   public boolean hasTexture;
   public boolean hasBrightness;
   public boolean hasNormals;
   public int rawBufferIndex;
   public int addedVertices;
   public boolean isColorDisabled;
   public int drawMode;
   public double xOffset;
   public double yOffset;
   public double zOffset;
   public int normal;
   public static Tessellator instance;
   public boolean isDrawing;
   private static boolean useVBO = false;
   private static IntBuffer vertexBuffers;
   private int vboIndex;
   private static int vboCount = 10;
   public static final int bufferSize = 2097152;
   public static final boolean little_endian;
   public static final int buffer_size_minus_32 = 2097120;
   public boolean draw_in_groups = true;

   public Tessellator() {
   }

   static
   {
      useVBO = tryVBO && GLContext.getCapabilities().GL_ARB_vertex_buffer_object;

      if (useVBO)
      {
         vertexBuffers = GLAllocation.createDirectIntBuffer(vboCount);
         ARBVertexBufferObject.glGenBuffersARB(vertexBuffers);
      }
   }


   public int draw() {
      if (!this.isDrawing) {
         throw new IllegalStateException("Not tesselating!");
      } else {
         this.isDrawing = false;
         int offs = 0;
         while (offs < vertexCount)
         {
            int vtc = 0;
            if (drawMode == 7 && convertQuadsToTriangles)
            {
               vtc = Math.min(vertexCount - offs, trivertsInBuffer);
            }
            else
            {
               vtc = Math.min(vertexCount - offs, nativeBufferSize >> 5);
            }
            this.intBuffer.clear();
            this.intBuffer.put(this.rawBuffer, offs * 8, vtc * 8);
            this.byteBuffer.position(0);
            this.byteBuffer.limit(vtc * 32);
            offs += vtc;
            if (this.useVBO) {
               this.vboIndex = (this.vboIndex + 1) % this.vboCount;
               ARBVertexBufferObject.glBindBufferARB(34962, this.vertexBuffers.get(this.vboIndex));
               ARBVertexBufferObject.glBufferDataARB(34962, this.byteBuffer, 35040);
            }

            if (this.hasTexture) {
               if (this.useVBO) {
                  GL11.glTexCoordPointer(2, 5126, 32, 12L);
               } else {
                  this.floatBuffer.position(3);
                  GL11.glTexCoordPointer(2, 32, this.floatBuffer);
               }

               GL11.glEnableClientState(32888);
            }

            if (this.hasBrightness) {
               OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
               if (this.useVBO) {
                  GL11.glTexCoordPointer(2, 5122, 32, 28L);
               } else {
                  this.shortBuffer.position(14);
                  GL11.glTexCoordPointer(2, 32, this.shortBuffer);
               }

               GL11.glEnableClientState(32888);
               OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }

            if (this.hasColor) {
               if (this.useVBO) {
                  GL11.glColorPointer(4, 5121, 32, 20L);
               } else {
                  this.byteBuffer.position(20);
                  GL11.glColorPointer(4, true, 32, this.byteBuffer);
               }

               GL11.glEnableClientState(32886);
            }

            if (this.hasNormals) {
               if (this.useVBO) {
                  GL11.glNormalPointer(5121, 32, 24L);
               } else {
                  this.byteBuffer.position(24);
                  GL11.glNormalPointer(32, this.byteBuffer);
               }

               GL11.glEnableClientState(32885);
            }

            if (this.useVBO) {
               GL11.glVertexPointer(3, 5126, 32, 0L);
            } else {
               this.floatBuffer.position(0);
               GL11.glVertexPointer(3, 32, this.floatBuffer);
            }

            GL11.glEnableClientState(32884);
            if (this.drawMode == 7 && convertQuadsToTriangles) {
               GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vtc);
            } else {
               GL11.glDrawArrays(this.drawMode, 0, vtc);
            }

            GL11.glDisableClientState(32884);
            if (this.hasTexture) {
               GL11.glDisableClientState(32888);
            }

            if (this.hasBrightness) {
               OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
               GL11.glDisableClientState(32888);
               OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }

            if (this.hasColor) {
               GL11.glDisableClientState(32886);
            }

            if (this.hasNormals) {
               GL11.glDisableClientState(32885);
            }
         }

         if (rawBufferSize > 0x20000 && rawBufferIndex < (rawBufferSize << 3))
         {
            rawBufferSize = 0;
            rawBuffer = null;
         }

         int var1 = this.rawBufferIndex * 4;
         this.reset();
         return var1;
      }
   }

   public final void reset() {
      this.vertexCount = 0;
      this.byteBuffer.clear();
      this.rawBufferIndex = 0;
      this.addedVertices = 0;
   }

   public final void startDrawingQuads() {
      this.startDrawing(7);
   }

   public final void startDrawing(int par1) {
      if (this.isDrawing) {
         throw new IllegalStateException("Already tesselating!");
      } else {
         this.isDrawing = true;
         this.reset();
         this.drawMode = par1;
         this.hasNormals = false;
         this.hasColor = false;
         this.hasTexture = false;
         this.hasBrightness = false;
         this.isColorDisabled = false;
      }
   }

   public final void setTextureUV(double par1, double par3) {
      this.hasTexture = true;
      this.textureU = par1;
      this.textureV = par3;
   }

   public final void setBrightness(int par1) {
      this.hasBrightness = true;
      this.brightness = par1;
   }

   public void setColorOpaque_F(float par1, float par2, float par3) {
      this.setColorOpaque((int)(par1 * 255.0F), (int)(par2 * 255.0F), (int)(par3 * 255.0F));
   }

   public void setColorRGBA_F(float par1, float par2, float par3, float par4) {
      this.setColorRGBA((int)(par1 * 255.0F), (int)(par2 * 255.0F), (int)(par3 * 255.0F), (int)(par4 * 255.0F));
   }

   public void setColorOpaque(int par1, int par2, int par3) {
      this.setColorRGBA(par1, par2, par3, 255);
   }

   public void setColorRGBA(int par1, int par2, int par3, int par4) {
      if (!this.isColorDisabled) {
         if (par1 > 255) {
            par1 = 255;
         }

         if (par2 > 255) {
            par2 = 255;
         }

         if (par3 > 255) {
            par3 = 255;
         }

         if (par4 > 255) {
            par4 = 255;
         }

         if (par1 < 0) {
            par1 = 0;
         }

         if (par2 < 0) {
            par2 = 0;
         }

         if (par3 < 0) {
            par3 = 0;
         }

         if (par4 < 0) {
            par4 = 0;
         }

         this.hasColor = true;
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.color = par4 << 24 | par3 << 16 | par2 << 8 | par1;
         } else {
            this.color = par1 << 24 | par2 << 16 | par3 << 8 | par4;
         }
      }

   }

   public final void addVertexWithUV(double par1, double par3, double par5, double par7, double par9) {
      this.setTextureUV(par7, par9);
      this.addVertex(par1, par3, par5);
   }

   public void addVertex(double par1, double par3, double par5) {
      if (rawBufferIndex >= rawBufferSize - 32)
      {
         if (rawBufferSize == 0)
         {
            rawBufferSize = 0x10000;
            rawBuffer = new int[rawBufferSize];
         }
         else
         {
            rawBufferSize *= 2;
            rawBuffer = Arrays.copyOf(rawBuffer, rawBufferSize);
         }
      }

      ++this.addedVertices;
      if (this.drawMode == 7 && convertQuadsToTriangles && this.addedVertices % 4 == 0) {
         for(int var7 = 0; var7 < 2; ++var7) {
            int var8 = 8 * (3 - var7);
            if (this.hasTexture) {
               this.rawBuffer[this.rawBufferIndex + 3] = this.rawBuffer[this.rawBufferIndex - var8 + 3];
               this.rawBuffer[this.rawBufferIndex + 4] = this.rawBuffer[this.rawBufferIndex - var8 + 4];
            }

            if (this.hasBrightness) {
               this.rawBuffer[this.rawBufferIndex + 7] = this.rawBuffer[this.rawBufferIndex - var8 + 7];
            }

            if (this.hasColor) {
               this.rawBuffer[this.rawBufferIndex + 5] = this.rawBuffer[this.rawBufferIndex - var8 + 5];
            }

            this.rawBuffer[this.rawBufferIndex + 0] = this.rawBuffer[this.rawBufferIndex - var8 + 0];
            this.rawBuffer[this.rawBufferIndex + 1] = this.rawBuffer[this.rawBufferIndex - var8 + 1];
            this.rawBuffer[this.rawBufferIndex + 2] = this.rawBuffer[this.rawBufferIndex - var8 + 2];
            ++this.vertexCount;
            this.rawBufferIndex += 8;
         }
      }

      if (this.hasTexture) {
         this.rawBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits((float)this.textureU);
         this.rawBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits((float)this.textureV);
      }

      if (this.hasBrightness) {
         this.rawBuffer[this.rawBufferIndex + 7] = this.brightness;
      }

      if (this.hasColor) {
         this.rawBuffer[this.rawBufferIndex + 5] = this.color;
      }

      if (this.hasNormals) {
         this.rawBuffer[this.rawBufferIndex + 6] = this.normal;
      }

      this.rawBuffer[this.rawBufferIndex + 0] = Float.floatToRawIntBits((float)(par1 + this.xOffset));
      this.rawBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits((float)(par3 + this.yOffset));
      this.rawBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits((float)(par5 + this.zOffset));
      this.rawBufferIndex += 8;
      ++this.vertexCount;

   }

   public void setColorOpaque_I(int par1) {
      int var2 = par1 >> 16 & 255;
      int var3 = par1 >> 8 & 255;
      int var4 = par1 & 255;
      this.setColorOpaque(var2, var3, var4);
   }

   public void setColorRGBA_I(int par1, int par2) {
      int var3 = par1 >> 16 & 255;
      int var4 = par1 >> 8 & 255;
      int var5 = par1 & 255;
      this.setColorRGBA(var3, var4, var5, par2);
   }

   public final void disableColor() {
      this.isColorDisabled = true;
   }

   public final void setNormal(float par1, float par2, float par3) {
      this.hasNormals = true;
      byte var4 = (byte)((int)(par1 * 127.0F));
      byte var5 = (byte)((int)(par2 * 127.0F));
      byte var6 = (byte)((int)(par3 * 127.0F));
      this.normal = var4 & 255 | (var5 & 255) << 8 | (var6 & 255) << 16;
   }

   public final void setTranslation(double par1, double par3, double par5) {
      this.xOffset = par1;
      this.yOffset = par3;
      this.zOffset = par5;
   }

   public final void addTranslation(float par1, float par2, float par3) {
      this.xOffset += (double)par1;
      this.yOffset += (double)par2;
      this.zOffset += (double)par3;
   }

   public void add4VerticesWithUV(double[] x, double[] y, double[] z, double[] u, double[] v) {
      Minecraft.setErrorMessage("Tessellator: vanilla does not support add4VerticesWithUV");
   }

   public void add4VerticesWithUVandAO(double[] x, double[] y, double[] z, double[] u, double[] v, float[] r, float[] g, float[] b, int[] brightness) {
      Minecraft.setErrorMessage("Tessellator: vanilla does not support add4VerticesWithUVandAO");
   }

   static {
      little_endian = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
   }
}
