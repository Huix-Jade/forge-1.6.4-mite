package net.minecraft.client.renderer;

import net.minecraft.client.model.ModelArachnid;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class RenderBlackWidowSpider extends RenderArachnid {
   private float scale;

   public RenderBlackWidowSpider(float scale) {
      super(new ModelArachnid(), new ModelArachnid(), scale);
      this.scale = scale;
   }

   protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
      super.preRenderCallback(par1EntityLivingBase, par2);
      GL11.glScalef(this.scale, this.scale, this.scale);
   }

   public String getSubtypeName() {
      return "black_widow";
   }
}
