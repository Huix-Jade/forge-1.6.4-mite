package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderFish extends Render {
	private static final ResourceLocation field_110792_a = new ResourceLocation("textures/particle/particles.png");

	public void doRenderFishHook(EntityFishHook par1EntityFishHook, double par2, double par4, double par6, float par8, float par9) {
		if (par1EntityFishHook.ticksExisted >= 3) {
			if (par1EntityFishHook.angler != Minecraft.getClientPlayer()) {
				par4 -= 0.20000000298023224;
			}

			boolean first_person_rendering = this.renderManager.options.thirdPersonView == 0 && par1EntityFishHook.angler == Minecraft.getClientPlayer();
			if (!first_person_rendering) {
				par4 -= 0.20000000298023224;
			}

			GL11.glPushMatrix();
			GL11.glTranslatef((float)par2, (float)par4, (float)par6);
			GL11.glEnable(32826);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			this.bindEntityTexture(par1EntityFishHook);
			Tessellator var10 = Tessellator.instance;
			byte var11 = 1;
			byte var12 = 2;
			float var13 = (float)(var11 * 8 + 0) / 128.0F;
			float var14 = (float)(var11 * 8 + 8) / 128.0F;
			float var15 = (float)(var12 * 8 + 0) / 128.0F;
			float var16 = (float)(var12 * 8 + 8) / 128.0F;
			var13 += 1.0E-5F;
			float var17 = 1.0F;
			float var18 = 0.5F;
			float var19 = 0.5F;
			GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(-0.06F, 0.0F, 0.0F);
			var10.startDrawingQuads();
			var10.setNormal(0.0F, 1.0F, 0.0F);
			var10.addVertexWithUV((double)(0.0F - var18), (double)(0.0F - var19), 0.0, (double)var13, (double)var16);
			var10.addVertexWithUV((double)(var17 - var18), (double)(0.0F - var19), 0.0, (double)var14, (double)var16);
			var10.addVertexWithUV((double)(var17 - var18), (double)(1.0F - var19), 0.0, (double)var14, (double)var15);
			var10.addVertexWithUV((double)(0.0F - var18), (double)(1.0F - var19), 0.0, (double)var13, (double)var15);
			var10.draw();
			GL11.glDisable(32826);
			GL11.glPopMatrix();
			if (par1EntityFishHook.angler != null) {
				float var20 = 0.0F;
				float var21 = MathHelper.sin(MathHelper.sqrt_float(var20) * 3.1415927F);
				Vec3 var22 = par1EntityFishHook.worldObj.getWorldVec3Pool().getVecFromPool(-0.5, 0.03, 0.8);
				var22.rotateAroundX(-(par1EntityFishHook.angler.prevRotationPitch + (par1EntityFishHook.angler.rotationPitch - par1EntityFishHook.angler.prevRotationPitch) * par9) * 3.1415927F / 180.0F);
				var22.rotateAroundY(-(par1EntityFishHook.angler.prevRotationYaw + (par1EntityFishHook.angler.rotationYaw - par1EntityFishHook.angler.prevRotationYaw) * par9) * 3.1415927F / 180.0F);
				var22.rotateAroundY(var21 * 0.5F);
				var22.rotateAroundX(-var21 * 0.7F);
				double var23 = par1EntityFishHook.angler.prevPosX + (par1EntityFishHook.angler.posX - par1EntityFishHook.angler.prevPosX) * (double)par9 + var22.xCoord;
				double var25 = par1EntityFishHook.angler.prevPosY + (par1EntityFishHook.angler.posY - par1EntityFishHook.angler.prevPosY) * (double)par9 + var22.yCoord;
				double var27 = par1EntityFishHook.angler.prevPosZ + (par1EntityFishHook.angler.posZ - par1EntityFishHook.angler.prevPosZ) * (double)par9 + var22.zCoord;
				double var29 = par1EntityFishHook.angler == Minecraft.getMinecraft().thePlayer ? 0.0 : (double)par1EntityFishHook.angler.getEyeHeight();
				if (!first_person_rendering) {
					float var31 = (par1EntityFishHook.angler.prevRenderYawOffset + (par1EntityFishHook.angler.renderYawOffset - par1EntityFishHook.angler.prevRenderYawOffset) * par9) * 3.1415927F / 180.0F;
					double var32 = (double)MathHelper.sin(var31);
					double var34 = (double)MathHelper.cos(var31);
					var23 = par1EntityFishHook.angler.prevPosX + (par1EntityFishHook.angler.posX - par1EntityFishHook.angler.prevPosX) * (double)par9 - var34 * 0.35 - var32 * 0.85;
					var25 = par1EntityFishHook.angler.prevPosY + var29 + (par1EntityFishHook.angler.posY - par1EntityFishHook.angler.prevPosY) * (double)par9 - 0.45;
					var27 = par1EntityFishHook.angler.prevPosZ + (par1EntityFishHook.angler.posZ - par1EntityFishHook.angler.prevPosZ) * (double)par9 - var32 * 0.35 + var34 * 0.85;
				}

				double var46 = par1EntityFishHook.prevPosX + (par1EntityFishHook.posX - par1EntityFishHook.prevPosX) * (double)par9;
				double var33 = par1EntityFishHook.prevPosY + (par1EntityFishHook.posY - par1EntityFishHook.prevPosY) * (double)par9 + 0.25;
				double var35 = par1EntityFishHook.prevPosZ + (par1EntityFishHook.posZ - par1EntityFishHook.prevPosZ) * (double)par9;
				double var37 = (double)((float)(var23 - var46));
				double var39 = (double)((float)(var25 - var33));
				double var41 = (double)((float)(var27 - var35));
				GL11.glDisable(3553);
				GL11.glDisable(2896);
				var10.startDrawing(3);
				var10.setColorOpaque_I(0);
				byte var43 = 16;

				for(int var44 = 0; var44 <= var43; ++var44) {
					float var45 = (float)var44 / (float)var43;
					var10.addVertex(par2 + var37 * (double)var45, par4 + var39 * (double)(var45 * var45 + var45) * 0.5 + 0.25 - (double)(0.05F * (1.0F - var45)), par6 + var41 * (double)var45);
				}

				var10.draw();
				GL11.glEnable(2896);
				GL11.glEnable(3553);
			}

		}
	}

	protected ResourceLocation func_110791_a(EntityFishHook par1EntityFishHook) {
		return field_110792_a;
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110791_a((EntityFishHook)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderFishHook((EntityFishHook)par1Entity, par2, par4, par6, par8, par9);
	}
}
