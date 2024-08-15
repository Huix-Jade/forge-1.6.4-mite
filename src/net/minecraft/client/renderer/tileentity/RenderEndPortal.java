package net.minecraft.client.renderer.tileentity;

import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEndPortal extends TileEntitySpecialRenderer {
	private static final ResourceLocation enderPortalEndSkyTextures = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation endPortalTextures = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random field_110644_e = new Random(31100L);
	FloatBuffer field_76908_a = GLAllocation.createDirectFloatBuffer(16);


	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderEndPortalTileEntity((TileEntityEndPortal)par1TileEntity, par2, par4, par6, par8);
	}

	public void renderEndPortalTileEntity(TileEntityEndPortal tileEntityEndPortal, double d, double e, double f, float g) {
		float var9 = (float)this.tileEntityRenderer.playerX;
		float var10 = (float)this.tileEntityRenderer.playerY;
		float var11 = (float)this.tileEntityRenderer.playerZ;
		GL11.glDisable(2896);
		field_110644_e.setSeed(31100L);
		float var12 = 0.75F;

		for(int var13 = 0; var13 < 16; ++var13) {
			GL11.glPushMatrix();
			float var14 = (float)(16 - var13);
			float var15 = 0.0625F;
			float var16 = 1.0F / (var14 + 1.0F);
			if (var13 == 0) {
				this.bindTexture(enderPortalEndSkyTextures);
				var16 = 0.1F;
				var14 = 65.0F;
				var15 = 0.125F;
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
			}

			if (var13 == 1) {
				this.bindTexture(endPortalTextures);
				GL11.glEnable(3042);
				GL11.glBlendFunc(1, 1);
				var15 = 0.5F;
			}

			float var17 = (float)(-(e + (double)var12));
			float var18 = var17 + ActiveRenderInfo.objectY;
			float var19 = var17 + var14 + ActiveRenderInfo.objectY;
			float var20 = var18 / var19;
			var20 += (float)(e + (double)var12);
			GL11.glTranslatef(var9, var20, var11);
			GL11.glTexGeni(8192, 9472, 9217);
			GL11.glTexGeni(8193, 9472, 9217);
			GL11.glTexGeni(8194, 9472, 9217);
			GL11.glTexGeni(8195, 9472, 9216);
			GL11.glTexGen(8192, 9473, (FloatBuffer)this.func_76907_a(1.0F, 0.0F, 0.0F, 0.0F));
			GL11.glTexGen(8193, 9473, (FloatBuffer)this.func_76907_a(0.0F, 0.0F, 1.0F, 0.0F));
			GL11.glTexGen(8194, 9473, (FloatBuffer)this.func_76907_a(0.0F, 0.0F, 0.0F, 1.0F));
			GL11.glTexGen(8195, 9474, (FloatBuffer)this.func_76907_a(0.0F, 1.0F, 0.0F, 0.0F));
			GL11.glEnable(3168);
			GL11.glEnable(3169);
			GL11.glEnable(3170);
			GL11.glEnable(3171);
			GL11.glPopMatrix();
			GL11.glMatrixMode(5890);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
			GL11.glScalef(var15, var15, var15);
			GL11.glTranslatef(0.5F, 0.5F, 0.0F);
			GL11.glRotatef((float)(var13 * var13 * 4321 + var13 * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
			GL11.glTranslatef(-var9, -var11, -var10);
			var18 = var17 + ActiveRenderInfo.objectY;
			GL11.glTranslatef(ActiveRenderInfo.objectX * var14 / var18, ActiveRenderInfo.objectZ * var14 / var18, -var10);
			Tessellator var23 = Tessellator.instance;
			var23.startDrawingQuads();
			var20 = field_110644_e.nextFloat() * 0.5F + 0.1F;
			float var21 = field_110644_e.nextFloat() * 0.5F + 0.4F;
			float var22 = field_110644_e.nextFloat() * 0.5F + 0.5F;
			if (var13 == 0) {
				var22 = 1.0F;
				var21 = 1.0F;
				var20 = 1.0F;
			}

			var23.setColorRGBA_F(var20 * var16, var21 * var16, var22 * var16, 1.0F);
			var23.addVertex(d, e + (double)var12, f);
			var23.addVertex(d, e + (double)var12, f + 1.0);
			var23.addVertex(d + 1.0, e + (double)var12, f + 1.0);
			var23.addVertex(d + 1.0, e + (double)var12, f);
			var23.draw();
			GL11.glPopMatrix();
			GL11.glMatrixMode(5888);
		}

		GL11.glDisable(3042);
		GL11.glDisable(3168);
		GL11.glDisable(3169);
		GL11.glDisable(3170);
		GL11.glDisable(3171);
		GL11.glEnable(2896);
	}

	private FloatBuffer func_76907_a(float f, float g, float h, float i) {
		this.field_76908_a.clear();
		this.field_76908_a.put(f).put(g).put(h).put(i);
		this.field_76908_a.flip();
		return this.field_76908_a;
	}
}
