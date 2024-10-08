package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderIronGolem extends RenderLiving {
	public static final int body_texture = 0;
	private final ModelIronGolem ironGolemModel;

	public RenderIronGolem() {
		super(new ModelIronGolem(), 0.5F);
		this.ironGolemModel = (ModelIronGolem)this.mainModel;
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/iron_golem");
	}

	public void doRenderIronGolem(EntityIronGolem par1EntityIronGolem, double par2, double par4, double par6, float par8, float par9) {
		super.doRenderLiving((EntityLiving)par1EntityIronGolem, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getIronGolemTextures(EntityIronGolem par1EntityIronGolem) {
		return this.textures[0];
	}

	protected void rotateIronGolemCorpse(EntityIronGolem par1EntityIronGolem, float par2, float par3, float par4) {
		super.rotateCorpse(par1EntityIronGolem, par2, par3, par4);
		if ((double)par1EntityIronGolem.limbSwingAmount >= 0.01) {
			float var5 = 13.0F;
			float var6 = par1EntityIronGolem.limbSwing - par1EntityIronGolem.limbSwingAmount * (1.0F - par4) + 6.0F;
			float var7 = (Math.abs(var6 % var5 - var5 * 0.5F) - var5 * 0.25F) / (var5 * 0.25F);
			GL11.glRotatef(6.5F * var7, 0.0F, 0.0F, 1.0F);
		}

	}

	protected void renderIronGolemEquippedItems(EntityIronGolem par1EntityIronGolem, float par2) {
		super.renderEquippedItems(par1EntityIronGolem, par2);
		if (par1EntityIronGolem.getHoldRoseTick() != 0) {
			GL11.glEnable(32826);
			GL11.glPushMatrix();
			GL11.glRotatef(5.0F + 180.0F * this.ironGolemModel.ironGolemRightArm.rotateAngleX / 3.1415927F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(-0.6875F, 1.25F, -0.9375F);
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			float var3 = 0.8F;
			GL11.glScalef(var3, -var3, var3);
			int var4 = par1EntityIronGolem.getBrightnessForRender(par2);
			int var5 = var4 % 65536;
			int var6 = var4 / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var5 / 1.0F, (float)var6 / 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindTexture(TextureMap.locationBlocksTexture);
			this.renderBlocks.renderBlockAsItem(Block.plantRed, 0, 1.0F);
			GL11.glPopMatrix();
			GL11.glDisable(32826);
		}

	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderIronGolem((EntityIronGolem)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
		this.renderIronGolemEquippedItems((EntityIronGolem)par1EntityLivingBase, par2);
	}

	protected void rotateCorpse(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4) {
		this.rotateIronGolemCorpse((EntityIronGolem)par1EntityLivingBase, par2, par3, par4);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderIronGolem((EntityIronGolem)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getIronGolemTextures((EntityIronGolem)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderIronGolem((EntityIronGolem)par1Entity, par2, par4, par6, par8, par9);
	}
}
