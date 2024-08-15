package net.minecraft.client.renderer;

import net.minecraft.client.model.ModelInvisibleStalker;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderShadow extends RenderBiped {
   public static final int body_texture = 0;

   public RenderShadow() {
      super(new ModelInvisibleStalker(), 0.5F);
   }

   protected void setTextures() {
      this.setTexture(0, "textures/entity/shadow");
   }

   protected ResourceLocation getEntityTexture(Entity par1Entity) {
      return this.textures[0];
   }
}
