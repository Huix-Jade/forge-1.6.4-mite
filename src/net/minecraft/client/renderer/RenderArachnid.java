package net.minecraft.client.renderer;

import net.minecraft.client.model.ModelArachnid;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityArachnid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public abstract class RenderArachnid extends RenderLiving {
   public static final int body_texture = 0;

   public RenderArachnid(ModelArachnid base_model, ModelArachnid render_pass_model, float scale) {
      super(base_model, scale);
      this.setRenderPassModel(render_pass_model);
   }

   protected void setTextures() {
      this.setTexture(0, "textures/entity/spider/" + this.getSubtypeName());
   }

   protected float setArachnidDeathMaxRotation(EntityArachnid par1EntityArachnid) {
      return 180.0F;
   }

   protected ResourceLocation getArachnidTextures(EntityArachnid par1EntityArachnid) {
      return this.textures[0];
   }

   protected float getDeathMaxRotation(EntityLivingBase par1EntityLivingBase) {
      return this.setArachnidDeathMaxRotation((EntityArachnid)par1EntityLivingBase);
   }

   protected ResourceLocation getEntityTexture(Entity par1Entity) {
      return this.getArachnidTextures((EntityArachnid)par1Entity);
   }

   public abstract String getSubtypeName();
}
