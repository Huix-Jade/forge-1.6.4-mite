package net.minecraft.entity;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RNG;
import net.minecraft.world.World;

public class EntityRepairFX extends EntityFX {
   protected static int random_number_index;

   public EntityRepairFX(World world, double pos_x, double pos_y, double pos_z, double vel_x, double vel_y, double vel_z) {
      super(world, pos_x, pos_y, pos_z, vel_x, vel_y, vel_z);
      this.setParticleTextureIndex(0);
      this.setSize(0.02F, 0.02F);
      this.particleScale = 1.0F + this.rand.nextFloat() * 0.2F;
      this.motionX = vel_x;
      this.motionY = vel_y;
      this.motionZ = vel_z;
      this.particleMaxAge = (int)(15.0 / ((double)RNG.float_1[++random_number_index & 32767] * 0.6 + 0.4));
      this.noClip = true;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9900000095367432;
      this.motionY *= 0.9900000095367432;
      this.motionZ *= 0.9900000095367432;
      if (++this.particleAge > this.particleMaxAge) {
         this.setDead();
      }

   }
}
