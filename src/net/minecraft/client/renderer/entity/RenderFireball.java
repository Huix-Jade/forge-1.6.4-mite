package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderFireball extends Render {
	private float field_77002_a;

	public RenderFireball(float par1) {
		this.field_77002_a = par1;
	}

	public void doRenderFireball(EntityFireball par1EntityFireball, double par2, double par4, double par6, float par8, float par9) {
		GL11.glPushMatrix();
		this.bindEntityTexture(par1EntityFireball);
		GL11.glTranslatef((float)par2, (float)par4, (float)par6);
		GL11.glEnable(32826);
		float var10 = this.field_77002_a;
		GL11.glScalef(var10 / 1.0F, var10 / 1.0F, var10 / 1.0F);
		Icon var11 = Item.fireballCharge.getIconFromSubtype(0);
		Tessellator var12 = Tessellator.instance;
		float var13 = var11.getMinU();
		float var14 = var11.getMaxU();
		float var15 = var11.getMinV();
		float var16 = var11.getMaxV();
		float var17 = 1.0F;
		float var18 = 0.5F;
		float var19 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		var12.startDrawingQuads();
		var12.setNormal(0.0F, 1.0F, 0.0F);
		var12.addVertexWithUV((double)(0.0F - var18), (double)(0.0F - var19), 0.0, (double)var13, (double)var16);
		var12.addVertexWithUV((double)(var17 - var18), (double)(0.0F - var19), 0.0, (double)var14, (double)var16);
		var12.addVertexWithUV((double)(var17 - var18), (double)(1.0F - var19), 0.0, (double)var14, (double)var15);
		var12.addVertexWithUV((double)(0.0F - var18), (double)(1.0F - var19), 0.0, (double)var13, (double)var15);
		var12.draw();
		GL11.glDisable(32826);
		GL11.glPopMatrix();
	}

	protected ResourceLocation getFireballTextures(EntityFireball par1EntityFireball) {
		return TextureMap.locationItemsTexture;
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getFireballTextures((EntityFireball)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderFireball((EntityFireball)par1Entity, par2, par4, par6, par8, par9);
	}
}
