package net.minecraft.client.renderer;

import org.lwjgl.opengl.GL11;

public final class TessellatorMITE extends Tessellator {
   public TessellatorMITE() {
      this.byteBuffer = GLAllocation.createDirectByteBuffer(8388608);
      this.intBuffer = this.byteBuffer.asIntBuffer();
      this.floatBuffer = this.byteBuffer.asFloatBuffer();
      this.shortBuffer = this.byteBuffer.asShortBuffer();
      this.rawBuffer = new int[2097152];
   }

   public int draw() {
      if (RenderingScheme.current == 101) {
         return super.draw();
      } else if (!this.isDrawing) {
         throw new IllegalStateException("Not tesselating!");
      } else {
         this.isDrawing = false;
         int group_size;
         if (this.vertexCount > 0) {
            this.intBuffer.clear();
            this.intBuffer.put(this.rawBuffer, 0, this.rawBufferIndex);
            this.byteBuffer.position(0);
            this.byteBuffer.limit(this.rawBufferIndex * 4);
            if (this.hasTexture) {
               this.floatBuffer.position(3);
               GL11.glTexCoordPointer(2, 32, this.floatBuffer);
               GL11.glEnableClientState(32888);
            }

            if (this.hasBrightness) {
               OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
               this.shortBuffer.position(14);
               GL11.glTexCoordPointer(2, 32, this.shortBuffer);
               GL11.glEnableClientState(32888);
               OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }

            if (this.hasColor) {
               this.byteBuffer.position(20);
               GL11.glColorPointer(4, true, 32, this.byteBuffer);
               GL11.glEnableClientState(32886);
            }

            if (this.hasNormals) {
               this.byteBuffer.position(24);
               GL11.glNormalPointer(32, this.byteBuffer);
               GL11.glEnableClientState(32885);
            }

            this.floatBuffer.position(0);
            GL11.glVertexPointer(3, 32, this.floatBuffer);
            GL11.glEnableClientState(32884);
            if (!this.draw_in_groups) {
               GL11.glDrawArrays(this.drawMode, 0, this.vertexCount);
            } else {
               group_size = 400;

               int i;
               for(i = 0; i < this.vertexCount - group_size; i += group_size) {
                  GL11.glDrawArrays(this.drawMode, i, group_size);
               }

               GL11.glDrawArrays(this.drawMode, i, this.vertexCount - i);
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

         group_size = this.rawBufferIndex * 4;
         this.reset();
         return group_size;
      }
   }

   public void setColorOpaque_F(float par1, float par2, float par3) {
      if (RenderingScheme.current == 102) {
         super.setColorOpaque_F(par1, par2, par3);
      } else {
         if (!this.isColorDisabled) {
            this.hasColor = true;
            if (little_endian) {
               this.color = -16777216 | (int)(par3 * 255.0F) << 16 | (int)(par2 * 255.0F) << 8 | (int)(par1 * 255.0F);
            } else {
               this.color = (int)(par1 * 255.0F) << 24 | (int)(par2 * 255.0F) << 16 | (int)(par3 * 255.0F) << 8 | 255;
            }
         }

      }
   }

   public void setColorRGBA_F(float par1, float par2, float par3, float par4) {
      if (RenderingScheme.current == 102) {
         super.setColorRGBA_F(par1, par2, par3, par4);
      } else {
         if (!this.isColorDisabled) {
            this.hasColor = true;
            if (little_endian) {
               this.color = (int)(par4 * 255.0F) << 24 | (int)(par3 * 255.0F) << 16 | (int)(par2 * 255.0F) << 8 | (int)(par1 * 255.0F);
            } else {
               this.color = (int)(par1 * 255.0F) << 24 | (int)(par2 * 255.0F) << 16 | (int)(par3 * 255.0F) << 8 | (int)(par4 * 255.0F);
            }
         }

      }
   }

   public void setColorRGBA(int par1, int par2, int par3, int par4) {
      if (RenderingScheme.current == 102) {
         super.setColorRGBA(par1, par2, par3, par4);
      } else {
         if (!this.isColorDisabled) {
            this.hasColor = true;
            if (little_endian) {
               this.color = par4 << 24 | par3 << 16 | par2 << 8 | par1;
            } else {
               this.color = par1 << 24 | par2 << 16 | par3 << 8 | par4;
            }
         }

      }
   }

   public void add4VerticesWithUVandAO(double[] x, double[] y, double[] z, double[] u, double[] v, float[] r, float[] g, float[] b, int[] brightness) {
      this.hasTexture = true;
      this.hasBrightness = true;
      if (!this.isColorDisabled) {
         this.hasColor = true;
         if (little_endian) {
            this.rawBuffer[this.rawBufferIndex + 5] = -16777216 | (int)(b[0] * 255.0F) << 16 | (int)(g[0] * 255.0F) << 8 | (int)(r[0] * 255.0F);
            this.rawBuffer[this.rawBufferIndex + 13] = -16777216 | (int)(b[1] * 255.0F) << 16 | (int)(g[1] * 255.0F) << 8 | (int)(r[1] * 255.0F);
            this.rawBuffer[this.rawBufferIndex + 21] = -16777216 | (int)(b[2] * 255.0F) << 16 | (int)(g[2] * 255.0F) << 8 | (int)(r[2] * 255.0F);
            this.rawBuffer[this.rawBufferIndex + 29] = -16777216 | (int)(b[3] * 255.0F) << 16 | (int)(g[3] * 255.0F) << 8 | (int)(r[3] * 255.0F);
         } else {
            this.rawBuffer[this.rawBufferIndex + 5] = (int)(r[0] * 255.0F) << 24 | (int)(g[0] * 255.0F) << 16 | (int)(b[0] * 255.0F) << 8 | 255;
            this.rawBuffer[this.rawBufferIndex + 13] = (int)(r[1] * 255.0F) << 24 | (int)(g[1] * 255.0F) << 16 | (int)(b[1] * 255.0F) << 8 | 255;
            this.rawBuffer[this.rawBufferIndex + 21] = (int)(r[2] * 255.0F) << 24 | (int)(g[2] * 255.0F) << 16 | (int)(b[2] * 255.0F) << 8 | 255;
            this.rawBuffer[this.rawBufferIndex + 29] = (int)(r[3] * 255.0F) << 24 | (int)(g[3] * 255.0F) << 16 | (int)(b[3] * 255.0F) << 8 | 255;
         }
      }

      this.rawBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits((float)u[0]);
      this.rawBuffer[this.rawBufferIndex + 11] = Float.floatToRawIntBits((float)u[1]);
      this.rawBuffer[this.rawBufferIndex + 19] = Float.floatToRawIntBits((float)u[2]);
      this.rawBuffer[this.rawBufferIndex + 27] = Float.floatToRawIntBits((float)u[3]);
      this.rawBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits((float)v[0]);
      this.rawBuffer[this.rawBufferIndex + 12] = Float.floatToRawIntBits((float)v[1]);
      this.rawBuffer[this.rawBufferIndex + 20] = Float.floatToRawIntBits((float)v[2]);
      this.rawBuffer[this.rawBufferIndex + 28] = Float.floatToRawIntBits((float)v[3]);
      this.rawBuffer[this.rawBufferIndex + 7] = brightness[0];
      this.rawBuffer[this.rawBufferIndex + 15] = brightness[1];
      this.rawBuffer[this.rawBufferIndex + 23] = brightness[2];
      this.rawBuffer[this.rawBufferIndex + 31] = brightness[3];
      if (this.hasNormals) {
         this.rawBuffer[this.rawBufferIndex + 6] = this.normal;
         this.rawBuffer[this.rawBufferIndex + 14] = this.normal;
         this.rawBuffer[this.rawBufferIndex + 22] = this.normal;
         this.rawBuffer[this.rawBufferIndex + 30] = this.normal;
      }

      this.rawBuffer[this.rawBufferIndex + 0] = Float.floatToRawIntBits((float)(x[0] + this.xOffset));
      this.rawBuffer[this.rawBufferIndex + 8] = Float.floatToRawIntBits((float)(x[1] + this.xOffset));
      this.rawBuffer[this.rawBufferIndex + 16] = Float.floatToRawIntBits((float)(x[2] + this.xOffset));
      this.rawBuffer[this.rawBufferIndex + 24] = Float.floatToRawIntBits((float)(x[3] + this.xOffset));
      this.rawBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits((float)(y[0] + this.yOffset));
      this.rawBuffer[this.rawBufferIndex + 9] = Float.floatToRawIntBits((float)(y[1] + this.yOffset));
      this.rawBuffer[this.rawBufferIndex + 17] = Float.floatToRawIntBits((float)(y[2] + this.yOffset));
      this.rawBuffer[this.rawBufferIndex + 25] = Float.floatToRawIntBits((float)(y[3] + this.yOffset));
      this.rawBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits((float)(z[0] + this.zOffset));
      this.rawBuffer[this.rawBufferIndex + 10] = Float.floatToRawIntBits((float)(z[1] + this.zOffset));
      this.rawBuffer[this.rawBufferIndex + 18] = Float.floatToRawIntBits((float)(z[2] + this.zOffset));
      this.rawBuffer[this.rawBufferIndex + 26] = Float.floatToRawIntBits((float)(z[3] + this.zOffset));
      this.rawBufferIndex += 32;
      this.addedVertices += 4;
      this.vertexCount += 4;
      if (this.rawBufferIndex >= 2097120) {
         this.draw();
         this.isDrawing = true;
      }

   }

   public void add4VerticesWithUV(double[] x, double[] y, double[] z, double[] u, double[] v) {
      this.hasTexture = true;
      this.rawBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits((float)u[0]);
      this.rawBuffer[this.rawBufferIndex + 11] = Float.floatToRawIntBits((float)u[1]);
      this.rawBuffer[this.rawBufferIndex + 19] = Float.floatToRawIntBits((float)u[2]);
      this.rawBuffer[this.rawBufferIndex + 27] = Float.floatToRawIntBits((float)u[3]);
      this.rawBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits((float)v[0]);
      this.rawBuffer[this.rawBufferIndex + 12] = Float.floatToRawIntBits((float)v[1]);
      this.rawBuffer[this.rawBufferIndex + 20] = Float.floatToRawIntBits((float)v[2]);
      this.rawBuffer[this.rawBufferIndex + 28] = Float.floatToRawIntBits((float)v[3]);
      if (this.hasBrightness) {
         this.rawBuffer[this.rawBufferIndex + 7] = this.brightness;
         this.rawBuffer[this.rawBufferIndex + 15] = this.brightness;
         this.rawBuffer[this.rawBufferIndex + 23] = this.brightness;
         this.rawBuffer[this.rawBufferIndex + 31] = this.brightness;
      }

      if (this.hasColor) {
         this.rawBuffer[this.rawBufferIndex + 5] = this.color;
         this.rawBuffer[this.rawBufferIndex + 13] = this.color;
         this.rawBuffer[this.rawBufferIndex + 21] = this.color;
         this.rawBuffer[this.rawBufferIndex + 29] = this.color;
      }

      if (this.hasNormals) {
         this.rawBuffer[this.rawBufferIndex + 6] = this.normal;
         this.rawBuffer[this.rawBufferIndex + 14] = this.normal;
         this.rawBuffer[this.rawBufferIndex + 22] = this.normal;
         this.rawBuffer[this.rawBufferIndex + 30] = this.normal;
      }

      this.rawBuffer[this.rawBufferIndex + 0] = Float.floatToRawIntBits((float)(x[0] + this.xOffset));
      this.rawBuffer[this.rawBufferIndex + 8] = Float.floatToRawIntBits((float)(x[1] + this.xOffset));
      this.rawBuffer[this.rawBufferIndex + 16] = Float.floatToRawIntBits((float)(x[2] + this.xOffset));
      this.rawBuffer[this.rawBufferIndex + 24] = Float.floatToRawIntBits((float)(x[3] + this.xOffset));
      this.rawBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits((float)(y[0] + this.yOffset));
      this.rawBuffer[this.rawBufferIndex + 9] = Float.floatToRawIntBits((float)(y[1] + this.yOffset));
      this.rawBuffer[this.rawBufferIndex + 17] = Float.floatToRawIntBits((float)(y[2] + this.yOffset));
      this.rawBuffer[this.rawBufferIndex + 25] = Float.floatToRawIntBits((float)(y[3] + this.yOffset));
      this.rawBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits((float)(z[0] + this.zOffset));
      this.rawBuffer[this.rawBufferIndex + 10] = Float.floatToRawIntBits((float)(z[1] + this.zOffset));
      this.rawBuffer[this.rawBufferIndex + 18] = Float.floatToRawIntBits((float)(z[2] + this.zOffset));
      this.rawBuffer[this.rawBufferIndex + 26] = Float.floatToRawIntBits((float)(z[3] + this.zOffset));
      this.rawBufferIndex += 32;
      this.addedVertices += 4;
      this.vertexCount += 4;
      if (this.rawBufferIndex >= 2097120) {
         this.draw();
         this.isDrawing = true;
      }

   }

   public void addVertex(double par1, double par3, double par5) {
      if (RenderingScheme.current == 102) {
         super.addVertex(par1, par3, par5);
      } else {
         ++this.addedVertices;
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
         if (this.vertexCount % 4 == 0 && this.rawBufferIndex >= 2097120) {
            this.draw();
            this.isDrawing = true;
         }

      }
   }

   public void setColorOpaque_I(int par1) {
      if (RenderingScheme.current == 102) {
         super.setColorOpaque_I(par1);
      } else {
         this.setColorRGBA(par1 >> 16 & 255, par1 >> 8 & 255, par1 & 255, 255);
      }
   }

   public void setColorRGBA_I(int par1, int par2) {
      if (RenderingScheme.current == 102) {
         super.setColorRGBA_I(par1, par2);
      } else {
         this.setColorRGBA(par1 >> 16 & 255, par1 >> 8 & 255, par1 & 255, par2);
      }
   }
}
