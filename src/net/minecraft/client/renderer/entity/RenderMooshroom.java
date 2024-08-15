package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderMooshroom extends RenderLiving {
	public static final int body_texture = 0;

	public RenderMooshroom(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/cow/mooshroom");
	}

	public void renderLivingMooshroom(EntityMooshroom par1EntityMooshroom, double par2, double par4, double par6, float par8, float par9) {
		super.doRenderLiving((EntityLiving)par1EntityMooshroom, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getMooshroomTextures(EntityMooshroom par1EntityMooshroom) {
		return this.textures[0];
	}

	protected void renderMooshroomEquippedItems(EntityMooshroom par1EntityMooshroom, float par2) {
		super.renderEquippedItems(par1EntityMooshroom, par2);
		if (!par1EntityMooshroom.isChild()) {
			this.bindTexture(TextureMap.locationBlocksTexture);
			GL11.glEnable(2884);
			GL11.glPushMatrix();
			GL11.glScalef(1.0F, -1.0F, 1.0F);
			GL11.glTranslatef(0.2F, 0.4F, 0.5F);
			GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
			this.renderBlocks.renderBlockAsItem(Block.mushroomRed, 0, 1.0F);
			GL11.glTranslatef(0.1F, 0.0F, -0.6F);
			GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
			this.renderBlocks.renderBlockAsItem(Block.mushroomRed, 0, 1.0F);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			((ModelQuadruped)this.mainModel).head.postRender(0.0625F);
			GL11.glScalef(1.0F, -1.0F, 1.0F);
			GL11.glTranslatef(0.0F, 0.75F, -0.2F);
			GL11.glRotatef(12.0F, 0.0F, 1.0F, 0.0F);
			this.renderBlocks.renderBlockAsItem(Block.mushroomRed, 0, 1.0F);
			GL11.glPopMatrix();
			GL11.glDisable(2884);
		}

	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.renderLivingMooshroom((EntityMooshroom)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
		this.renderMooshroomEquippedItems((EntityMooshroom)par1EntityLivingBase, par2);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.renderLivingMooshroom((EntityMooshroom)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getMooshroomTextures((EntityMooshroom)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.renderLivingMooshroom((EntityMooshroom)par1Entity, par2, par4, par6, par8, par9);
	}
}
