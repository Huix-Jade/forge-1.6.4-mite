package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;

public class RenderPig extends RenderLiving {
	public static final int body_texture = 0;
	public static final int body_texture_sick = 1;
	public static final int saddle_texture = 2;

	public RenderPig(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3) {
		super(par1ModelBase, par3);
		this.setRenderPassModel(par2ModelBase);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/pig/pig");
		this.setTexture(1, "textures/entity/pig/sick");
		this.setTexture(2, "textures/entity/pig/pig_saddle");
	}

	protected int renderSaddledPig(EntityPig par1EntityPig, int par2, float par3) {
		if (par2 == 0 && par1EntityPig.getSaddled()) {
			this.bindTexture(this.textures[2]);
			return 1;
		} else {
			return -1;
		}
	}

	protected ResourceLocation getPigTextures(EntityPig par1EntityPig) {
		return par1EntityPig.isWell() ? this.textures[0] : this.textures[1];
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.renderSaddledPig((EntityPig)par1EntityLivingBase, par2, par3);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getPigTextures((EntityPig)par1Entity);
	}
}
