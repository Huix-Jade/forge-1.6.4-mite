package net.minecraft.client.renderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGelatinousCube;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderGelatinousCube extends RenderLiving {
   public static final int texture_green_slime = 0;
   public static final int texture_ochre_jelly = 1;
   public static final int texture_crimson_blob = 2;
   public static final int texture_gray_ooze = 3;
   public static final int texture_black_pudding = 4;
   private ModelBase scaleAmount;

   public RenderGelatinousCube(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3) {
      super(par1ModelBase, par3);
      this.scaleAmount = par2ModelBase;
   }

   protected void setTextures() {
      this.setTexture(0, "textures/entity/slime/slime");
      this.setTexture(1, "textures/entity/slime/jelly");
      this.setTexture(2, "textures/entity/slime/blob");
      this.setTexture(3, "textures/entity/slime/ooze");
      this.setTexture(4, "textures/entity/slime/pudding");
   }

   protected int shouldRenderPass(EntityGelatinousCube entity_gelatinous_cube, int par2, float par3) {
      if (entity_gelatinous_cube.isInvisible()) {
         return 0;
      } else if (par2 == 0) {
         this.setRenderPassModel(this.scaleAmount);
         GL11.glEnable(2977);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         return 1;
      } else {
         if (par2 == 1) {
            GL11.glDisable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         }

         return -1;
      }
   }

   protected void scaleGelatinousCube(EntityGelatinousCube entity_gelatinous_cube, float par2) {
      float var3 = (float)entity_gelatinous_cube.getSize();
      float var4 = (entity_gelatinous_cube.prevSquishFactor + (entity_gelatinous_cube.squishFactor - entity_gelatinous_cube.prevSquishFactor) * par2) / (var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      GL11.glScalef(var5 * var3, 1.0F / var5 * var3, var5 * var3);
   }

   protected ResourceLocation getTextures(EntityGelatinousCube entity_gelatinous_cube) {
      return this.textures[entity_gelatinous_cube.isSlime() ? 0 : (entity_gelatinous_cube.isJelly() ? 1 : (entity_gelatinous_cube.isBlob() ? 2 : (entity_gelatinous_cube.isOoze() ? 3 : 4)))];
   }

   protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
      this.scaleGelatinousCube((EntityGelatinousCube)par1EntityLivingBase, par2);
   }

   protected int shouldRenderPass(EntityLivingBase entity_living_base, int par2, float par3) {
      return this.shouldRenderPass((EntityGelatinousCube)entity_living_base, par2, par3);
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return this.getTextures((EntityGelatinousCube)entity);
   }
}
