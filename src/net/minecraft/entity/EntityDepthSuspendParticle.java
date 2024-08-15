package net.minecraft.entity;

import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.world.World;

public class EntityDepthSuspendParticle extends EntityAuraFX {
   public static EntityDepthSuspendParticle[] cached_objects = new EntityDepthSuspendParticle[256];
   public static int num_cached_objects;

   public EntityDepthSuspendParticle(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
      super(par1World, par2, par4, par6, par8, par10, par12);
      this.motionX = 0.0;
      this.motionY = 0.006000000052154064;
      this.motionZ = 0.0;
   }

   public void setDead() {
      if (num_cached_objects < cached_objects.length) {
         cached_objects[num_cached_objects++] = this;
      }

      super.setDead();
   }

   public static EntityAuraFX getCachedOrCreate(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
      if (num_cached_objects == 0) {
         return new EntityDepthSuspendParticle(par1World, par2, par4, par6, par8, par10, par12);
      } else {
         EntityAuraFX fx = cached_objects[--num_cached_objects];
         fx.worldObj = par1World;
         fx.setPosition(par2, par4, par6);
         fx.lastTickPosX = par2;
         fx.lastTickPosY = par4;
         fx.lastTickPosZ = par6;
         fx.motionX = 0.0;
         fx.motionY = 0.006000000052154064;
         fx.motionZ = 0.0;
         fx.particleAge = 0;
         fx.isDead = false;
         return fx;
      }
   }
}
