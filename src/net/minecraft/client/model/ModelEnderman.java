package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelEnderman extends ModelBiped {
   public boolean isCarrying;
   public boolean isAttacking;

   public ModelEnderman() {
      super(0.0F, -14.0F, 64, 32);
      float var1 = -14.0F;
      float var2 = 0.0F;
      this.bipedHeadwear = new ModelRenderer(this, 0, 16);
      this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var2 - 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + var1, 0.0F);
      this.bipedBody = new ModelRenderer(this, 32, 16);
      this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, var2);
      this.bipedBody.setRotationPoint(0.0F, 0.0F + var1, 0.0F);
      this.bipedRightArm = new ModelRenderer(this, 56, 0);
      this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, var2);
      this.bipedRightArm.setRotationPoint(-3.0F, 2.0F + var1, 0.0F);
      this.bipedLeftArm = new ModelRenderer(this, 56, 0);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, var2);
      this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + var1, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 56, 0);
      this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, var2);
      this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F + var1, 0.0F);
      this.bipedLeftLeg = new ModelRenderer(this, 56, 0);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, var2);
      this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F + var1, 0.0F);
   }

   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.setRotationAngles(var1, var2, var3, var4, var5, var6, var7);
      this.bipedHead.showModel = true;
      float var8 = -14.0F;
      this.bipedBody.rotateAngleX = 0.0F;
      this.bipedBody.rotationPointY = var8;
      this.bipedBody.rotationPointZ = -0.0F;
      ModelRenderer var10000 = this.bipedRightLeg;
      var10000.rotateAngleX -= 0.0F;
      var10000 = this.bipedLeftLeg;
      var10000.rotateAngleX -= 0.0F;
      var10000 = this.bipedRightArm;
      var10000.rotateAngleX = (float)((double)var10000.rotateAngleX * 0.5);
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleX = (float)((double)var10000.rotateAngleX * 0.5);
      var10000 = this.bipedRightLeg;
      var10000.rotateAngleX = (float)((double)var10000.rotateAngleX * 0.5);
      var10000 = this.bipedLeftLeg;
      var10000.rotateAngleX = (float)((double)var10000.rotateAngleX * 0.5);
      float var9 = 0.4F;
      if (this.bipedRightArm.rotateAngleX > var9) {
         this.bipedRightArm.rotateAngleX = var9;
      }

      if (this.bipedLeftArm.rotateAngleX > var9) {
         this.bipedLeftArm.rotateAngleX = var9;
      }

      if (this.bipedRightArm.rotateAngleX < -var9) {
         this.bipedRightArm.rotateAngleX = -var9;
      }

      if (this.bipedLeftArm.rotateAngleX < -var9) {
         this.bipedLeftArm.rotateAngleX = -var9;
      }

      if (this.bipedRightLeg.rotateAngleX > var9) {
         this.bipedRightLeg.rotateAngleX = var9;
      }

      if (this.bipedLeftLeg.rotateAngleX > var9) {
         this.bipedLeftLeg.rotateAngleX = var9;
      }

      if (this.bipedRightLeg.rotateAngleX < -var9) {
         this.bipedRightLeg.rotateAngleX = -var9;
      }

      if (this.bipedLeftLeg.rotateAngleX < -var9) {
         this.bipedLeftLeg.rotateAngleX = -var9;
      }

      if (this.isCarrying) {
         this.bipedRightArm.rotateAngleX = -0.5F;
         this.bipedLeftArm.rotateAngleX = -0.5F;
         this.bipedRightArm.rotateAngleZ = 0.05F;
         this.bipedLeftArm.rotateAngleZ = -0.05F;
      }

      this.bipedRightArm.rotationPointZ = 0.0F;
      this.bipedLeftArm.rotationPointZ = 0.0F;
      this.bipedRightLeg.rotationPointZ = 0.0F;
      this.bipedLeftLeg.rotationPointZ = 0.0F;
      this.bipedRightLeg.rotationPointY = 9.0F + var8;
      this.bipedLeftLeg.rotationPointY = 9.0F + var8;
      this.bipedHead.rotationPointZ = -0.0F;
      this.bipedHead.rotationPointY = var8 + 1.0F;
      this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX;
      this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY;
      this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ;
      this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
      this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
      this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ;
      if (this.isAttacking) {
         float var10 = 1.0F;
         var10000 = this.bipedHead;
         var10000.rotationPointY -= var10 * 5.0F;
      }

   }
}
