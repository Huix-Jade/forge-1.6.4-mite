package net.minecraft.client.renderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.EntityDireWolf;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class RenderDireWolf extends RenderWolf {
   private float scale;

   public RenderDireWolf(ModelBase par1ModelBase, ModelBase par2ModelBase, float par2, float scale) {
      super(par1ModelBase, par2ModelBase, par2 * scale);
      this.scale = scale;
   }

   protected void setTextures() {
      this.setTexture(0, "textures/entity/dire_wolf/neutral");
      this.setTexture(1, "textures/entity/dire_wolf/tame");
      this.setTexture(2, "textures/entity/dire_wolf/angry");
      this.setTexture(3, "textures/entity/dire_wolf/collar");
   }

   protected void preRenderScale(EntityDireWolf par1EntityDireWolf, float par2) {
      GL11.glScalef(this.scale, this.scale, this.scale);
   }

   protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
      this.preRenderScale((EntityDireWolf)par1EntityLivingBase, par2);
   }
}
