package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelArachnid extends ModelBase {
   public ModelRenderer head;
   public ModelRenderer neck;
   public ModelRenderer body;
   public ModelRenderer leg1;
   public ModelRenderer leg2;
   public ModelRenderer leg3;
   public ModelRenderer leg4;
   public ModelRenderer leg5;
   public ModelRenderer leg6;
   public ModelRenderer leg7;
   public ModelRenderer leg8;

   public ModelArachnid() {
      float var1 = 0.0F;
      byte var2 = 15;
      this.head = new ModelRenderer(this, 32, 4);
      this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, var1);
      this.head.setRotationPoint(0.0F, (float)var2, -3.0F);
      this.neck = new ModelRenderer(this, 0, 0);
      this.neck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, var1);
      this.neck.setRotationPoint(0.0F, (float)var2, 0.0F);
      this.body = new ModelRenderer(this, 0, 12);
      this.body.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, var1);
      this.body.setRotationPoint(0.0F, (float)var2, 9.0F);
      this.leg1 = new ModelRenderer(this, 18, 0);
      this.leg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.leg1.setRotationPoint(-4.0F, (float)var2, 2.0F);
      this.leg2 = new ModelRenderer(this, 18, 0);
      this.leg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.leg2.setRotationPoint(4.0F, (float)var2, 2.0F);
      this.leg3 = new ModelRenderer(this, 18, 0);
      this.leg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.leg3.setRotationPoint(-4.0F, (float)var2, 1.0F);
      this.leg4 = new ModelRenderer(this, 18, 0);
      this.leg4.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.leg4.setRotationPoint(4.0F, (float)var2, 1.0F);
      this.leg5 = new ModelRenderer(this, 18, 0);
      this.leg5.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.leg5.setRotationPoint(-4.0F, (float)var2, 0.0F);
      this.leg6 = new ModelRenderer(this, 18, 0);
      this.leg6.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.leg6.setRotationPoint(4.0F, (float)var2, 0.0F);
      this.leg7 = new ModelRenderer(this, 18, 0);
      this.leg7.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.leg7.setRotationPoint(-4.0F, (float)var2, -1.0F);
      this.leg8 = new ModelRenderer(this, 18, 0);
      this.leg8.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.leg8.setRotationPoint(4.0F, (float)var2, -1.0F);
   }

   public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
      this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
      this.head.render(par7);
      this.neck.render(par7);
      this.body.render(par7);
      this.leg1.render(par7);
      this.leg2.render(par7);
      this.leg3.render(par7);
      this.leg4.render(par7);
      this.leg5.render(par7);
      this.leg6.render(par7);
      this.leg7.render(par7);
      this.leg8.render(par7);
   }

   public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
      this.head.rotateAngleY = par4 / 57.295776F;
      this.head.rotateAngleX = par5 / 57.295776F;
      float var8 = 0.7853982F;
      this.leg1.rotateAngleZ = -var8;
      this.leg2.rotateAngleZ = var8;
      this.leg3.rotateAngleZ = -var8 * 0.74F;
      this.leg4.rotateAngleZ = var8 * 0.74F;
      this.leg5.rotateAngleZ = -var8 * 0.74F;
      this.leg6.rotateAngleZ = var8 * 0.74F;
      this.leg7.rotateAngleZ = -var8;
      this.leg8.rotateAngleZ = var8;
      float var9 = -0.0F;
      float var10 = 0.3926991F;
      this.leg1.rotateAngleY = var10 * 2.0F + var9;
      this.leg2.rotateAngleY = -var10 * 2.0F - var9;
      this.leg3.rotateAngleY = var10 * 1.0F + var9;
      this.leg4.rotateAngleY = -var10 * 1.0F - var9;
      this.leg5.rotateAngleY = -var10 * 1.0F + var9;
      this.leg6.rotateAngleY = var10 * 1.0F - var9;
      this.leg7.rotateAngleY = -var10 * 2.0F + var9;
      this.leg8.rotateAngleY = var10 * 2.0F - var9;
      float var11 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + 0.0F) * 0.4F) * par2;
      float var12 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * par2;
      float var13 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * par2;
      float var14 = -(MathHelper.cos(par1 * 0.6662F * 2.0F + 4.712389F) * 0.4F) * par2;
      float var15 = Math.abs(MathHelper.sin(par1 * 0.6662F + 0.0F) * 0.4F) * par2;
      float var16 = Math.abs(MathHelper.sin(par1 * 0.6662F + 3.1415927F) * 0.4F) * par2;
      float var17 = Math.abs(MathHelper.sin(par1 * 0.6662F + 1.5707964F) * 0.4F) * par2;
      float var18 = Math.abs(MathHelper.sin(par1 * 0.6662F + 4.712389F) * 0.4F) * par2;
      ModelRenderer var10000 = this.leg1;
      var10000.rotateAngleY += var11;
      var10000 = this.leg2;
      var10000.rotateAngleY += -var11;
      var10000 = this.leg3;
      var10000.rotateAngleY += var12;
      var10000 = this.leg4;
      var10000.rotateAngleY += -var12;
      var10000 = this.leg5;
      var10000.rotateAngleY += var13;
      var10000 = this.leg6;
      var10000.rotateAngleY += -var13;
      var10000 = this.leg7;
      var10000.rotateAngleY += var14;
      var10000 = this.leg8;
      var10000.rotateAngleY += -var14;
      var10000 = this.leg1;
      var10000.rotateAngleZ += var15;
      var10000 = this.leg2;
      var10000.rotateAngleZ += -var15;
      var10000 = this.leg3;
      var10000.rotateAngleZ += var16;
      var10000 = this.leg4;
      var10000.rotateAngleZ += -var16;
      var10000 = this.leg5;
      var10000.rotateAngleZ += var17;
      var10000 = this.leg6;
      var10000.rotateAngleZ += -var17;
      var10000 = this.leg7;
      var10000.rotateAngleZ += var18;
      var10000 = this.leg8;
      var10000.rotateAngleZ += -var18;
   }
}
