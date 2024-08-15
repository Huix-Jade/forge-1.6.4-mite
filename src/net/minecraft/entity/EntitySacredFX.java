package net.minecraft.entity;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RNG;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySacredFX extends EntityFX {
   float reddustParticleScale;
   private static final int random_motion_table_entries = 255;
   private static double[] random_motion_x = new double[255];
   private static double[] random_motion_y = new double[255];
   private static double[] random_motion_z = new double[255];
   private static int next_random_motion_index;

   public EntitySacredFX(World par1World, double par2, double par4, double par6) {
      this(par1World, par2, par4, par6, 1.0F);
   }

   public EntitySacredFX(World par1World, double par2, double par4, double par6, float par8) {
      super(par1World, par2, par4, par6);
      this.motionX = random_motion_x[next_random_motion_index];
      this.motionY = random_motion_y[next_random_motion_index];
      this.motionZ = random_motion_z[next_random_motion_index];
      if (++next_random_motion_index >= 255) {
         next_random_motion_index = 0;
      }

      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 0.9F;
      this.particleAlpha = 0.15F;
      this.particleScale *= 0.25F;
      this.particleScale *= par8;
      this.reddustParticleScale = this.particleScale;
      this.particleMaxAge = 60 + RNG.int_32[++RNG.random_number_index & 32767];
      this.noClip = false;
      this.setParticleTextureIndex(100);
   }

   public int getBrightnessForRender(float par1) {
      float var2 = ((float)this.particleAge + par1) / (float)this.particleMaxAge;
      if (var2 < 0.0F) {
         var2 = 0.0F;
      }

      if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      int var3 = super.getBrightnessForRender(par1);
      short var4 = 240;
      int var5 = var3 >> 16 & 255;
      return var4 | var5 << 16;
   }

   public float getBrightness(float par1) {
      return 1.0F;
   }

   public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
      float var8 = ((float)this.particleAge + par2) / (float)this.particleMaxAge * 32.0F;
      if (var8 < 0.0F) {
         var8 = 0.0F;
      }

      if (var8 > 1.0F) {
         var8 = 1.0F;
      }

      this.particleScale = this.reddustParticleScale * var8;
      super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      this.particleAlpha = (float)(this.particleMaxAge - this.particleAge) / (float)this.particleMaxAge * 0.1F;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      if (this.posY == this.prevPosY) {
         this.motionX *= 1.1;
         this.motionZ *= 1.1;
      }

      this.motionX *= 0.9599999785423279;
      this.motionY *= 0.9599999785423279;
      this.motionZ *= 0.9599999785423279;
      if (this.onGround) {
         this.motionX *= 0.699999988079071;
         this.motionZ *= 0.699999988079071;
      }

   }

   static {
      int random_number_index = 0;

      for(int i = 0; i < 255; ++i) {
         ++random_number_index;
         double motion_x = (double)(RNG.float_1[random_number_index & 32767] * 0.8F - 0.4F);
         ++random_number_index;
         double motion_y = (double)(RNG.float_1[random_number_index & 32767] * 0.8F - 0.4F);
         ++random_number_index;
         double motion_z = (double)(RNG.float_1[random_number_index & 32767] * 0.8F - 0.4F);
         float var14 = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
         float var15 = MathHelper.sqrt_double(motion_x * motion_x + motion_y * motion_y + motion_z * motion_z);
         motion_x = motion_x / (double)var15 * (double)var14 * 0.4000000059604645;
         motion_y = motion_y / (double)var15 * (double)var14 * 0.4000000059604645 + 0.10000000149011612;
         motion_z = motion_z / (double)var15 * (double)var14 * 0.4000000059604645;
         motion_x *= 0.10000000149011612;
         motion_y *= 0.10000000149011612;

         for(motion_z *= 0.10000000149011612; motion_x * motion_x + motion_y * motion_y + motion_z * motion_z > 9.999999747378752E-6; motion_z *= 0.800000011920929) {
            motion_x *= 0.800000011920929;
            motion_y *= 0.800000011920929;
         }

         random_motion_x[i] = motion_x;
         random_motion_y[i] = motion_y;
         random_motion_z[i] = motion_z;
      }

   }
}
