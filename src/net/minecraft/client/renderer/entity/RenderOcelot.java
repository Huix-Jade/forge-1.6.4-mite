package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderOcelot extends RenderLiving {
	public static final int body_texture_ocelot = 0;
	public static final int body_texture_black = 1;
	public static final int body_texture_red = 2;
	public static final int body_texture_siamese = 3;

	public RenderOcelot(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/cat/ocelot");
		this.setTexture(1, "textures/entity/cat/black");
		this.setTexture(2, "textures/entity/cat/red");
		this.setTexture(3, "textures/entity/cat/siamese");
	}

	public void renderLivingOcelot(EntityOcelot par1EntityOcelot, double par2, double par4, double par6, float par8, float par9) {
		super.doRenderLiving((EntityLiving)par1EntityOcelot, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation func_110874_a(EntityOcelot par1EntityOcelot) {
		switch (par1EntityOcelot.getTameSkin()) {
			case 0:
			default:
				return this.textures[0];
			case 1:
				return this.textures[1];
			case 2:
				return this.textures[2];
			case 3:
				return this.textures[3];
		}
	}

	protected void preRenderOcelot(EntityOcelot par1EntityOcelot, float par2) {
		super.preRenderCallback(par1EntityOcelot, par2);
		if (par1EntityOcelot.isTamed()) {
			GL11.glScalef(0.8F, 0.8F, 0.8F);
		}

	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.renderLivingOcelot((EntityOcelot)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		this.preRenderOcelot((EntityOcelot)par1EntityLivingBase, par2);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.renderLivingOcelot((EntityOcelot)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110874_a((EntityOcelot)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.renderLivingOcelot((EntityOcelot)par1Entity, par2, par4, par6, par8, par9);
	}
}
