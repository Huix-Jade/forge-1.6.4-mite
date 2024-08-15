package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderXPOrb extends Render {
	private static final ResourceLocation experienceOrbTextures = new ResourceLocation("textures/entity/experience_orb.png");

	public RenderXPOrb() {
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.renderTheXPOrb((EntityXPOrb)par1Entity, par2, par4, par6, par8, par9);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return this.getExperienceOrbTextures((EntityXPOrb)entity);
	}

	public void renderTheXPOrb(EntityXPOrb entityXPOrb, double d, double e, double f, float g, float h) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d, (float)e, (float)f);
		this.bindEntityTexture(entityXPOrb);
		int var10 = entityXPOrb.getTextureByXP();
		float var11 = (float)(var10 % 4 * 16 + 0) / 64.0F;
		float var12 = (float)(var10 % 4 * 16 + 16) / 64.0F;
		float var13 = (float)(var10 / 4 * 16 + 0) / 64.0F;
		float var14 = (float)(var10 / 4 * 16 + 16) / 64.0F;
		float var15 = 1.0F;
		float var16 = 0.5F;
		float var17 = 0.25F;
		int var18 = entityXPOrb.getBrightnessForRender(h);
		int var19 = var18 % 65536;
		int var20 = var18 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var19 / 1.0F, (float)var20 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float var26 = 255.0F;
		float var27 = ((float)entityXPOrb.xpColor + h) / 2.0F;
		var20 = (int)((MathHelper.sin(var27 + 0.0F) + 1.0F) * 0.5F * var26);
		int var21 = (int)var26;
		int var22 = (int)((MathHelper.sin(var27 + 4.1887903F) + 1.0F) * 0.1F * var26);
		int var23 = var20 << 16 | var21 << 8 | var22;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		float var24 = 0.3F;
		GL11.glScalef(var24, var24, var24);
		Tessellator var25 = Tessellator.instance;
		var25.startDrawingQuads();
		var25.setColorRGBA_I(var23, 128);
		var25.setNormal(0.0F, 1.0F, 0.0F);
		var25.addVertexWithUV((double)(0.0F - var16), (double)(0.0F - var17), 0.0, (double)var11, (double)var14);
		var25.addVertexWithUV((double)(var15 - var16), (double)(0.0F - var17), 0.0, (double)var12, (double)var14);
		var25.addVertexWithUV((double)(var15 - var16), (double)(1.0F - var17), 0.0, (double)var12, (double)var13);
		var25.addVertexWithUV((double)(0.0F - var16), (double)(1.0F - var17), 0.0, (double)var11, (double)var13);
		var25.draw();
		GL11.glDisable(3042);
		GL11.glDisable(32826);
		GL11.glPopMatrix();
	}

	protected ResourceLocation getExperienceOrbTextures(EntityXPOrb entityXPOrb) {
		return experienceOrbTextures;
	}
}
