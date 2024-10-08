package net.minecraft.client.particle;

import net.minecraft.client.renderer.RNG;
import net.minecraft.world.World;

public class EntityAuraFX extends EntityFX {
   protected static int random_number_index;

   public EntityAuraFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
      super(par1World, par2, par4, par6, par8, par10, par12);
      float var14 = RNG.float_1[++random_number_index & 32767] * 0.1F + 0.2F;
      this.particleRed = var14;
      this.particleGreen = var14;
      this.particleBlue = var14;
      this.setParticleTextureIndex(0);
      this.setSize(0.02F, 0.02F);
      this.particleScale *= this.rand.nextFloat() * 0.6F + 0.5F;
      this.motionX *= 0.019999999552965164;
      this.motionY *= 0.019999999552965164;
      this.motionZ *= 0.019999999552965164;
      this.particleMaxAge = (int)(20.0 / ((double)RNG.float_1[++random_number_index & 32767] * 0.8 + 0.2));
      this.noClip = true;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.99;
      this.motionY *= 0.99;
      this.motionZ *= 0.99;
      if (++this.particleAge > this.particleMaxAge) {
         this.setDead();
      }

   }
}
