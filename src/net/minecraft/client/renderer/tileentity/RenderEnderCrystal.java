package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEnderCrystal extends Render {
	private static final ResourceLocation enderCrystalTextures = new ResourceLocation("textures/entity/endercrystal/endercrystal.png");
	private ModelBase field_76995_b;

	public RenderEnderCrystal() {
		this.shadowSize = 0.5F;
		this.field_76995_b = new ModelEnderCrystal(0.0F, true);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity)
	{
		return this.getEnderCrystalTextures((EntityEnderCrystal)par1Entity);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.doRenderEnderCrystal((EntityEnderCrystal)par1Entity, par2, par4, par6, par8, par9);
	}

	public void doRenderEnderCrystal(EntityEnderCrystal entityEnderCrystal, double d, double e, double f, float g, float h) {
		float var10 = (float)entityEnderCrystal.innerRotation + h;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d, (float)e, (float)f);
		this.bindTexture(enderCrystalTextures);
		float var11 = MathHelper.sin(var10 * 0.2F) / 2.0F + 0.5F;
		var11 += var11 * var11;
		this.field_76995_b.render(entityEnderCrystal, 0.0F, var10 * 3.0F, var11 * 0.2F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}

	protected ResourceLocation getEnderCrystalTextures(EntityEnderCrystal entityEnderCrystal) {
		return enderCrystalTextures;
	}
}
