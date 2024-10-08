package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBat extends RenderLiving {
	public static final int texture_normal = 0;
	public static final int texture_vampire = 1;
	public static final int texture_nightwing = 2;
	private int renderedBatSize;

	public RenderBat() {
		super(new ModelBat(), 0.25F);
		this.renderedBatSize = ((ModelBat)this.mainModel).getBatSize();
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/bat");
		this.setTexture(1, "textures/entity/bat/vampire");
		this.setTexture(2, "textures/entity/bat/nightwing");
	}

	public void func_82443_a(EntityBat par1EntityBat, double par2, double par4, double par6, float par8, float par9) {
		int var10 = ((ModelBat)this.mainModel).getBatSize();
		if (var10 != this.renderedBatSize) {
			this.renderedBatSize = var10;
			this.mainModel = new ModelBat();
		}

		super.doRenderLiving((EntityLiving)par1EntityBat, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getBatTextures(EntityBat par1EntityBat) {
		return this.textures[par1EntityBat.isVampireBat() ? 1 : (par1EntityBat.isNightwing() ? 2 : 0)];
	}

	protected void func_82442_a(EntityBat par1EntityBat, float par2) {
		float scale = 0.35F * par1EntityBat.getScaleFactor();
		GL11.glScalef(scale, scale, scale);
	}

	protected void func_82445_a(EntityBat par1EntityBat, double par2, double par4, double par6) {
		super.renderLivingAt(par1EntityBat, par2, par4, par6);
	}

	protected void func_82444_a(EntityBat par1EntityBat, float par2, float par3, float par4) {
		if (!par1EntityBat.getIsBatHanging()) {
			GL11.glTranslatef(0.0F, MathHelper.cos(par2 * 0.3F) * 0.1F, 0.0F);
		} else {
			GL11.glTranslatef(0.0F, -0.1F, 0.0F);
		}

		super.rotateCorpse(par1EntityBat, par2, par3, par4);
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.func_82443_a((EntityBat)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		this.func_82442_a((EntityBat)par1EntityLivingBase, par2);
	}

	protected void rotateCorpse(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4) {
		this.func_82444_a((EntityBat)par1EntityLivingBase, par2, par3, par4);
	}

	protected void renderLivingAt(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6) {
		this.func_82445_a((EntityBat)par1EntityLivingBase, par2, par4, par6);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.func_82443_a((EntityBat)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getBatTextures((EntityBat)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.func_82443_a((EntityBat)par1Entity, par2, par4, par6, par8, par9);
	}
}
