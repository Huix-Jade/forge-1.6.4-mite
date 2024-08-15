package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelLeashKnot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderLeashKnot extends Render {
	private static final ResourceLocation leashKnotTextures = new ResourceLocation("textures/entity/lead_knot.png");
	private ModelLeashKnot leashKnotModel = new ModelLeashKnot();

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.func_110799_a((EntityLeashKnot)par1Entity, par2, par4, par6, par8, par9);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return this.getLeashKnotTextures((EntityLeashKnot)entity);
	}


	public void func_110799_a(EntityLeashKnot entityLeashKnot, double d, double e, double f, float g, float h) {
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		GL11.glTranslatef((float)d, (float)e, (float)f);
		float var10 = 0.0625F;
		GL11.glEnable(32826);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		GL11.glEnable(3008);
		this.bindEntityTexture(entityLeashKnot);
		this.leashKnotModel.render(entityLeashKnot, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var10);
		GL11.glPopMatrix();
	}

	protected ResourceLocation getLeashKnotTextures(EntityLeashKnot entityLeashKnot) {
		return leashKnotTextures;
	}
}
