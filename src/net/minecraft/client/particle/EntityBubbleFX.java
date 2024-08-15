package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBubbleFX extends EntityFX {
   public EntityBubbleFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.setParticleTextureIndex(32);
      this.setSize(0.02F, 0.02F);
      this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
      this.motionX = var8 * 0.20000000298023224 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.02F);
      this.motionY = var10 * 0.20000000298023224 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.02F);
      this.motionZ = var12 * 0.20000000298023224 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.02F);
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY += 0.002;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.8500000238418579;
      this.motionY *= 0.8500000238418579;
      this.motionZ *= 0.8500000238418579;
      if (this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) != Material.water) {
         this.setDead();
      }

      if (this.particleMaxAge-- <= 0) {
         this.setDead();
      }

   }
}
