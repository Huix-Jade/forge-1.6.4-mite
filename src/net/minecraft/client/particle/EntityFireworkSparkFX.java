package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityFireworkSparkFX extends EntityFX {
   private int baseTextureIndex = 160;
   private boolean field_92054_ax;
   private boolean field_92048_ay;
   private final EffectRenderer field_92047_az;
   private float fadeColourRed;
   private float fadeColourGreen;
   private float fadeColourBlue;
   private boolean hasFadeColour;

   public EntityFireworkSparkFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, EffectRenderer var14) {
      super(var1, var2, var4, var6);
      this.motionX = var8;
      this.motionY = var10;
      this.motionZ = var12;
      this.field_92047_az = var14;
      this.particleScale *= 0.75F;
      this.particleMaxAge = 48 + this.rand.nextInt(12);
      this.noClip = false;
   }

   public void setTrail(boolean var1) {
      this.field_92054_ax = var1;
   }

   public void setTwinkle(boolean var1) {
      this.field_92048_ay = var1;
   }

   public void setColour(int var1) {
      float var2 = (float)((var1 & 16711680) >> 16) / 255.0F;
      float var3 = (float)((var1 & '\uff00') >> 8) / 255.0F;
      float var4 = (float)((var1 & 255) >> 0) / 255.0F;
      float var5 = 1.0F;
      this.setRBGColorF(var2 * var5, var3 * var5, var4 * var5);
   }

   public void setFadeColour(int var1) {
      this.fadeColourRed = (float)((var1 & 16711680) >> 16) / 255.0F;
      this.fadeColourGreen = (float)((var1 & '\uff00') >> 8) / 255.0F;
      this.fadeColourBlue = (float)((var1 & 255) >> 0) / 255.0F;
      this.hasFadeColour = true;
   }

   public AxisAlignedBB getBoundingBox() {
      return null;
   }

   public boolean canBePushed() {
      return false;
   }

   public void renderParticle(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      if (!this.field_92048_ay || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
         super.renderParticle(var1, var2, var3, var4, var5, var6, var7);
      }

   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      if (this.particleAge > this.particleMaxAge / 2) {
         this.setAlphaF(1.0F - ((float)this.particleAge - (float)(this.particleMaxAge / 2)) / (float)this.particleMaxAge);
         if (this.hasFadeColour) {
            this.particleRed += (this.fadeColourRed - this.particleRed) * 0.2F;
            this.particleGreen += (this.fadeColourGreen - this.particleGreen) * 0.2F;
            this.particleBlue += (this.fadeColourBlue - this.particleBlue) * 0.2F;
         }
      }

      this.setParticleTextureIndex(this.baseTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
      this.motionY -= 0.004;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9100000262260437;
      this.motionY *= 0.9100000262260437;
      this.motionZ *= 0.9100000262260437;
      if (this.onGround) {
         this.motionX *= 0.699999988079071;
         this.motionZ *= 0.699999988079071;
      }

      if (this.field_92054_ax && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0) {
         EntityFireworkSparkFX var1 = new EntityFireworkSparkFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0, this.field_92047_az);
         var1.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
         var1.particleAge = var1.particleMaxAge / 2;
         if (this.hasFadeColour) {
            var1.hasFadeColour = true;
            var1.fadeColourRed = this.fadeColourRed;
            var1.fadeColourGreen = this.fadeColourGreen;
            var1.fadeColourBlue = this.fadeColourBlue;
         }

         var1.field_92048_ay = this.field_92048_ay;
         this.field_92047_az.addEffect(var1);
      }

   }

   public int getBrightnessForRender(float var1) {
      return 15728880;
   }

   public float getBrightness(float var1) {
      return 1.0F;
   }
}
