package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBoat extends Render {
	private static final ResourceLocation boatTextures = new ResourceLocation("textures/entity/boat.png");
	protected ModelBase modelBoat;

	public RenderBoat() {
		this.shadowSize = 0.5F;
		this.modelBoat = new ModelBoat();
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity)
	{
		return this.getBoatTextures((EntityBoat)par1Entity);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.renderBoat((EntityBoat)par1Entity, par2, par4, par6, par8, par9);
	}

	public void renderBoat(EntityBoat entityBoat, double d, double e, double f, float g, float h) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d, (float)e, (float)f);
		GL11.glRotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		float var10 = (float)entityBoat.getTimeSinceHit() - h;
		float var11 = entityBoat.getDamageTaken() - h;
		if (var11 < 0.0F) {
			var11 = 0.0F;
		}

		if (var10 > 0.0F) {
			GL11.glRotatef(MathHelper.sin(var10) * var10 * var11 / 10.0F * (float)entityBoat.getForwardDirection(), 1.0F, 0.0F, 0.0F);
		}

		float var12 = 0.75F;
		GL11.glScalef(var12, var12, var12);
		GL11.glScalef(1.0F / var12, 1.0F / var12, 1.0F / var12);
		this.bindEntityTexture(entityBoat);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		this.modelBoat.render(entityBoat, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}

	protected ResourceLocation getBoatTextures(EntityBoat entityBoat) {
		return boatTextures;
	}
}
