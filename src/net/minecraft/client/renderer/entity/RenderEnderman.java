package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEnderman extends RenderLiving {
	public static final int body_texture = 0;
	private ModelEnderman endermanModel;
	private Random rnd = new Random();

	public RenderEnderman() {
		super(new ModelEnderman(), 0.5F);
		this.endermanModel = (ModelEnderman)super.mainModel;
		this.setRenderPassModel(this.endermanModel);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/enderman/enderman");
	}

	public void renderEnderman(EntityEnderman par1EntityEnderman, double par2, double par4, double par6, float par8, float par9) {
		this.endermanModel.isCarrying = par1EntityEnderman.getCarried() > 0;
		this.endermanModel.isAttacking = par1EntityEnderman.isScreaming();
		if (par1EntityEnderman.isScreaming()) {
			double var10 = 0.02;
			par2 += this.rnd.nextGaussian() * var10;
			par6 += this.rnd.nextGaussian() * var10;
		}

		super.doRenderLiving((EntityLiving)par1EntityEnderman, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEndermanTextures(EntityEnderman par1EntityEnderman) {
		return this.textures[0];
	}

	protected void renderCarrying(EntityEnderman par1EntityEnderman, float par2) {
		super.renderEquippedItems(par1EntityEnderman, par2);
		if (par1EntityEnderman.getCarried() > 0) {
			GL11.glEnable(32826);
			GL11.glPushMatrix();
			float var3 = 0.5F;
			GL11.glTranslatef(0.0F, 0.6875F, -0.75F);
			var3 *= 1.0F;
			GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(-var3, -var3, var3);
			int var4 = par1EntityEnderman.getBrightnessForRender(par2);
			int var5 = var4 % 65536;
			int var6 = var4 / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var5 / 1.0F, (float)var6 / 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindTexture(TextureMap.locationBlocksTexture);
			this.renderBlocks.renderBlockAsItem(Block.blocksList[par1EntityEnderman.getCarried()], par1EntityEnderman.getCarryingData(), 1.0F);
			GL11.glPopMatrix();
			GL11.glDisable(32826);
		}

	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.renderEnderman((EntityEnderman)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
		this.renderCarrying((EntityEnderman)par1EntityLivingBase, par2);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.renderEnderman((EntityEnderman)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getEndermanTextures((EntityEnderman)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.renderEnderman((EntityEnderman)par1Entity, par2, par4, par6, par8, par9);
	}
}
