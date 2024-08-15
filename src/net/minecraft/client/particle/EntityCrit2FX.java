package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class EntityCrit2FX extends EntityFX {
   private Entity theEntity;
   private int currentLife;
   private int maximumLife;
   EnumParticle particle;

   public EntityCrit2FX(World par1World, Entity par2Entity) {
      this(par1World, par2Entity, EnumParticle.crit);
   }

   public EntityCrit2FX(World par1World, Entity par2Entity, EnumParticle particle) {
      super(par1World, par2Entity.posX, par2Entity.boundingBox.minY + (double)(par2Entity.height / 2.0F), par2Entity.posZ, par2Entity.motionX, par2Entity.motionY, par2Entity.motionZ);
      this.theEntity = par2Entity;
      this.maximumLife = 3;
      this.particle = particle;
      this.onUpdate();
   }

   public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
   }

   public void onUpdate() {
      for(int var1 = 0; var1 < 16; ++var1) {
         double var2 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
         double var4 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
         if (var2 * var2 + var4 * var4 + var6 * var6 <= 1.0) {
            double var8 = this.theEntity.posX + var2 * (double)this.theEntity.width / 4.0;
            double var10 = this.theEntity.boundingBox.minY + (double)(this.theEntity.height / 2.0F) + var4 * (double)this.theEntity.height / 4.0;
            double var12 = this.theEntity.posZ + var6 * (double)this.theEntity.width / 4.0;
            this.worldObj.spawnParticle(this.particle, var8, var10, var12, var2, var4 + 0.2, var6);
         }
      }

      ++this.currentLife;
      if (this.currentLife >= this.maximumLife) {
         this.setDead();
      }

   }

   public int getFXLayer() {
      return 3;
   }
}
