package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

public class RenderItem extends Render {
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private RenderBlocks itemRenderBlocks = new RenderBlocks();
	private Random random = new Random();
	public boolean renderWithColor = true;
	public float zLevel;
	public static boolean renderInFrame;

	public RenderItem() {
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}

	public void doRenderItem(EntityItem par1EntityItem, double par2, double par4, double par6, float par8, float par9) {
		this.bindEntityTexture(par1EntityItem);
		this.random.setSeed(187L);
		ItemStack itemstack = par1EntityItem.getEntityItem();
		if (itemstack.getItem() != null) {
			GL11.glPushMatrix();
			float var11 = shouldBob() ? MathHelper.sin(((float)par1EntityItem.age + par9) / 10.0F + par1EntityItem.hoverStart) * 0.1F + 0.1F : 0F;
			float var12 = (((float)par1EntityItem.age + par9) / 20.0F + par1EntityItem.hoverStart) * 57.295776F;
			byte var13 = getMiniBlockCount(itemstack);

			GL11.glTranslatef((float)par2, (float)par4 + var11, (float)par6);
			GL11.glEnable(32826);
			float var19;
			float var18;
			float var20;
			int var26;
			int var15;
			if (itemstack.getItemSpriteNumber() == 0 && itemstack.itemID < Block.blocksList.length && Block.blocksList[itemstack.itemID] != null && RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType())) {
				Block var21 = Block.blocksList[itemstack.itemID];
				GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
				if (renderInFrame) {
					GL11.glScalef(1.25F, 1.25F, 1.25F);
					GL11.glTranslatef(0.0F, 0.05F, 0.0F);
					GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				float var25 = 0.25F;
				var15 = var21.getRenderType();
				if (var15 == 1 || var15 == 19 || var15 == 12 || var15 == 2) {
					var25 = 0.5F;
				}

				GL11.glScalef(var25, var25, var25);

				for(var26 = 0; var26 < var13; ++var26) {
					GL11.glPushMatrix();
					if (var26 > 0) {
						var18 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var25;
						var19 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var25;
						var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var25;
						GL11.glTranslatef(var18, var19, var20);
					}

					var18 = 1.0F;
					this.itemRenderBlocks.renderBlockAsItem(var21, itemstack.getItemSubtype(), var18);
					GL11.glPopMatrix();
				}
			} else {
				float var16;
				if (itemstack.getItem().requiresMultipleRenderPasses()) {
					if (renderInFrame) {
						GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
						GL11.glTranslatef(0.0F, -0.05F, 0.0F);
					} else {
						GL11.glScalef(0.5F, 0.5F, 0.5F);
					}

					for(int var23 = 0; var23 <= itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++var23) {
						this.random.setSeed(187L);
						Icon var22 = itemstack.getItem().getIcon(itemstack, var23);
						var16 = 1.0F;
						if (this.renderWithColor) {
							var26 = Item.itemsList[itemstack.itemID].getColorFromItemStack(itemstack, var23);
							var18 = (float)(var26 >> 16 & 255) / 255.0F;
							var19 = (float)(var26 >> 8 & 255) / 255.0F;
							var20 = (float)(var26 & 255) / 255.0F;
							GL11.glColor4f(var18 * var16, var19 * var16, var20 * var16, 1.0F);
							this.renderDroppedItem(par1EntityItem, var22, var13, par9, var18 * var16, var19 * var16, var20 * var16, var23);
						} else {
							this.renderDroppedItem(par1EntityItem, var22, var13, par9, 1.0F, 1.0F, 1.0F, var23);
						}
					}
				} else {
					if (renderInFrame) {
						GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
						GL11.glTranslatef(0.0F, -0.05F, 0.0F);
					} else {
						GL11.glScalef(0.5F, 0.5F, 0.5F);
					}

					Icon var14 = itemstack.getIconIndex();
					if (this.renderWithColor) {
						var15 = Item.itemsList[itemstack.itemID].getColorFromItemStack(itemstack, 0);
						var16 = (float)(var15 >> 16 & 255) / 255.0F;
						float var17 = (float)(var15 >> 8 & 255) / 255.0F;
						var18 = (float)(var15 & 255) / 255.0F;
						var19 = 1.0F;
						this.renderDroppedItem(par1EntityItem, var14, var13, par9, var16 * var19, var17 * var19, var18 * var19);
					} else {
						this.renderDroppedItem(par1EntityItem, var14, var13, par9, 1.0F, 1.0F, 1.0F);
					}
				}
			}

			GL11.glDisable(32826);
			GL11.glPopMatrix();
		}

	}

