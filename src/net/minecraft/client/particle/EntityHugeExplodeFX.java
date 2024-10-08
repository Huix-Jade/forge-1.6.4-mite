package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class EntityHugeExplodeFX extends EntityFX {
   private int timeSinceStart;
   private int maximumTime = 8;

   public EntityHugeExplodeFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
      super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
   }

   public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
   }

   public void onUpdate() {
      for(int var1 = 0; var1 < 6; ++var1) {
         double var2 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         double var4 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         double var6 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         this.worldObj.spawnParticle(EnumParticle.largeexplode, var2, var4, var6, (double)((float)this.timeSinceStart / (float)this.maximumTime), 0.0, 0.0);
      }

      ++this.timeSinceStart;
      if (this.timeSinceStart == this.maximumTime) {
         this.setDead();
      }

   }

   public int getFXLayer() {
      return 1;
   }
}
