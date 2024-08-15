package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderChicken extends RenderLiving {
	public static final int body_texture = 0;
	public static final int body_texture_sick = 1;

	public RenderChicken(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/chicken");
		this.setTexture(1, "textures/entity/chicken/sick");
	}

	public void renderChicken(EntityChicken par1EntityChicken, double par2, double par4, double par6, float par8, float par9) {
		super.doRenderLiving((EntityLiving)par1EntityChicken, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getChickenTextures(EntityChicken par1EntityChicken) {
		return par1EntityChicken.isWell() ? this.textures[0] : this.textures[1];
	}

	protected float getWingRotation(EntityChicken par1EntityChicken, float par2) {
		float var3 = par1EntityChicken.field_70888_h + (par1EntityChicken.field_70886_e - par1EntityChicken.field_70888_h) * par2;
		float var4 = par1EntityChicken.field_70884_g + (par1EntityChicken.destPos - par1EntityChicken.field_70884_g) * par2;
		return (MathHelper.sin(var3) + 1.0F) * var4;
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.renderChicken((EntityChicken)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	protected float handleRotationFloat(EntityLivingBase par1EntityLivingBase, float par2) {
		return this.getWingRotation((EntityChicken)par1EntityLivingBase, par2);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.renderChicken((EntityChicken)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getChickenTextures((EntityChicken)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.renderChicken((EntityChicken)par1Entity, par2, par4, par6, par8, par9);
	}
}