	protected ResourceLocation func_110796_a(EntityItem par1EntityItem) {
		return this.renderManager.renderEngine.getResourceLocation(par1EntityItem.getEntityItem().getItemSpriteNumber());
	}

	private void renderDroppedItem(EntityItem par1EntityItem, Icon par2Icon, int par3, float par4, float par5, float par6, float par7)
	{
		renderDroppedItem(par1EntityItem, par2Icon, par3, par4, par5, par6, par7, 0);
	}

	private void renderDroppedItem(EntityItem par1EntityItem, Icon par2Icon, int par3, float par4, float par5, float par6, float par7, int pass)
	{
		Tessellator var8 = Tessellator.instance;
		if (par2Icon == null) {
			TextureManager var9 = Minecraft.getMinecraft().getTextureManager();
			ResourceLocation var10 = var9.getResourceLocation(par1EntityItem.getEntityItem().getItemSpriteNumber());
			par2Icon = ((TextureMap)var9.getTexture(var10)).getAtlasSprite("missingno");
		}

		float var25 = ((Icon)par2Icon).getMinU();
		float var26 = ((Icon)par2Icon).getMaxU();
		float var11 = ((Icon)par2Icon).getMinV();
		float var12 = ((Icon)par2Icon).getMaxV();
		float var13 = 1.0F;
		float var14 = 0.5F;
		float var15 = 0.25F;
		float var17;
		if (this.renderManager.options.isFancyGraphicsEnabled()) {
			GL11.glPushMatrix();
			if (renderInFrame) {
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			} else {
				GL11.glRotatef((((float)par1EntityItem.age + par4) / 20.0F + par1EntityItem.hoverStart) * 57.295776F, 0.0F, 1.0F, 0.0F);
			}

			float var16 = 0.0625F;
			var17 = 0.021875F;
			ItemStack var18 = par1EntityItem.getEntityItem();
			int var19 = var18.stackSize;
			byte var24 = getMiniItemCount(var18);

			GL11.glTranslatef(-var14, -var15, -((var16 + var17) * (float)var24 / 2.0F));

			for(int var20 = 0; var20 < var24; ++var20) {
				// Makes items offset when in 3D, like when in 2D, looks much better. Considered a vanilla bug...
				if (var20 > 0 && shouldSpreadItems()) {
					float x = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
					float y = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
					float z = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
					GL11.glTranslatef(x, y, var16 + var17);
				} else {
					GL11.glTranslatef(0f, 0f, var16 + var17);
				}

				if (var18.getItemSpriteNumber() == 0) {
					this.bindTexture(TextureMap.locationBlocksTexture);
				} else {
					this.bindTexture(TextureMap.locationItemsTexture);
				}

				GL11.glColor4f(par5, par6, par7, 1.0F);
				ItemRenderer.renderItemIn2D(var8, var26, var11, var25, var12, ((Icon)par2Icon).getIconWidth(), ((Icon)par2Icon).getIconHeight(), var16);
				if (var18.hasEffect(pass)) {
					GL11.glDepthFunc(514);
					GL11.glDisable(2896);
					this.renderManager.renderEngine.bindTexture(RES_ITEM_GLINT);
					GL11.glEnable(3042);
					GL11.glBlendFunc(768, 1);
					float var21 = 0.76F;
					GL11.glColor4f(0.5F * var21, 0.25F * var21, 0.8F * var21, 1.0F);
					GL11.glMatrixMode(5890);
					GL11.glPushMatrix();
					float var22 = 0.125F;
					GL11.glScalef(var22, var22, var22);
					float var23 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
					GL11.glTranslatef(var23, 0.0F, 0.0F);
					GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
					ItemRenderer.renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, var16);
					GL11.glPopMatrix();
					GL11.glPushMatrix();
					GL11.glScalef(var22, var22, var22);
					var23 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
					GL11.glTranslatef(-var23, 0.0F, 0.0F);
					GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
					ItemRenderer.renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, var16);
					GL11.glPopMatrix();
					GL11.glMatrixMode(5888);
					GL11.glDisable(3042);
					GL11.glEnable(2896);
					GL11.glDepthFunc(515);
				}
			}

