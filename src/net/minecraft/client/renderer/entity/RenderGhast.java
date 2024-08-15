package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGhast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderGhast extends RenderLiving {
	public static final int body_texture = 0;
	public static final int body_texture_shooting = 1;

	public RenderGhast() {
		super(new ModelGhast(), 0.5F);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/ghast/ghast");
		this.setTexture(1, "textures/entity/ghast/ghast_shooting");
	}

	protected ResourceLocation func_110867_a(EntityGhast par1EntityGhast) {
		return par1EntityGhast.func_110182_bF() ? this.textures[1] : this.textures[0];
	}

	protected void preRenderGhast(EntityGhast par1EntityGhast, float par2) {
		float var4 = ((float)par1EntityGhast.prevAttackCounter + (float)(par1EntityGhast.attackCounter - par1EntityGhast.prevAttackCounter) * par2) / 20.0F;
		if (var4 < 0.0F) {
			var4 = 0.0F;
		}

		var4 = 1.0F / (var4 * var4 * var4 * var4 * var4 * 2.0F + 1.0F);
		float var5 = (8.0F + var4) / 2.0F;
		float var6 = (8.0F + 1.0F / var4) / 2.0F;
		GL11.glScalef(var6, var5, var6);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		this.preRenderGhast((EntityGhast)par1EntityLivingBase, par2);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110867_a((EntityGhast)par1Entity);
	}
}
