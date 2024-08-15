package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderMinecart extends Render {
	private static final ResourceLocation minecartTextures = new ResourceLocation("textures/entity/minecart.png");
	protected ModelBase modelMinecart = new ModelMinecart();
	protected final RenderBlocks field_94145_f;

	public RenderMinecart() {
		this.shadowSize = 0.5F;
		this.field_94145_f = new RenderBlocks();
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity)
	{
		return this.getMinecartTextures((EntityMinecart)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.renderTheMinecart((EntityMinecart)par1Entity, par2, par4, par6, par8, par9);
	}

	public void renderTheMinecart(EntityMinecart entityMinecart, double d, double e, double f, float g, float h) {
		GL11.glPushMatrix();
		this.bindEntityTexture(entityMinecart);
		long var10 = (long)entityMinecart.entityId * 493286711L;
		var10 = var10 * var10 * 4392167121L + var10 * 98761L;
		float var12 = (((float)(var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float var13 = (((float)(var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float var14 = (((float)(var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		GL11.glTranslatef(var12, var13, var14);
		double var15 = entityMinecart.lastTickPosX + (entityMinecart.posX - entityMinecart.lastTickPosX) * (double)h;
		double var17 = entityMinecart.lastTickPosY + (entityMinecart.posY - entityMinecart.lastTickPosY) * (double)h;
		double var19 = entityMinecart.lastTickPosZ + (entityMinecart.posZ - entityMinecart.lastTickPosZ) * (double)h;
		double var21 = 0.30000001192092896;
		Vec3 var23 = entityMinecart.func_70489_a(var15, var17, var19);
		float var24 = entityMinecart.prevRotationPitch + (entityMinecart.rotationPitch - entityMinecart.prevRotationPitch) * h;
		if (var23 != null) {
			Vec3 var25 = entityMinecart.func_70495_a(var15, var17, var19, var21);
			Vec3 var26 = entityMinecart.func_70495_a(var15, var17, var19, -var21);
			if (var25 == null) {
				var25 = var23;
			}

			if (var26 == null) {
				var26 = var23;
			}

			d += var23.xCoord - var15;
			e += (var25.yCoord + var26.yCoord) / 2.0 - var17;
			f += var23.zCoord - var19;
			Vec3 var27 = var26.addVector(-var25.xCoord, -var25.yCoord, -var25.zCoord);
			if (var27.lengthVector() != 0.0) {
				var27 = var27.normalize();
				g = (float)(Math.atan2(var27.zCoord, var27.xCoord) * 180.0 / Math.PI);
				var24 = (float)(Math.atan(var27.yCoord) * 73.0);
			}
		}

		GL11.glTranslatef((float)d, (float)e, (float)f);
		GL11.glRotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-var24, 0.0F, 0.0F, 1.0F);
		float var31 = (float)entityMinecart.getRollingAmplitude() - h;
		float var32 = entityMinecart.getDamage() - h;
		if (var32 < 0.0F) {
			var32 = 0.0F;
		}

		if (var31 > 0.0F) {
			GL11.glRotatef(MathHelper.sin(var31) * var31 * var32 / 10.0F * (float)entityMinecart.getRollingDirection(), 1.0F, 0.0F, 0.0F);
		}

		int var33 = entityMinecart.getDisplayTileOffset();
		Block var28 = entityMinecart.getDisplayTile();
		int var29 = entityMinecart.getDisplayTileData();
		if (var28 != null) {
			GL11.glPushMatrix();
			this.bindTexture(TextureMap.locationBlocksTexture);
			float var30 = 0.75F;
			GL11.glScalef(var30, var30, var30);
			GL11.glTranslatef(0.0F, (float)var33 / 16.0F, 0.0F);
			this.renderBlockInMinecart(entityMinecart, h, var28, var29);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindEntityTexture(entityMinecart);
		}

		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		this.modelMinecart.render(entityMinecart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}

	protected ResourceLocation getMinecartTextures(EntityMinecart entityMinecart) {
		return minecartTextures;
	}

	protected void renderBlockInMinecart(EntityMinecart entityMinecart, float f, Block block, int i) {
		float var5 = entityMinecart.getBrightness(f);
		GL11.glPushMatrix();
		this.field_94145_f.renderBlockAsItem(block, i, var5);
		GL11.glPopMatrix();
	}
}
