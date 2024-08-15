package net.minecraft.client.renderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;

public class RenderHellhound extends RenderLiving {
   public static final int body_texture = 0;

   public RenderHellhound(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3) {
      super(par1ModelBase, par3);
      this.setRenderPassModel(par2ModelBase);
   }

   protected void setTextures() {
      this.setTexture(0, "textures/entity/hellhound/hellhound");
   }

   protected float getTailRotation(EntityWolf par1EntityWolf, float par2) {
      return par1EntityWolf.getTailRotation();
   }

   protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
      return -1;
   }

   protected float handleRotationFloat(EntityLivingBase par1EntityLivingBase, float par2) {
      return this.getTailRotation((EntityWolf)par1EntityLivingBase, par2);
   }

   protected ResourceLocation getEntityTexture(Entity par1Entity) {
      return this.textures[0];
   }
}
