package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelInvisibleStalker extends ModelBiped {
   public ModelInvisibleStalker() {
      super(0.0F, 0.0F, 64, 32);
   }

   public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
      super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
      float var8 = MathHelper.sin(this.onGround * 3.1415927F);
      float var9 = MathHelper.sin((1.0F - (1.0F - this.onGround) * (1.0F - this.onGround)) * 3.1415927F);
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightArm.rotateAngleY = -(0.1F - var8 * 0.6F);
      this.bipedLeftArm.rotateAngleY = 0.1F - var8 * 0.6F;
      this.bipedRightArm.rotateAngleX = -1.5707964F;
      this.bipedLeftArm.rotateAngleX = -1.5707964F;
      ModelRenderer var10000 = this.bipedRightArm;
      var10000.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
      var10000 = this.bipedRightArm;
      var10000.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.bipedRightArm;
      var10000.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
   }
}
