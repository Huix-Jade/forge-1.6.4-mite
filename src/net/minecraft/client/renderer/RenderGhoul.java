package net.minecraft.client.renderer;

import net.minecraft.client.model.ModelInvisibleStalker;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGhoul;
import net.minecraft.util.ResourceLocation;

public class RenderGhoul extends RenderBiped {
   public static final int texture_normal = 0;

   public RenderGhoul() {
      super(new ModelInvisibleStalker(), 0.5F);
   }

   protected void setTextures() {
      this.setTexture(0, "textures/entity/ghoul");
   }

   protected ResourceLocation getGhoulTextures(EntityGhoul ghoul) {
      return this.textures[0];
   }

   protected ResourceLocation getEntityTexture(Entity par1Entity) {
      return this.getGhoulTextures((EntityGhoul)par1Entity);
   }
}
