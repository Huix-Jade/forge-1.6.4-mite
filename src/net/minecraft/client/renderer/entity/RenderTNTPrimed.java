package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderTNTPrimed extends Render {
	private RenderBlocks blockRenderer = new RenderBlocks();

	public RenderTNTPrimed() {
		this.shadowSize = 0.5F;
	}

	public void renderPrimedTNT(EntityTNTPrimed par1EntityTNTPrimed, double par2, double par4, double par6, float par8, float par9) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)par2, (float)par4, (float)par6);
		float var10;
		if ((float)par1EntityTNTPrimed.fuse - par9 + 1.0F < 10.0F) {
			var10 = 1.0F - ((float)par1EntityTNTPrimed.fuse - par9 + 1.0F) / 10.0F;
			if (var10 < 0.0F) {
				var10 = 0.0F;
			}

			if (var10 > 1.0F) {
				var10 = 1.0F;
			}

			var10 *= var10;
			var10 *= var10;
			float var11 = 1.0F + var10 * 0.3F;
			GL11.glScalef(var11, var11, var11);
		}

		var10 = (1.0F - ((float)par1EntityTNTPrimed.fuse - par9 + 1.0F) / 100.0F) * 0.8F;
		this.bindEntityTexture(par1EntityTNTPrimed);
		this.blockRenderer.renderBlockAsItem(Block.tnt, 0, par1EntityTNTPrimed.getBrightness(par9));
		if (par1EntityTNTPrimed.fuse / 5 % 2 == 0) {
			GL11.glDisable(3553);
			GL11.glDisable(2896);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 772);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, var10);
			this.blockRenderer.renderBlockAsItem(Block.tnt, 0, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(3042);
			GL11.glEnable(2896);
			GL11.glEnable(3553);
		}

		GL11.glPopMatrix();
	}

	protected ResourceLocation func_110808_a(EntityTNTPrimed par1EntityTNTPrimed) {
		return TextureMap.locationBlocksTexture;
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110808_a((EntityTNTPrimed)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.renderPrimedTNT((EntityTNTPrimed)par1Entity, par2, par4, par6, par8, par9);
	}
}
