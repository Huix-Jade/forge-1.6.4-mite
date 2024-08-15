package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkStarterFX extends EntityFX {
   private int fireworkAge;
   private final EffectRenderer theEffectRenderer;
   private NBTTagList fireworkExplosions;
   boolean twinkle;

   public EntityFireworkStarterFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, EffectRenderer var14, NBTTagCompound var15) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.motionX = var8;
      this.motionY = var10;
      this.motionZ = var12;
      this.theEffectRenderer = var14;
      this.particleMaxAge = 8;
      if (var15 != null) {
         this.fireworkExplosions = var15.getTagList("Explosions");
         if (this.fireworkExplosions.tagCount() == 0) {
            this.fireworkExplosions = null;
         } else {
            this.particleMaxAge = this.fireworkExplosions.tagCount() * 2 - 1;

            for(int var16 = 0; var16 < this.fireworkExplosions.tagCount(); ++var16) {
               NBTTagCompound var17 = (NBTTagCompound)this.fireworkExplosions.tagAt(var16);
               if (var17.getBoolean("Flicker")) {
                  this.twinkle = true;
                  this.particleMaxAge += 15;
                  break;
               }
            }
         }
      }

   }

   public void renderParticle(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   public void onUpdate() {
      boolean var1;
      if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
         var1 = this.func_92037_i();
         boolean var2 = false;
         if (this.fireworkExplosions.tagCount() >= 3) {
            var2 = true;
         } else {
            for(int var3 = 0; var3 < this.fireworkExplosions.tagCount(); ++var3) {
               NBTTagCompound var4 = (NBTTagCompound)this.fireworkExplosions.tagAt(var3);
               if (var4.getByte("Type") == 1) {
                  var2 = true;
                  break;
               }
            }
         }

         String var15 = "fireworks." + (var2 ? "largeBlast" : "blast") + (var1 ? "_far" : "");
         this.worldObj.playSound(this.posX, this.posY, this.posZ, var15, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
      }

      if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.tagCount()) {
         int var13 = this.fireworkAge / 2;
         NBTTagCompound var14 = (NBTTagCompound)this.fireworkExplosions.tagAt(var13);
         byte var17 = var14.getByte("Type");
         boolean var18 = var14.getBoolean("Trail");
         boolean var5 = var14.getBoolean("Flicker");
         int[] var6 = var14.getIntArray("Colors");
         int[] var7 = var14.getIntArray("FadeColors");
         if (var17 == 1) {
            this.createBall(0.5, 4, var6, var7, var18, var5);
         } else if (var17 == 2) {
            this.createShaped(0.5, new double[][]{{0.0, 1.0}, {0.3455, 0.309}, {0.9511, 0.309}, {0.3795918367346939, -0.12653061224489795}, {0.6122448979591837, -0.8040816326530612}, {0.0, -0.35918367346938773}}, var6, var7, var18, var5, false);
         } else if (var17 == 3) {
            this.createShaped(0.5, new double[][]{{0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}}, var6, var7, var18, var5, true);
         } else if (var17 == 4) {
            this.createBurst(var6, var7, var18, var5);
         } else {
            this.createBall(0.25, 2, var6, var7, var18, var5);
         }

         int var8 = var6[0];
         float var9 = (float)((var8 & 16711680) >> 16) / 255.0F;
         float var10 = (float)((var8 & '\uff00') >> 8) / 255.0F;
         float var11 = (float)((var8 & 255) >> 0) / 255.0F;
         EntityFireworkOverlayFX var12 = new EntityFireworkOverlayFX(this.worldObj, this.posX, this.posY, this.posZ);
         var12.setRBGColorF(var9, var10, var11);
         this.theEffectRenderer.addEffect(var12);
      }

      ++this.fireworkAge;
      if (this.fireworkAge > this.particleMaxAge) {
         if (this.twinkle) {
            var1 = this.func_92037_i();
            String var16 = "fireworks." + (var1 ? "twinkle_far" : "twinkle");
            this.worldObj.playSound(this.posX, this.posY, this.posZ, var16, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
         }

         this.setDead();
      }

   }

   private boolean func_92037_i() {
      Minecraft var1 = Minecraft.getMinecraft();
      return var1 == null || var1.renderViewEntity == null || !(var1.renderViewEntity.getDistanceSq(this.posX, this.posY, this.posZ) < 256.0);
   }

   private void createParticle(double var1, double var3, double var5, double var7, double var9, double var11, int[] var13, int[] var14, boolean var15, boolean var16) {
      EntityFireworkSparkFX var17 = new EntityFireworkSparkFX(this.worldObj, var1, var3, var5, var7, var9, var11, this.theEffectRenderer);
      var17.setTrail(var15);
      var17.setTwinkle(var16);
      int var18 = this.rand.nextInt(var13.length);
      var17.setColour(var13[var18]);
      if (var14 != null && var14.length > 0) {
         var17.setFadeColour(var14[this.rand.nextInt(var14.length)]);
      }

      this.theEffectRenderer.addEffect(var17);
   }

   private void createBall(double var1, int var3, int[] var4, int[] var5, boolean var6, boolean var7) {
      double var8 = this.posX;
      double var10 = this.posY;
      double var12 = this.posZ;

      for(int var14 = -var3; var14 <= var3; ++var14) {
         for(int var15 = -var3; var15 <= var3; ++var15) {
            for(int var16 = -var3; var16 <= var3; ++var16) {
               double var17 = (double)var15 + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
               double var19 = (double)var14 + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
               double var21 = (double)var16 + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
               double var23 = (double)MathHelper.sqrt_double(var17 * var17 + var19 * var19 + var21 * var21) / var1 + this.rand.nextGaussian() * 0.05;
               this.createParticle(var8, var10, var12, var17 / var23, var19 / var23, var21 / var23, var4, var5, var6, var7);
               if (var14 != -var3 && var14 != var3 && var15 != -var3 && var15 != var3) {
                  var16 += var3 * 2 - 1;
               }
            }
         }
      }

   }

   private void createShaped(double var1, double[][] var3, int[] var4, int[] var5, boolean var6, boolean var7, boolean var8) {
      double var9 = var3[0][0];
      double var11 = var3[0][1];
      this.createParticle(this.posX, this.posY, this.posZ, var9 * var1, var11 * var1, 0.0, var4, var5, var6, var7);
      float var13 = this.rand.nextFloat() * 3.1415927F;
      double var14 = var8 ? 0.034 : 0.34;

      for(int var16 = 0; var16 < 3; ++var16) {
         double var17 = (double)var13 + (double)((float)var16 * 3.1415927F) * var14;
         double var19 = var9;
         double var21 = var11;

         for(int var23 = 1; var23 < var3.length; ++var23) {
            double var24 = var3[var23][0];
            double var26 = var3[var23][1];

            for(double var28 = 0.25; var28 <= 1.0; var28 += 0.25) {
               double var30 = (var19 + (var24 - var19) * var28) * var1;
               double var32 = (var21 + (var26 - var21) * var28) * var1;
               double var34 = var30 * Math.sin(var17);
               var30 *= Math.cos(var17);

               for(double var36 = -1.0; var36 <= 1.0; var36 += 2.0) {
                  this.createParticle(this.posX, this.posY, this.posZ, var30 * var36, var32, var34 * var36, var4, var5, var6, var7);
               }
            }

            var19 = var24;
            var21 = var26;
         }
      }

   }

   private void createBurst(int[] var1, int[] var2, boolean var3, boolean var4) {
      double var5 = this.rand.nextGaussian() * 0.05;
      double var7 = this.rand.nextGaussian() * 0.05;

      for(int var9 = 0; var9 < 70; ++var9) {
         double var10 = this.motionX * 0.5 + this.rand.nextGaussian() * 0.15 + var5;
         double var12 = this.motionZ * 0.5 + this.rand.nextGaussian() * 0.15 + var7;
         double var14 = this.motionY * 0.5 + this.rand.nextDouble() * 0.5;
         this.createParticle(this.posX, this.posY, this.posZ, var10, var14, var12, var1, var2, var3, var4);
      }

   }

   public int getFXLayer() {
      return 0;
   }
}
