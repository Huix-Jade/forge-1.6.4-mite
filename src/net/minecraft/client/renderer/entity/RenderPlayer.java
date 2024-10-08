package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumItemInUseAction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

public class RenderPlayer extends RendererLivingEntity {
	private static final ResourceLocation steveTextures = new ResourceLocation("textures/entity/steve.png");
	private ModelBiped modelBipedMain;
	private ModelBiped modelArmorChestplate;
	private ModelBiped modelArmor;
	public static boolean see_zevimrgv_in_tournament;

	public RenderPlayer() {
		super(new ModelBiped(0.0F), 0.5F);
		this.modelBipedMain = (ModelBiped)this.mainModel;
		this.modelArmorChestplate = new ModelBiped(1.0F);
		this.modelArmor = new ModelBiped(0.5F);
	}

	protected int setArmorModel(AbstractClientPlayer par1AbstractClientPlayer, int par2, float par3) {
		ItemStack itemstack = par1AbstractClientPlayer.inventory.armorItemInSlot(3 - par2);
		RenderPlayerEvent.SetArmorModel event = new RenderPlayerEvent.SetArmorModel(par1AbstractClientPlayer, this, 3 - par2, par3, itemstack);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.result != -1)
		{
			return event.result;
		}
		if (itemstack != null) {
			Item var5 = itemstack.getItem();
			if (var5 instanceof ItemArmor) {
				ItemArmor var6 = (ItemArmor)var5;
				this.bindTexture(RenderBiped.getArmorResource(par1AbstractClientPlayer, itemstack, par2, null));
				ModelBiped var7 = par2 == 2 ? this.modelArmor : this.modelArmorChestplate;
				var7.bipedHead.showModel = par2 == 0;
				var7.bipedHeadwear.showModel = par2 == 0;
				var7.bipedBody.showModel = par2 == 1 || par2 == 2;
				var7.bipedRightArm.showModel = par2 == 1;
				var7.bipedLeftArm.showModel = par2 == 1;
				var7.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
				var7.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
				var7 = ForgeHooksClient.getArmorModel(par1AbstractClientPlayer, itemstack, par2, var7);
				this.setRenderPassModel(var7);
				var7.onGround = this.mainModel.onGround;
				var7.isRiding = this.mainModel.isRiding;
				var7.isChild = this.mainModel.isChild;
				float var8 = 1.0F;
				//Move outside if to allow for more then just CLOTH
				int var9 = var6.getColor(itemstack);
				if (var9 != -1)
				{
					float var10 = (float)(var9 >> 16 & 255) / 255.0F;
					float var11 = (float)(var9 >> 8 & 255) / 255.0F;
					float var12 = (float)(var9 & 255) / 255.0F;
					GL11.glColor3f(var8 * var10, var8 * var11, var8 * var12);
					if (itemstack.isItemEnchanted()) {
						return 31;
					}

					return 16;
				}

				GL11.glColor3f(var8, var8, var8);
				if (itemstack.isItemEnchanted()) {
					return 15;
				}

				return 1;
			}
		}

