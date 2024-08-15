package net.minecraft.client.renderer;

import net.minecraft.client.model.ModelInvisibleStalker;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderInvisibleStalker extends RenderBiped {
   public static final int body_texture = 0;

   public RenderInvisibleStalker() {
      super(new ModelInvisibleStalker(), 0.5F);
   }

   protected void setTextures() {
      this.setTexture(0, "textures/entity/wight");
   }

   protected ResourceLocation func_110856_a(EntityLiving par1EntityLiving) {
      return this.textures[0];
   }

   protected ResourceLocation getEntityTexture(Entity par1Entity) {
      return this.textures[0];
   }

   public float getModelOpacity(Entity entity) {
      return 0.05F;
   }
}
