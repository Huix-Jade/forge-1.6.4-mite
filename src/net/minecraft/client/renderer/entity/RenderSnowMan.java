package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.client.IItemRenderer;
import static net.minecraftforge.client.IItemRenderer.ItemRenderType.*;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.*;
import net.minecraftforge.client.MinecraftForgeClient;

public class RenderSnowMan extends RenderLiving {
	public static final int body_texture = 0;
	private ModelSnowMan snowmanModel;

	public RenderSnowMan() {
		super(new ModelSnowMan(), 0.5F);
		this.snowmanModel = (ModelSnowMan)super.mainModel;
		this.setRenderPassModel(this.snowmanModel);
	}

	protected void setTextures() {
		this.setTexture(0, "textures/entity/snowman");
	}

	protected void renderSnowmanPumpkin(EntitySnowman par1EntitySnowman, float par2) {
		super.renderEquippedItems(par1EntitySnowman, par2);
		ItemStack var3 = new ItemStack(Block.pumpkin, 1);
		if (var3 != null && var3.getItem() instanceof ItemBlock) {
			GL11.glPushMatrix();
			this.snowmanModel.head.postRender(0.0625F);
			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var3, EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, var3, BLOCK_3D));

			if (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[var3.itemID].getRenderType())) {
				float var4 = 0.625F;
				GL11.glTranslatef(0.0F, -0.34375F, 0.0F);
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(var4, -var4, var4);
			}

			this.renderManager.itemRenderer.renderItem(par1EntitySnowman, var3, 0);
			GL11.glPopMatrix();
		}

	}

	protected ResourceLocation getSnowManTextures(EntitySnowman par1EntitySnowman) {
		return this.textures[0];
	}

	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
		this.renderSnowmanPumpkin((EntitySnowman)par1EntityLivingBase, par2);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getSnowManTextures((EntitySnowman)par1Entity);
	}
}
