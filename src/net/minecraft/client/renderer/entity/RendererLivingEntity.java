package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

public abstract class RendererLivingEntity extends Render {
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	protected ModelBase mainModel;
	protected ModelBase renderPassModel;

	public static float NAME_TAG_RANGE = 64.0f;
	public static float NAME_TAG_RANGE_SNEAK = 32.0f;

	public RendererLivingEntity(ModelBase par1ModelBase, float par2) {
		this.mainModel = par1ModelBase;
		this.shadowSize = par2;
	}

	public void setRenderPassModel(ModelBase par1ModelBase) {
		this.renderPassModel = par1ModelBase;
	}

	private float interpolateRotation(float par1, float par2, float par3) {
		float var4;
		for(var4 = par2 - par1; var4 < -180.0F; var4 += 360.0F) {
		}

		while(var4 >= 180.0F) {
			var4 -= 360.0F;
		}

		return par1 + par3 * var4;
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(par1EntityLivingBase, this))) return;
		GL11.glPushMatrix();
		if (par1EntityLivingBase.drawBackFaces()) {
			GL11.glDisable(2884);
		} else {
			GL11.glEnable(2884);
		}

		this.mainModel.onGround = this.renderSwingProgress(par1EntityLivingBase, par9);
		if (this.renderPassModel != null) {
			this.renderPassModel.onGround = this.mainModel.onGround;
		}

		this.mainModel.isRiding = par1EntityLivingBase.isRiding();
		if (this.renderPassModel != null) {
			this.renderPassModel.isRiding = this.mainModel.isRiding;
		}

		this.mainModel.isChild = par1EntityLivingBase.isChild();
		if (this.renderPassModel != null) {
			this.renderPassModel.isChild = this.mainModel.isChild;
		}

		try {
			float var10 = this.interpolateRotation(par1EntityLivingBase.prevRenderYawOffset, par1EntityLivingBase.renderYawOffset, par9);
			float var11 = this.interpolateRotation(par1EntityLivingBase.prevRotationYawHead, par1EntityLivingBase.rotationYawHead, par9);
			if (par1EntityLivingBase.isPotionActive(Potion.confusion)) {
				var11 = (float)((double)var11 - Math.cos((double)((float)par1EntityLivingBase.ticksExisted * 0.15F)) * 3.0);
			}

			float var13;
			if (par1EntityLivingBase.isRiding() && par1EntityLivingBase.ridingEntity instanceof EntityLivingBase) {
				EntityLivingBase var12 = (EntityLivingBase)par1EntityLivingBase.ridingEntity;
				var10 = this.interpolateRotation(var12.prevRenderYawOffset, var12.renderYawOffset, par9);
				var13 = MathHelper.wrapAngleTo180_float(var11 - var10);
				if (var13 < -85.0F) {
					var13 = -85.0F;
				}

				if (var13 >= 85.0F) {
					var13 = 85.0F;
				}

				var10 = var11 - var13;
				if (var13 * var13 > 2500.0F) {
					var10 += var13 * 0.2F;
				}
			}

			float var26 = par1EntityLivingBase.prevRotationPitch + (par1EntityLivingBase.rotationPitch - par1EntityLivingBase.prevRotationPitch) * par9;
			if (par1EntityLivingBase.isPotionActive(Potion.confusion)) {
				var26 = (float)((double)var26 + Math.sin((double)((float)par1EntityLivingBase.ticksExisted * 0.15F)) * 6.0);
			}

			this.renderLivingAt(par1EntityLivingBase, par2, par4, par6);
			var13 = this.handleRotationFloat(par1EntityLivingBase, par9);
			this.rotateCorpse(par1EntityLivingBase, var13, var10, par9);
			float var14 = 0.0625F;
			GL11.glEnable(32826);
			GL11.glScalef(-1.0F, -1.0F, 1.0F);
			this.preRenderCallback(par1EntityLivingBase, par9);
			GL11.glTranslatef(0.0F, -24.0F * var14 - 0.0078125F, 0.0F);
			float var15 = par1EntityLivingBase.prevLimbSwingAmount + (par1EntityLivingBase.limbSwingAmount - par1EntityLivingBase.prevLimbSwingAmount) * par9;
			float var16 = par1EntityLivingBase.limbSwing - par1EntityLivingBase.limbSwingAmount * (1.0F - par9);
			if (par1EntityLivingBase.isChild()) {
				var16 *= 3.0F;
			}

			if (var15 > 1.0F) {
				var15 = 1.0F;
			}

			GL11.glEnable(3008);
			this.mainModel.setLivingAnimations(par1EntityLivingBase, var16, var15, par9);
			this.renderModel(par1EntityLivingBase, var16, var15, var13, var11 - var10, var26, var14);

			float var19;
			int var18;
			float var20;
			float var22;
			int var28;
			for(int var17 = 0; var17 < 4; ++var17) {
				var18 = this.shouldRenderPass(par1EntityLivingBase, var17, par9);
				if (var18 > 0) {
					this.renderPassModel.setLivingAnimations(par1EntityLivingBase, var16, var15, par9);
					this.renderPassModel.render(par1EntityLivingBase, var16, var15, var13, var11 - var10, var26, var14);
					if ((var18 & 240) == 16) {
						this.func_82408_c(par1EntityLivingBase, var17, par9);
						this.renderPassModel.render(par1EntityLivingBase, var16, var15, var13, var11 - var10, var26, var14);
					}

					if ((var18 & 15) == 15) {
						var19 = (float)par1EntityLivingBase.ticksExisted + par9;
						this.bindTexture(RES_ITEM_GLINT);
						GL11.glEnable(3042);
						var20 = 0.5F;
						GL11.glColor4f(var20, var20, var20, 1.0F);
						GL11.glDepthFunc(514);
						GL11.glDepthMask(false);

						for(var28 = 0; var28 < 2; ++var28) {
							GL11.glDisable(2896);
							var22 = 0.76F;
							GL11.glColor4f(0.5F * var22, 0.25F * var22, 0.8F * var22, 1.0F);
							GL11.glBlendFunc(768, 1);
							GL11.glMatrixMode(5890);
							GL11.glLoadIdentity();
							float var23 = var19 * (0.001F + (float)var28 * 0.003F) * 20.0F;
							float var24 = 0.33333334F;
							GL11.glScalef(var24, var24, var24);
							GL11.glRotatef(30.0F - (float)var28 * 60.0F, 0.0F, 0.0F, 1.0F);
							GL11.glTranslatef(0.0F, var23, 0.0F);
							GL11.glMatrixMode(5888);
							this.renderPassModel.render(par1EntityLivingBase, var16, var15, var13, var11 - var10, var26, var14);
						}

						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glMatrixMode(5890);
						GL11.glDepthMask(true);
						GL11.glLoadIdentity();
						GL11.glMatrixMode(5888);
						GL11.glEnable(2896);
						GL11.glDisable(3042);
						GL11.glDepthFunc(515);
					}

					GL11.glDisable(3042);
					GL11.glEnable(3008);
				}
			}

			GL11.glDepthMask(true);
			this.renderEquippedItems(par1EntityLivingBase, par9);
			float var27 = par1EntityLivingBase.getBrightness(par9);
			var18 = this.getColorMultiplier(par1EntityLivingBase, var27, par9);
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GL11.glDisable(3553);
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
			if ((var18 >> 24 & 255) > 0 || par1EntityLivingBase.hurtTime > 0 || par1EntityLivingBase.deathTime > 0 || par1EntityLivingBase.tagged) {
				GL11.glDisable(3553);
				GL11.glDisable(3008);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glDepthFunc(514);
				if (par1EntityLivingBase.tagged) {
					GL11.glPushAttrib(2929);
					GL11.glDisable(2929);
					GL11.glColor4f(var27, 0.0F, 0.0F, 0.4F);
					this.mainModel.render(par1EntityLivingBase, var16, var15, var13, var11 - var10, var26, var14);
					GL11.glPopAttrib();
				}

				if (par1EntityLivingBase.hurtTime > 0 || par1EntityLivingBase.deathTime > 0) {
					GL11.glColor4f(var27, 0.0F, 0.0F, 0.4F);
					this.mainModel.render(par1EntityLivingBase, var16, var15, var13, var11 - var10, var26, var14);

					for(var28 = 0; var28 < 4; ++var28) {
						if (this.inheritRenderPass(par1EntityLivingBase, var28, par9) >= 0) {
							GL11.glColor4f(var27, 0.0F, 0.0F, 0.4F);
							this.renderPassModel.render(par1EntityLivingBase, var16, var15, var13, var11 - var10, var26, var14);
						}
					}
				}

				if ((var18 >> 24 & 255) > 0) {
					var19 = (float)(var18 >> 16 & 255) / 255.0F;
					var20 = (float)(var18 >> 8 & 255) / 255.0F;
					float var30 = (float)(var18 & 255) / 255.0F;
					var22 = (float)(var18 >> 24 & 255) / 255.0F;
					GL11.glColor4f(var19, var20, var30, var22);
					this.mainModel.render(par1EntityLivingBase, var16, var15, var13, var11 - var10, var26, var14);

					for(int var29 = 0; var29 < 4; ++var29) {
						if (this.inheritRenderPass(par1EntityLivingBase, var29, par9) >= 0) {
							GL11.glColor4f(var19, var20, var30, var22);
							this.renderPassModel.render(par1EntityLivingBase, var16, var15, var13, var11 - var10, var26, var14);
						}
					}
				}

				GL11.glDepthFunc(515);
				GL11.glDisable(3042);
				GL11.glEnable(3008);
				GL11.glEnable(3553);
			}

			GL11.glDisable(32826);
		} catch (Exception var25) {
			var25.printStackTrace();
		}

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(3553);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glEnable(2884);
		GL11.glPopMatrix();
		this.passSpecialRender(par1EntityLivingBase, par2, par4, par6);
		MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(par1EntityLivingBase, this));
	}

	protected void renderModelGlowing(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4, float par5, float par6, float par7) {
		ResourceLocation glowing_texture = this.getGlowingTextureCounterpart(this.getEntityTexture(par1EntityLivingBase));
		if (glowing_texture != null) {
			this.use_glowing_texture = true;
			this.bindTexture(glowing_texture);
			GL11.glDepthMask(!par1EntityLivingBase.isInvisible());
			int var5 = 15728880;
			int var6 = var5 % 65536;
			int var7 = var5 / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var6 / 1.0F, (float)var7 / 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mainModel.render(par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
			OpenGlHelper.restorePreviousLightmapTextureCoords();
			this.use_glowing_texture = false;
		}

	}

	protected void renderModel(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4, float par5, float par6, float par7) {
		this.bindEntityTexture(par1EntityLivingBase);
		if (!par1EntityLivingBase.isInvisible()) {
			float alpha = this.renderManager.getEntityRenderObject(par1EntityLivingBase).getModelOpacity(par1EntityLivingBase);
			if (alpha < 0.99F) {
				GL11.glPushMatrix();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
				GL11.glDepthMask(false);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glAlphaFunc(516, 0.003921569F);
			}

			this.mainModel.render(par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
			this.renderModelGlowing(par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
			if (alpha < 0.99F) {
				GL11.glDisable(3042);
				GL11.glAlphaFunc(516, 0.1F);
				GL11.glPopMatrix();
				GL11.glDepthMask(true);
			}
		} else if (!par1EntityLivingBase.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
			GL11.glDepthMask(false);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glAlphaFunc(516, 0.003921569F);
			this.mainModel.render(par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
			this.renderModelGlowing(par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
			GL11.glDisable(3042);
			GL11.glAlphaFunc(516, 0.1F);
			GL11.glPopMatrix();
			GL11.glDepthMask(true);
		} else {
			this.mainModel.setRotationAngles(par2, par3, par4, par5, par6, par7, par1EntityLivingBase);
		}

	}

	protected void renderLivingAt(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6) {
		GL11.glTranslatef((float)par2, (float)par4, (float)par6);
	}

	protected void rotateCorpse(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4) {
		GL11.glRotatef(180.0F - par3, 0.0F, 1.0F, 0.0F);
		if (par1EntityLivingBase.deathTime > 0) {
			float var5 = ((float)par1EntityLivingBase.deathTime + par4 - 1.0F) / 20.0F * 1.6F;
			var5 = MathHelper.sqrt_float(var5);
			if (var5 > 1.0F) {
				var5 = 1.0F;
			}

			GL11.glRotatef(var5 * this.getDeathMaxRotation(par1EntityLivingBase), 0.0F, 0.0F, 1.0F);
		} else {
			String var6 = EnumChatFormatting.func_110646_a(par1EntityLivingBase.getEntityName());
			if ((var6.equals("Dinnerbone") || var6.equals("Grumm")) && (!(par1EntityLivingBase instanceof EntityPlayer) || !((EntityPlayer)par1EntityLivingBase).getHideCape())) {
				GL11.glTranslatef(0.0F, par1EntityLivingBase.height + 0.1F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			}
		}

	}

	protected float renderSwingProgress(EntityLivingBase par1EntityLivingBase, float par2) {
		return par1EntityLivingBase.getSwingProgress(par2);
	}

	protected float handleRotationFloat(EntityLivingBase par1EntityLivingBase, float par2) {
		return (float)par1EntityLivingBase.ticksExisted + par2;
	}

	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
	}

	protected void renderArrowsStuckInEntity(EntityLivingBase par1EntityLivingBase, float par2) {
		int var3 = par1EntityLivingBase.getArrowCountInEntity();
		if (var3 > 0) {
			EntityArrow var4 = new EntityArrow(par1EntityLivingBase.worldObj, par1EntityLivingBase.posX, par1EntityLivingBase.posY, par1EntityLivingBase.posZ, Item.arrowIron, false);
			Random var5 = new Random((long)par1EntityLivingBase.entityId);
			RenderHelper.disableStandardItemLighting();

			for(int var6 = 0; var6 < var3; ++var6) {
				GL11.glPushMatrix();
				ModelRenderer var7 = this.mainModel.getRandomModelBox(var5);
				ModelBox var8 = (ModelBox)var7.cubeList.get(var5.nextInt(var7.cubeList.size()));
				var7.postRender(0.0625F);
				float var9 = var5.nextFloat();
				float var10 = var5.nextFloat();
				float var11 = var5.nextFloat();
				float var12 = (var8.posX1 + (var8.posX2 - var8.posX1) * var9) / 16.0F;
				float var13 = (var8.posY1 + (var8.posY2 - var8.posY1) * var10) / 16.0F;
				float var14 = (var8.posZ1 + (var8.posZ2 - var8.posZ1) * var11) / 16.0F;
				GL11.glTranslatef(var12, var13, var14);
				var9 = var9 * 2.0F - 1.0F;
				var10 = var10 * 2.0F - 1.0F;
				var11 = var11 * 2.0F - 1.0F;
				var9 *= -1.0F;
				var10 *= -1.0F;
				var11 *= -1.0F;
				float var15 = MathHelper.sqrt_float(var9 * var9 + var11 * var11);
				var4.prevRotationYaw = var4.rotationYaw = (float)(Math.atan2((double)var9, (double)var11) * 180.0 / Math.PI);
				var4.prevRotationPitch = var4.rotationPitch = (float)(Math.atan2((double)var10, (double)var15) * 180.0 / Math.PI);
				double var16 = 0.0;
				double var18 = 0.0;
				double var20 = 0.0;
				float var22 = 0.0F;
				this.renderManager.renderEntityWithPosYaw(var4, var16, var18, var20, var22, par2);
				GL11.glPopMatrix();
			}

			RenderHelper.enableStandardItemLighting();
		}

	}

	protected int inheritRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.shouldRenderPass(par1EntityLivingBase, par2, par3);
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return -1;
	}

	protected void func_82408_c(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
	}

	protected float getDeathMaxRotation(EntityLivingBase par1EntityLivingBase) {
		return 90.0F;
	}

	protected int getColorMultiplier(EntityLivingBase par1EntityLivingBase, float par2, float par3) {
		return 0;
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
	}

	protected void passSpecialRender(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6) {
		if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Specials.Pre(par1EntityLivingBase, this))) return;
		if (this.func_110813_b(par1EntityLivingBase)) {
			float var8 = 1.6F;
			float var9 = 0.016666668F * var8;
			double var10 = par1EntityLivingBase.getDistanceSqToEntity(this.renderManager.livingPlayer);
			float var12 = par1EntityLivingBase.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;
			if (var10 < (double)(var12 * var12)) {
				String var13 = par1EntityLivingBase.getTranslatedEntityName();
				if (par1EntityLivingBase.isSneaking()) {
					FontRenderer var14 = this.getFontRendererFromRenderManager();
					GL11.glPushMatrix();
					GL11.glTranslatef((float)par2 + 0.0F, (float)par4 + par1EntityLivingBase.height + 0.5F, (float)par6);
					GL11.glNormal3f(0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
					GL11.glScalef(-var9, -var9, var9);
					GL11.glDisable(2896);
					GL11.glTranslatef(0.0F, 0.25F / var9, 0.0F);
					GL11.glDepthMask(false);
					GL11.glEnable(3042);
					GL11.glBlendFunc(770, 771);
					Tessellator var15 = Tessellator.instance;
					GL11.glDisable(3553);
					var15.startDrawingQuads();
					int var16 = var14.getStringWidth(var13) / 2;
					var15.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
					var15.addVertex((double)(-var16 - 1), -1.0, 0.0);
					var15.addVertex((double)(-var16 - 1), 8.0, 0.0);
					var15.addVertex((double)(var16 + 1), 8.0, 0.0);
					var15.addVertex((double)(var16 + 1), -1.0, 0.0);
					var15.draw();
					GL11.glEnable(3553);
					GL11.glDepthMask(true);
					var14.drawString(var13, -var14.getStringWidth(var13) / 2, 0, 553648127);
					GL11.glEnable(2896);
					GL11.glDisable(3042);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glPopMatrix();
				} else {
					this.func_96449_a(par1EntityLivingBase, par2, par4, par6, var13, var9, var10);
				}
			}
		}
		MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Specials.Post(par1EntityLivingBase, this));
	}

	protected boolean func_110813_b(EntityLivingBase par1EntityLivingBase) {
		return Minecraft.isGuiEnabled() && par1EntityLivingBase != this.renderManager.livingPlayer && !par1EntityLivingBase.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer) && par1EntityLivingBase.riddenByEntity == null;
	}

	protected void func_96449_a(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, String par8Str, float par9, double par10) {
		if (par1EntityLivingBase.inBed()) {
			this.renderLivingLabel(par1EntityLivingBase, par8Str, par2, par4 - 1.5, par6, 64);
		} else {
			this.renderLivingLabel(par1EntityLivingBase, par8Str, par2, par4, par6, 64);
		}

	}

	protected void renderLivingLabel(EntityLivingBase par1EntityLivingBase, String par2Str, double par3, double par5, double par7, int par9) {
		if (par1EntityLivingBase.isEntityPlayer()) {
			EntityPlayer player = par1EntityLivingBase.getAsPlayer();
			if (player.isGhost() || player.isZevimrgvInTournament()) {
				return;
			}
		}

		double var10 = par1EntityLivingBase.getDistanceSqToEntity(this.renderManager.livingPlayer);
		if (var10 <= (double)(par9 * par9)) {
			FontRenderer var12 = this.getFontRendererFromRenderManager();
			float var13 = 1.6F;
			float var14 = 0.016666668F * var13;
			GL11.glPushMatrix();
			GL11.glTranslatef((float)par3 + 0.0F, (float)par5 + par1EntityLivingBase.height + 0.5F, (float)par7);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.renderManager.playerViewX, this.renderManager.options.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-var14, -var14, var14);
			GL11.glDisable(2896);
			GL11.glDepthMask(false);
			GL11.glDisable(2929);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			Tessellator var15 = Tessellator.instance;
			byte var16 = 0;
			if (par2Str.equals("deadmau5")) {
				var16 = -10;
			}

			GL11.glDisable(3553);
			var15.startDrawingQuads();
			int var17 = var12.getStringWidth(par2Str) / 2;
			var15.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
			var15.addVertex((double)(-var17 - 1), (double)(-1 + var16), 0.0);
			var15.addVertex((double)(-var17 - 1), (double)(8 + var16), 0.0);
			var15.addVertex((double)(var17 + 1), (double)(8 + var16), 0.0);
			var15.addVertex((double)(var17 + 1), (double)(-1 + var16), 0.0);
			var15.draw();
			GL11.glEnable(3553);
			var12.drawString(par2Str, -var12.getStringWidth(par2Str) / 2, var16, 553648127);
			GL11.glEnable(2929);
			GL11.glDepthMask(true);
			var12.drawString(par2Str, -var12.getStringWidth(par2Str) / 2, var16, -1);
			GL11.glEnable(2896);
			GL11.glDisable(3042);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}

	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderLiving((EntityLivingBase)par1Entity, par2, par4, par6, par8, par9);
	}
}
