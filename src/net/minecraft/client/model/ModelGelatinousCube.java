package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelGelatinousCube extends ModelBase {
   ModelRenderer bodies;
   ModelRenderer right_eye;
   ModelRenderer left_eye;
   ModelRenderer mouth;

   public ModelGelatinousCube(int par1) {
      this.bodies = new ModelRenderer(this, 0, par1);
      this.bodies.addBox(-4.0F, 16.0F, -4.0F, 8, 8, 8);
      if (par1 > 0) {
         this.bodies = new ModelRenderer(this, 0, par1);
         this.bodies.addBox(-3.0F, 17.0F, -3.0F, 6, 6, 6);
         this.right_eye = new ModelRenderer(this, 32, 0);
         this.right_eye.addBox(-3.25F, 18.0F, -3.5F, 2, 2, 2);
         this.left_eye = new ModelRenderer(this, 32, 4);
         this.left_eye.addBox(1.25F, 18.0F, -3.5F, 2, 2, 2);
         this.mouth = new ModelRenderer(this, 32, 8);
         this.mouth.addBox(0.0F, 21.0F, -3.5F, 1, 1, 1);
      }

   }

   public void render(Entity par1Entity, float limb_swing_fraction, float limb_swing_extent, float ticks_existed_plus_partial_tick, float yaw_difference, float pitch, float par7) {
      this.setRotationAngles(limb_swing_fraction, limb_swing_extent, ticks_existed_plus_partial_tick, yaw_difference, pitch, par7, par1Entity);
      this.bodies.render(par7);
      if (this.right_eye != null) {
         this.right_eye.render(par7);
         this.left_eye.render(par7);
         this.mouth.render(par7);
      }

   }
}
