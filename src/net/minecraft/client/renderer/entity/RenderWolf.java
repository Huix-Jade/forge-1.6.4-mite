package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWolf extends RenderLiving {
	public static final int neutral_texture = 0;
	public static final int tamed_texture = 1;
	public static final int angry_texture = 2;
	public static final int collar_texture = 3;

	public RenderWolf(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3) {
		super(par1ModelBase, par3);
		this.setRenderPassModel(par2ModelBase);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/wolf/wolf");
		this.setTexture(1, "textures/entity/wolf/wolf_tame");
		this.setTexture(2, "textures/entity/wolf/wolf_angry");
		this.setTexture(3, "textures/entity/wolf/wolf_collar");
	}

	protected float getTailRotation(EntityWolf par1EntityWolf, float par2) {
		return par1EntityWolf.getTailRotation();
	}

	protected int func_82447_a(EntityWolf par1EntityWolf, int par2, float par3) {
		float var4;
		if (par2 == 0 && par1EntityWolf.getWolfShaking()) {
			var4 = par1EntityWolf.getBrightness(par3) * par1EntityWolf.getShadingWhileShaking(par3);
			this.bindTexture(this.func_110914_a(par1EntityWolf));
			GL11.glColor3f(var4, var4, var4);
			return 1;
		} else if (par2 == 1 && par1EntityWolf.isTamed()) {
			this.bindTexture(this.textures[3]);
			var4 = 1.0F;
			int var5 = par1EntityWolf.getCollarColor();
			GL11.glColor3f(var4 * EntitySheep.fleeceColorTable[var5][0], var4 * EntitySheep.fleeceColorTable[var5][1], var4 * EntitySheep.fleeceColorTable[var5][2]);
			return 1;
		} else {
			return -1;
		}
	}

	protected ResourceLocation func_110914_a(EntityWolf par1EntityWolf) {
		return par1EntityWolf.isTamed() ? this.textures[1] : (par1EntityWolf.looksAngry() ? this.textures[2] : this.textures[0]);
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.func_82447_a((EntityWolf)par1EntityLivingBase, par2, par3);
	}

	protected float handleRotationFloat(EntityLivingBase par1EntityLivingBase, float par2) {
		return this.getTailRotation((EntityWolf)par1EntityLivingBase, par2);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110914_a((EntityWolf)par1Entity);
	}
}