			GL11.glPopMatrix();
		} else {
			for(int var27 = 0; var27 < par3; ++var27) {
				GL11.glPushMatrix();
				if (var27 > 0) {
					var17 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					float var29 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					float var28 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					GL11.glTranslatef(var17, var29, var28);
				}

				if (!renderInFrame) {
					GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				}

				GL11.glColor4f(par5, par6, par7, 1.0F);
				var8.startDrawingQuads();
				var8.setNormal(0.0F, 1.0F, 0.0F);
				var8.addVertexWithUV((double)(0.0F - var14), (double)(0.0F - var15), 0.0, (double)var25, (double)var12);
				var8.addVertexWithUV((double)(var13 - var14), (double)(0.0F - var15), 0.0, (double)var26, (double)var12);
				var8.addVertexWithUV((double)(var13 - var14), (double)(1.0F - var15), 0.0, (double)var26, (double)var11);
				var8.addVertexWithUV((double)(0.0F - var14), (double)(1.0F - var15), 0.0, (double)var25, (double)var11);
				var8.draw();
				GL11.glPopMatrix();
			}
		}

	}
	public void renderItemIntoGUI(FontRenderer par1FontRenderer, TextureManager par2TextureManager, ItemStack par3ItemStack, int par4, int par5)
	{
		renderItemIntoGUI(par1FontRenderer, par2TextureManager, par3ItemStack, par4, par5, false);
	}

	public void renderItemIntoGUI(FontRenderer par1FontRenderer, TextureManager par2TextureManager, ItemStack par3ItemStack, int par4, int par5, boolean renderEffect)
	{
		int var6 = par3ItemStack.itemID;
		int var7 = par3ItemStack.getItemSubtype();
		Object var8 = par3ItemStack.getIconIndex();
		float var17;
		int var18;
		float var12;
		float var13;
		Block block = (var6 < Block.blocksList.length ? Block.blocksList[var6] : null);
		if (par3ItemStack.getItemSpriteNumber() == 0 && block != null && RenderBlocks.renderItemIn3d(Block.blocksList[var6].getRenderType()))
		{
			par2TextureManager.bindTexture(TextureMap.locationBlocksTexture);
			GL11.glPushMatrix();
			GL11.glTranslatef((float)(par4 - 2), (float)(par5 + 3), -3.0F + this.zLevel);
			GL11.glScalef(10.0F, 10.0F, 10.0F);
			GL11.glTranslatef(1.0F, 0.5F, 1.0F);
			GL11.glScalef(1.0F, 1.0F, -1.0F);
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			var18 = Item.itemsList[var6].getColorFromItemStack(par3ItemStack, 0);
			var17 = (float)(var18 >> 16 & 255) / 255.0F;
			var12 = (float)(var18 >> 8 & 255) / 255.0F;
			var13 = (float)(var18 & 255) / 255.0F;
			if (this.renderWithColor) {
				GL11.glColor4f(var17, var12, var13, 1.0F);
			}

			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			this.itemRenderBlocks.useInventoryTint = this.renderWithColor;
			this.itemRenderBlocks.renderBlockAsItem(block, var7, 1.0F);
			this.itemRenderBlocks.useInventoryTint = true;
			GL11.glPopMatrix();
		} else if (Item.itemsList[var6].requiresMultipleRenderPasses()) {
			GL11.glDisable(2896);

			for (int var9 = 0; var9 < Item.itemsList[var6].getRenderPasses(var6); ++var9) {
				par2TextureManager.bindTexture(par3ItemStack.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
				Icon var10 = Item.itemsList[var6].getIcon(par3ItemStack, var9);
				int var11 = Item.itemsList[var6].getColorFromItemStack(par3ItemStack, var9);
				var12 = (float)(var11 >> 16 & 255) / 255.0F;
				var13 = (float)(var11 >> 8 & 255) / 255.0F;
				float var14 = (float)(var11 & 255) / 255.0F;
				if (this.renderWithColor) {
					GL11.glColor4f(var12, var13, var14, 1.0F);
				}

				this.renderIcon(par4, par5, var10, 16, 16);

				if (par3ItemStack.hasEffect(var9))
				{
					renderEffect(par2TextureManager, par4, par5);
				}
			}

			GL11.glEnable(2896);
		} else {
			GL11.glDisable(2896);
			ResourceLocation var16 = par2TextureManager.getResourceLocation(par3ItemStack.getItemSpriteNumber());
			par2TextureManager.bindTexture(var16);
			if (var8 == null) {
				var8 = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(var16)).getAtlasSprite("missingno");
			}

			var18 = Item.itemsList[var6].getColorFromItemStack(par3ItemStack, 0);
			var17 = (float)(var18 >> 16 & 255) / 255.0F;
			var12 = (float)(var18 >> 8 & 255) / 255.0F;
			var13 = (float)(var18 & 255) / 255.0F;
			if (this.renderWithColor) {
				GL11.glColor4f(var17, var12, var13, 1.0F);
			}

			this.renderIcon(par4, par5, (Icon)var8, 16, 16);
			GL11.glEnable(2896);
		}

		GL11.glEnable(2884);
	}

	private void renderEffect(TextureManager manager, int x, int y)
	{
		GL11.glDepthFunc(GL11.GL_GREATER);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		manager.bindTexture(RES_ITEM_GLINT);
		this.zLevel -= 50.0F;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
		GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
		this.renderGlint(x * 431278612 + y * 32178161, x - 2, y - 2, 20, 20);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		this.zLevel += 50.0F;
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
	}

	public void renderItemAndEffectIntoGUI(FontRenderer par1FontRenderer, TextureManager par2TextureManager, ItemStack par3ItemStack, int par4, int par5) {
		if (par3ItemStack != null) {
			if (!ForgeHooksClient.renderInventoryItem(renderBlocks, par2TextureManager, par3ItemStack, renderWithColor, zLevel, (float)par4, (float)par5))
			{
				this.renderItemIntoGUI(par1FontRenderer, par2TextureManager, par3ItemStack, par4, par5, true);
			}

            /* Modders must handle this themselves if they use custom renderers!
			if (par3ItemStack.hasEffect()) {
				GL11.glDepthFunc(516);
				GL11.glDisable(2896);
				GL11.glDepthMask(false);
				par2TextureManager.bindTexture(RES_ITEM_GLINT);
				this.zLevel -= 50.0F;
				GL11.glEnable(3042);
				GL11.glBlendFunc(774, 774);
				GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
				this.renderGlint(par4 * 431278612 + par5 * 32178161, par4 - 2, par5 - 2, 20, 20);
				GL11.glDisable(3042);
				GL11.glDepthMask(true);
				this.zLevel += 50.0F;
				GL11.glEnable(2896);
				GL11.glDepthFunc(515);
			}

             */
		}

	}

	private void renderGlint(int par1, int par2, int par3, int par4, int par5) {
		for(int var6 = 0; var6 < 2; ++var6) {
			if (var6 == 0) {
				GL11.glBlendFunc(768, 1);
			}

			if (var6 == 1) {
				GL11.glBlendFunc(768, 1);
			}

			float var7 = 0.00390625F;
			float var8 = 0.00390625F;
			float var9 = (float)(Minecraft.getSystemTime() % (long)(3000 + var6 * 1873)) / (3000.0F + (float)(var6 * 1873)) * 256.0F;
			float var10 = 0.0F;
			Tessellator var11 = Tessellator.instance;
			float var12 = 4.0F;
			if (var6 == 1) {
				var12 = -1.0F;
			}

			var11.startDrawingQuads();
			var11.addVertexWithUV((double)(par2 + 0), (double)(par3 + par5), (double)this.zLevel, (double)((var9 + (float)par5 * var12) * var7), (double)((var10 + (float)par5) * var8));
			var11.addVertexWithUV((double)(par2 + par4), (double)(par3 + par5), (double)this.zLevel, (double)((var9 + (float)par4 + (float)par5 * var12) * var7), (double)((var10 + (float)par5) * var8));
			var11.addVertexWithUV((double)(par2 + par4), (double)(par3 + 0), (double)this.zLevel, (double)((var9 + (float)par4) * var7), (double)((var10 + 0.0F) * var8));
			var11.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)this.zLevel, (double)((var9 + 0.0F) * var7), (double)((var10 + 0.0F) * var8));
			var11.draw();
		}

	}

	public void renderItemOverlayIntoGUI(FontRenderer par1FontRenderer, TextureManager par2TextureManager, ItemStack par3ItemStack, int par4, int par5) {
		this.renderItemOverlayIntoGUI(par1FontRenderer, par2TextureManager, par3ItemStack, par4, par5, (String)null);
	}

	public void renderItemOverlayIntoGUI(FontRenderer par1FontRenderer, TextureManager par2TextureManager, ItemStack par3ItemStack, int par4, int par5, String par6Str) {
		if (par3ItemStack != null) {
			if (par3ItemStack.stackSize > 1 || par6Str != null) {
				String var7 = par6Str == null ? String.valueOf(par3ItemStack.stackSize) : par6Str;
				GL11.glDisable(2896);
				GL11.glDisable(2929);
				par1FontRenderer.drawStringWithShadow(var7, par4 + 19 - 2 - par1FontRenderer.getStringWidth(var7), par5 + 6 + 3, 16777215);
				GL11.glEnable(2896);
				GL11.glEnable(2929);
			}

			if (par3ItemStack.isItemDamaged()) {
				int var12 = (int)Math.round(13.0 - (double)par3ItemStack.getItemDamageForDisplay() * 13.0 / (double)par3ItemStack.getMaxDamage());
				int var8 = (int)Math.round(255.0 - (double)par3ItemStack.getItemDamageForDisplay() * 255.0 / (double)par3ItemStack.getMaxDamage());
				GL11.glDisable(2896);
				GL11.glDisable(2929);
				GL11.glDisable(3553);
				Tessellator var9 = Tessellator.instance;
				int var10 = 255 - var8 << 16 | var8 << 8;
				int var11 = (255 - var8) / 4 << 16 | 16128;
				this.renderQuad(var9, par4 + 2, par5 + 13, 13, 2, 0);
				this.renderQuad(var9, par4 + 2, par5 + 13, 12, 1, var11);
				this.renderQuad(var9, par4 + 2, par5 + 13, var12, 1, var10);
				GL11.glEnable(3553);
				GL11.glEnable(2896);
				GL11.glEnable(2929);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}

	}

	private void renderQuad(Tessellator par1Tessellator, int par2, int par3, int par4, int par5, int par6) {
		par1Tessellator.startDrawingQuads();
		par1Tessellator.setColorOpaque_I(par6);
		par1Tessellator.addVertex((double)(par2 + 0), (double)(par3 + 0), 0.0);
		par1Tessellator.addVertex((double)(par2 + 0), (double)(par3 + par5), 0.0);
		par1Tessellator.addVertex((double)(par2 + par4), (double)(par3 + par5), 0.0);
		par1Tessellator.addVertex((double)(par2 + par4), (double)(par3 + 0), 0.0);
		par1Tessellator.draw();
	}

	public void renderIcon(int par1, int par2, Icon par3Icon, int par4, int par5) {
		Tessellator var6 = Tessellator.instance;
		var6.startDrawingQuads();
		var6.addVertexWithUV((double)(par1 + 0), (double)(par2 + par5), (double)this.zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMaxV());
		var6.addVertexWithUV((double)(par1 + par4), (double)(par2 + par5), (double)this.zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMaxV());
		var6.addVertexWithUV((double)(par1 + par4), (double)(par2 + 0), (double)this.zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMinV());
		var6.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMinV());
		var6.draw();
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.func_110796_a((EntityItem)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderItem((EntityItem)par1Entity, par2, par4, par6, par8, par9);
	}

	/**
	 * Items should spread out when rendered in 3d?
	 * @return
	 */
	public boolean shouldSpreadItems()
	{
		return true;
	}

	/**
	 * Items should have a bob effect
	 * @return
	 */
	public boolean shouldBob()
	{
		return true;
	}

	public byte getMiniBlockCount(ItemStack stack)
	{
		byte ret = 1;
		if (stack.stackSize > 1 ) ret = 2;
		if (stack.stackSize > 5 ) ret = 3;
		if (stack.stackSize > 20) ret = 4;
		if (stack.stackSize > 40) ret = 5;
		return ret;
	}

	/**
	 * Allows for a subclass to override how many rendered items appear in a
	 * "mini item 3d stack"
	 * @param stack
	 * @return
	 */
	public byte getMiniItemCount(ItemStack stack)
	{
		byte ret = 1;
		if (stack.stackSize > 1) ret = 2;
		if (stack.stackSize > 15) ret = 3;
		if (stack.stackSize > 31) ret = 4;
		return ret;
	}
}