		return -1;
	}

	protected void func_130220_b(AbstractClientPlayer par1AbstractClientPlayer, int par2, float par3) {
		ItemStack var4 = par1AbstractClientPlayer.inventory.armorItemInSlot(3 - par2);
		if (var4 != null) {
			Item var5 = var4.getItem();
			if (var5 instanceof ItemArmor) {
				this.bindTexture(RenderBiped.getArmorResource(par1AbstractClientPlayer, var4, par2, "overlay"));
				float var6 = 1.0F;
				GL11.glColor3f(var6, var6, var6);
			}
		}

	}

	public void func_130009_a(AbstractClientPlayer par1AbstractClientPlayer, double par2, double par4, double par6, float par8, float par9) {
		if (MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Pre(par1AbstractClientPlayer, this, par9))) return;
		float var10 = 1.0F;
		GL11.glColor3f(var10, var10, var10);
		ItemStack var11 = par1AbstractClientPlayer.inventory.getCurrentItemStack();
		this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = var11 != null ? 1 : 0;
		if (var11 != null && par1AbstractClientPlayer.getItemInUseCount() > 0) {
			EnumItemInUseAction var12 = var11.getItemInUseAction(par1AbstractClientPlayer);
			if (var12 == EnumItemInUseAction.BLOCK) {
				this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 3;
			} else if (var12 == EnumItemInUseAction.BOW) {
				this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = true;
			}
		}

		this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = par1AbstractClientPlayer.isSneaking();
		double var14 = par4 - (double)par1AbstractClientPlayer.yOffset;
		if (par1AbstractClientPlayer.isSneaking() && !(par1AbstractClientPlayer instanceof EntityPlayerSP)) {
			var14 -= 0.125;
		}

		super.doRenderLiving(par1AbstractClientPlayer, par2, var14, par6, par8, par9);
		this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
		this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
		this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
		MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Post(par1AbstractClientPlayer, this, par9));
	}

	protected ResourceLocation func_110817_a(AbstractClientPlayer par1AbstractClientPlayer) {
		return par1AbstractClientPlayer.getLocationSkin();
	}

	protected void renderSpecials(AbstractClientPlayer par1AbstractClientPlayer, float par2) {
		RenderPlayerEvent.Specials.Pre event = new RenderPlayerEvent.Specials.Pre(par1AbstractClientPlayer, this, par2);
		if (MinecraftForge.EVENT_BUS.post(event))
		{
			return;
		}

		float var3 = 1.0F;
		GL11.glColor3f(var3, var3, var3);
		super.renderEquippedItems(par1AbstractClientPlayer, par2);
		super.renderArrowsStuckInEntity(par1AbstractClientPlayer, par2);
		ItemStack itemstack = par1AbstractClientPlayer.inventory.armorItemInSlot(3);
		if (itemstack != null && event.renderHelmet) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedHead.postRender(0.0625F);
			float var5;
			if (itemstack != null && itemstack.getItem() instanceof ItemBlock)
			{
				IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
				boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));

				if (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType()))
				{
					var5 = 0.625F;
					GL11.glTranslatef(0.0F, -0.25F, 0.0F);
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
					GL11.glScalef(var5, -var5, -var5);
				}

				this.renderManager.itemRenderer.renderItem(par1AbstractClientPlayer, itemstack, 0);
			} else if (itemstack.getItem().itemID == Item.skull.itemID) {
				var5 = 1.0625F;
				GL11.glScalef(var5, -var5, -var5);
				String var6 = "";
				if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("SkullOwner")) {
					var6 = itemstack.getTagCompound().getString("SkullOwner");
				}

				TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, itemstack.getItemSubtype(), var6);
			}

			GL11.glPopMatrix();
		}

		float var14;
		if (par1AbstractClientPlayer.getCommandSenderName().equals("deadmau5") && par1AbstractClientPlayer.getTextureSkin().isTextureUploaded()) {
			this.bindTexture(par1AbstractClientPlayer.getLocationSkin());

			for(int var23 = 0; var23 < 2; ++var23) {
				float var27 = par1AbstractClientPlayer.prevRotationYaw + (par1AbstractClientPlayer.rotationYaw - par1AbstractClientPlayer.prevRotationYaw) * par2 - (par1AbstractClientPlayer.prevRenderYawOffset + (par1AbstractClientPlayer.renderYawOffset - par1AbstractClientPlayer.prevRenderYawOffset) * par2);
				float var7 = par1AbstractClientPlayer.prevRotationPitch + (par1AbstractClientPlayer.rotationPitch - par1AbstractClientPlayer.prevRotationPitch) * par2;
				GL11.glPushMatrix();
				GL11.glRotatef(var27, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(var7, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(0.375F * (float)(var23 * 2 - 1), 0.0F, 0.0F);
				GL11.glTranslatef(0.0F, -0.375F, 0.0F);
				GL11.glRotatef(-var7, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-var27, 0.0F, 1.0F, 0.0F);
				var14 = 1.3333334F;
				GL11.glScalef(var14, var14, var14);
				this.modelBipedMain.renderEars(0.0625F);
				GL11.glPopMatrix();
			}
		}

		boolean var24 = par1AbstractClientPlayer.getTextureCape().isTextureUploaded();
		boolean var25 = !par1AbstractClientPlayer.isInvisible();
		boolean var26 = !par1AbstractClientPlayer.getHideCape();
		var24 = event.renderCape && var24;
		if (var24 && var25 && var26) {
			this.bindTexture(par1AbstractClientPlayer.getLocationCape());
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, 0.125F);
			double var29 = par1AbstractClientPlayer.field_71091_bM + (par1AbstractClientPlayer.field_71094_bP - par1AbstractClientPlayer.field_71091_bM) * (double)par2 - (par1AbstractClientPlayer.prevPosX + (par1AbstractClientPlayer.posX - par1AbstractClientPlayer.prevPosX) * (double)par2);
			double var10 = par1AbstractClientPlayer.field_71096_bN + (par1AbstractClientPlayer.field_71095_bQ - par1AbstractClientPlayer.field_71096_bN) * (double)par2 - (par1AbstractClientPlayer.prevPosY + (par1AbstractClientPlayer.posY - par1AbstractClientPlayer.prevPosY) * (double)par2);
			double var12 = par1AbstractClientPlayer.field_71097_bO + (par1AbstractClientPlayer.field_71085_bR - par1AbstractClientPlayer.field_71097_bO) * (double)par2 - (par1AbstractClientPlayer.prevPosZ + (par1AbstractClientPlayer.posZ - par1AbstractClientPlayer.prevPosZ) * (double)par2);
			var14 = par1AbstractClientPlayer.prevRenderYawOffset + (par1AbstractClientPlayer.renderYawOffset - par1AbstractClientPlayer.prevRenderYawOffset) * par2;
			double var15 = (double)MathHelper.sin(var14 * 3.1415927F / 180.0F);
			double var17 = (double)(-MathHelper.cos(var14 * 3.1415927F / 180.0F));
			float var19 = (float)var10 * 10.0F;
			if (var19 < -6.0F) {
				var19 = -6.0F;
			}

			if (var19 > 32.0F) {
				var19 = 32.0F;
			}

			float var20 = (float)(var29 * var15 + var12 * var17) * 100.0F;
			float var21 = (float)(var29 * var17 - var12 * var15) * 100.0F;
			if (var20 < 0.0F) {
				var20 = 0.0F;
			}

			float var22 = par1AbstractClientPlayer.prevCameraYaw + (par1AbstractClientPlayer.cameraYaw - par1AbstractClientPlayer.prevCameraYaw) * par2;
			var19 += MathHelper.sin((par1AbstractClientPlayer.prevDistanceWalkedModified + (par1AbstractClientPlayer.distanceWalkedModified - par1AbstractClientPlayer.prevDistanceWalkedModified) * par2) * 6.0F) * 32.0F * var22;
			if (par1AbstractClientPlayer.isSneaking()) {
				var19 += 25.0F;
			}

			GL11.glRotatef(6.0F + var20 / 2.0F + var19, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(var21 / 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-var21 / 2.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			this.modelBipedMain.renderCloak(0.0625F);
			GL11.glPopMatrix();
		}

		ItemStack var28 = par1AbstractClientPlayer.inventory.getCurrentItemStack();
		if (var28 != null && event.renderItem) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedRightArm.postRender(0.0625F);
			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
			ItemRenderer var10000;
			if (var28.getItem() instanceof ItemFishingRod && par1AbstractClientPlayer.fishEntity != null) {
				var10000 = this.renderManager.itemRenderer;
				ItemRenderer.render_icon_override = ((ItemFishingRod)var28.getItem()).func_94597_g();
			}

			EnumItemInUseAction var9 = null;
			if (par1AbstractClientPlayer.getItemInUseCount() > 0) {
				var9 = var28.getItemInUseAction(par1AbstractClientPlayer);
			}

			float var31;
			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var28, EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, var28, BLOCK_3D));
			boolean isBlock = var28.itemID < Block.blocksList.length && var28.getItemSpriteNumber() == 0;

			if (is3D || (isBlock && RenderBlocks.renderItemIn3d(Block.blocksList[var28.itemID].getRenderType())))
			{
				var31 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				var31 *= 0.75F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(-var31, -var31, var31);
			} else if (var28.getItem() instanceof ItemBow) {
				var31 = 0.625F;
				GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(var31, -var31, var31);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else if (Item.itemsList[var28.itemID].isFull3D()) {
				var31 = 0.625F;
				if (Item.itemsList[var28.itemID].shouldRotateAroundWhenRendering()) {
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(0.0F, -0.125F, 0.0F);
				}

				if (par1AbstractClientPlayer.getItemInUseCount() > 0 && var9 == EnumItemInUseAction.BLOCK) {
					GL11.glTranslatef(0.05F, 0.0F, -0.1F);
					GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
				}

				GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
				GL11.glScalef(var31, -var31, var31);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				var31 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(var31, var31, var31);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			float var13;
			int var33;
			float var32;
			if (var28.getItem().requiresMultipleRenderPasses()) {
				for(var33 = 0; var33 <= var28.getItem().getRenderPasses(var28.getItemDamage()); ++var33) {
					int var11 = var28.getItem().getColorFromItemStack(var28, var33);
					var32 = (float)(var11 >> 16 & 255) / 255.0F;
					var13 = (float)(var11 >> 8 & 255) / 255.0F;
					var14 = (float)(var11 & 255) / 255.0F;
					GL11.glColor4f(var32, var13, var14, 1.0F);
					this.renderManager.itemRenderer.renderItem(par1AbstractClientPlayer, var28, var33);
				}
			} else {
				var33 = var28.getItem().getColorFromItemStack(var28, 0);
				float var30 = (float)(var33 >> 16 & 255) / 255.0F;
				var32 = (float)(var33 >> 8 & 255) / 255.0F;
				var13 = (float)(var33 & 255) / 255.0F;
				GL11.glColor4f(var30, var32, var13, 1.0F);
				this.renderManager.itemRenderer.renderItem(par1AbstractClientPlayer, var28, 0);
			}

			var10000 = this.renderManager.itemRenderer;
			ItemRenderer.render_icon_override = null;
			GL11.glPopMatrix();
		}

		MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Specials.Post(par1AbstractClientPlayer, this, par2));
	}

	protected void renderPlayerScale(AbstractClientPlayer par1AbstractClientPlayer, float par2) {
		float var3 = 0.9375F;
		GL11.glScalef(var3, var3, var3);
	}

	protected void func_96450_a(AbstractClientPlayer par1AbstractClientPlayer, double par2, double par4, double par6, String par8Str, float par9, double par10) {
		if (par10 < 100.0) {
			Scoreboard var12 = par1AbstractClientPlayer.getWorldScoreboard();
			ScoreObjective var13 = var12.func_96539_a(2);
			if (var13 != null) {
				Score var14 = var12.func_96529_a(par1AbstractClientPlayer.getEntityName(), var13);
				if (par1AbstractClientPlayer.inBed()) {
					this.renderLivingLabel(par1AbstractClientPlayer, var14.getScorePoints() + " " + var13.getDisplayName(), par2, par4 - 1.5, par6, 64);
				} else {
					this.renderLivingLabel(par1AbstractClientPlayer, var14.getScorePoints() + " " + var13.getDisplayName(), par2, par4, par6, 64);
				}

				par4 += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * par9);
			}
		}

		super.func_96449_a(par1AbstractClientPlayer, par2, par4, par6, par8Str, par9, par10);
	}

	public void renderFirstPersonArm(EntityPlayer player) {
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		float alpha = this.getModelOpacity(player);
		if (alpha < 0.99F) {
			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
			GL11.glDepthMask(false);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glAlphaFunc(516, 0.003921569F);
		}

		this.modelBipedMain.onGround = 0.0F;
		this.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		this.modelBipedMain.bipedRightArm.render(0.0625F);
		if (alpha < 0.99F) {
			GL11.glDisable(3042);
			GL11.glAlphaFunc(516, 0.1F);
			GL11.glPopMatrix();
			GL11.glDepthMask(true);
		}

	}

	protected void renderPlayerSleep(AbstractClientPlayer par1AbstractClientPlayer, double par2, double par4, double par6) {
		if (par1AbstractClientPlayer.isEntityAlive() && par1AbstractClientPlayer.inBed()) {
			super.renderLivingAt(par1AbstractClientPlayer, par2 + (double)par1AbstractClientPlayer.field_71079_bU, par4 + (double)par1AbstractClientPlayer.field_71082_cx, par6 + (double)par1AbstractClientPlayer.field_71089_bV);
		} else {
			super.renderLivingAt(par1AbstractClientPlayer, par2, par4, par6);
		}

	}

	protected void rotatePlayer(AbstractClientPlayer par1AbstractClientPlayer, float par2, float par3, float par4) {
		if (par1AbstractClientPlayer.isEntityAlive() && par1AbstractClientPlayer.inBed()) {
			GL11.glRotatef(par1AbstractClientPlayer.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.getDeathMaxRotation(par1AbstractClientPlayer), 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} else {
			super.rotateCorpse(par1AbstractClientPlayer, par2, par3, par4);
		}

	}

	protected void func_96449_a(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, String par8Str, float par9, double par10) {
		this.func_96450_a((AbstractClientPlayer)par1EntityLivingBase, par2, par4, par6, par8Str, par9, par10);
	}

	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		this.renderPlayerScale((AbstractClientPlayer)par1EntityLivingBase, par2);
	}

	protected void func_82408_c(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		this.func_130220_b((AbstractClientPlayer)par1EntityLivingBase, par2, par3);
	}

	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return this.setArmorModel((AbstractClientPlayer)par1EntityLivingBase, par2, par3);
	}

	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
		this.renderSpecials((AbstractClientPlayer)par1EntityLivingBase, par2);
	}

	protected void rotateCorpse(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4) {
		this.rotatePlayer((AbstractClientPlayer)par1EntityLivingBase, par2, par3, par4);
	}

	protected void renderLivingAt(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6) {
		this.renderPlayerSleep((AbstractClientPlayer)par1EntityLivingBase, par2, par4, par6);
	}

	public void doRenderLiving(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
		this.func_130009_a((AbstractClientPlayer)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110817_a((AbstractClientPlayer)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		if (par1Entity.isSneaking()) {
			par4 -= 0.18000000715255737;
		}

		this.func_130009_a((AbstractClientPlayer)par1Entity, par2, par4, par6, par8, par9);
	}

	public float getModelOpacity(Entity entity) {
		if (entity.isEntityPlayer()) {
			EntityPlayer player = entity.getAsPlayer();
			if (player.isGhost()) {
				return 0.0F;
			}

			if (player.isZevimrgvInTournament() && !see_zevimrgv_in_tournament) {
				return 0.0F;
			}
		}

		return super.getModelOpacity(entity);
	}
}
