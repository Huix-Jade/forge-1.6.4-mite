package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

public class RenderSilverfish extends RenderLiving {
	public static final int body_texture = 0;
	public static final int texture_netherspawn = 1;
	public static final int texture_copperspine = 2;
	public static final int texture_hoary_silverfish = 3;

	public RenderSilverfish() {
		super(new ModelSilverfish(), 0.3F);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/silverfish/silverfish");
		this.setTexture(1, "textures/entity/silverfish/netherspawn");
		this.setTexture(2, "textures/entity/silverfish/copperspine");
		this.setTexture(3, "textures/entity/silverfish/hoary");
	}

	protected float getSilverfishDeathRotation(EntitySilverfish par1EntitySilverfish) {
		return 180.0F;
	}

	public void renderSilverfish(EntitySilverfish par1EntitySilverfish, double par2, double par4, double par6, float par8, float par9) {
		super.doRenderLiving((EntityLiving)par1EntitySilverfish, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getSilverfishTextures(EntitySilverfish par1EntitySilverfish) {
		return this.textures[par1EntitySilverfish.isNetherspawn() ? 1 : (par1EntitySilverfish.isCopperspine() ? 2 : (par1EntitySilverfish.isHoarySilverfish() ? 3 : 0))];
	}

	protected int shouldSilverfishRenderPass(EntitySilverfish par1EntitySilverfish, int par2, float par3) {
		return -1;
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.renderSilverfish((EntitySilverfish)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	protected float getDeathMaxRotation(EntityLivingBase par1EntityLivingBase) {
		return this.getSilverfishDeathRotation((EntitySilverfish)par1EntityLivingBase);
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.shouldSilverfishRenderPass((EntitySilverfish)par1EntityLivingBase, par2, par3);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.renderSilverfish((EntitySilverfish)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getSilverfishTextures((EntitySilverfish)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.renderSilverfish((EntitySilverfish)par1Entity, par2, par4, par6, par8, par9);
	}
}
