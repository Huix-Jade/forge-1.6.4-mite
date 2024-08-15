package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySuspendFX extends EntityFX {
   public EntitySuspendFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
      super(par1World, par2, par4 - 0.125, par6, par8, par10, par12);
      this.particleRed = 0.4F;
      this.particleGreen = 0.4F;
      this.particleBlue = 0.7F;
      this.setParticleTextureIndex(0);
      this.setSize(0.01F, 0.01F);
      this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
      this.motionX = par8 * 0.0;
      this.motionY = par10 * 0.0;
      this.motionZ = par12 * 0.0;
      this.particleMaxAge = (int)(32.0 / (Math.random() * 0.8 + 0.2));
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      if (this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.15000000596046448), MathHelper.floor_double(this.posZ)) != Material.water) {
         this.setDead();
      }

      if (this.particleMaxAge-- <= 0) {
         this.setDead();
      }

   }
}
