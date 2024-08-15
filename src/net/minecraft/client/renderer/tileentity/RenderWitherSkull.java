package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWitherSkull extends Render {
	private static final ResourceLocation invulnerableWitherTextures = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
	private static final ResourceLocation witherTextures = new ResourceLocation("textures/entity/wither/wither.png");
	private final ModelSkeletonHead skeletonHeadModel = new ModelSkeletonHead();


	protected ResourceLocation getEntityTexture(Entity par1Entity)
	{
		return this.func_110809_a((EntityWitherSkull)par1Entity);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.func_82399_a((EntityWitherSkull)par1Entity, par2, par4, par6, par8, par9);
	}

	private float func_82400_a(float f, float g, float h) {
		float var4;
		for(var4 = g - f; var4 < -180.0F; var4 += 360.0F) {
		}

		while(var4 >= 180.0F) {
			var4 -= 360.0F;
		}

		return f + h * var4;
	}

	public void func_82399_a(EntityWitherSkull entityWitherSkull, double d, double e, double f, float g, float h) {
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		float var10 = this.func_82400_a(entityWitherSkull.prevRotationYaw, entityWitherSkull.rotationYaw, h);
		float var11 = entityWitherSkull.prevRotationPitch + (entityWitherSkull.rotationPitch - entityWitherSkull.prevRotationPitch) * h;
		GL11.glTranslatef((float)d, (float)e, (float)f);
		float var12 = 0.0625F;
		GL11.glEnable(32826);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		GL11.glEnable(3008);
		this.bindEntityTexture(entityWitherSkull);
		this.skeletonHeadModel.render(entityWitherSkull, 0.0F, 0.0F, 0.0F, var10, var11, var12);
		GL11.glPopMatrix();
	}

	protected ResourceLocation func_110809_a(EntityWitherSkull entityWitherSkull) {
		return entityWitherSkull.isInvulnerable() ? invulnerableWitherTextures : witherTextures;
	}
}
