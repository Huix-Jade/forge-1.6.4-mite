package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockStrongbox;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumChestType;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityChestRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation RES_TRAPPED_DOUBLE = new ResourceLocation("textures/entity/chest/trapped_double.png");
	private static final ResourceLocation RES_CHRISTMAS_DOUBLE = new ResourceLocation("textures/entity/chest/christmas_double.png");
	private static final ResourceLocation RES_NORMAL_DOUBLE = new ResourceLocation("textures/entity/chest/normal_double.png");
	private static final ResourceLocation RES_TRAPPED_SINGLE = new ResourceLocation("textures/entity/chest/trapped.png");
	private static final ResourceLocation RES_CHRISTMAS_SINGLE = new ResourceLocation("textures/entity/chest/christmas.png");
	private static final ResourceLocation RES_NORMAL_SINGLE = new ResourceLocation("textures/entity/chest/normal.png");
	private static final ResourceLocation RES_COPPER_SINGLE = new ResourceLocation("textures/entity/chest/copper_single.png");
	private static final ResourceLocation RES_SILVER_SINGLE = new ResourceLocation("textures/entity/chest/silver_single.png");
	private static final ResourceLocation RES_GOLD_SINGLE = new ResourceLocation("textures/entity/chest/gold_single.png");
	private static final ResourceLocation RES_IRON_SINGLE = new ResourceLocation("textures/entity/chest/iron_single.png");
	private static final ResourceLocation RES_MITHRIL_SINGLE = new ResourceLocation("textures/entity/chest/mithril_single.png");
	private static final ResourceLocation RES_ADAMANTIUM_SINGLE = new ResourceLocation("textures/entity/chest/adamantium_single.png");
	private static final ResourceLocation RES_ANCIENT_METAL_SINGLE = new ResourceLocation("textures/entity/chest/ancient_metal_single.png");
	private ModelChest chestModel = new ModelChest();
	private ModelChest largeChestModel = new ModelLargeChest();
	private boolean isChristmas;

	public TileEntityChestRenderer() {
		Calendar var1 = Calendar.getInstance();
		if (var1.get(2) + 1 == 12 && var1.get(5) >= 24 && var1.get(5) <= 26) {
			this.isChristmas = true;
		}

	}

	public void renderTileEntityChestAt(TileEntityChest par1TileEntityChest, double par2, double par4, double par6, float par8) {
		if (!par1TileEntityChest.hasWorldObj() || par1TileEntityChest.getBlockType() != null) {
			int var9;
			if (!par1TileEntityChest.hasWorldObj()) {
				var9 = 0;
			} else {
				Block var10 = par1TileEntityChest.getBlockType();
				var9 = par1TileEntityChest.getBlockMetadata();
				if (var10 instanceof BlockChest && var9 == 0 && !(var10 instanceof BlockStrongbox)) {
					try
					{
						((BlockChest)var10).tryAlignWithNeighboringChest(par1TileEntityChest.getWorldObj(), par1TileEntityChest.xCoord, par1TileEntityChest.yCoord, par1TileEntityChest.zCoord);
					}
					catch (ClassCastException e)
					{
						FMLLog.severe("Attempted to render a chest at %d,  %d, %d that was not a chest",
								par1TileEntityChest.xCoord, par1TileEntityChest.yCoord, par1TileEntityChest.zCoord);
					}

					var9 = par1TileEntityChest.getBlockMetadata();
				}

				par1TileEntityChest.checkForAdjacentChests();
			}

			if (par1TileEntityChest.adjacentChestZNeg == null && par1TileEntityChest.adjacentChestXNeg == null) {
				ModelChest var14;
				if (par1TileEntityChest.adjacentChestXPos == null && par1TileEntityChest.adjacentChestZPosition == null) {
					var14 = this.chestModel;
					if (par1TileEntityChest.getChestType() == EnumChestType.trapped) {
						this.bindTexture(RES_TRAPPED_SINGLE);
					} else {
						Material material = par1TileEntityChest.getBlockMaterial();
						if (material == Material.copper) {
							this.bindTexture(RES_COPPER_SINGLE);
						} else if (material == Material.silver) {
							this.bindTexture(RES_SILVER_SINGLE);
						} else if (material == Material.gold) {
							this.bindTexture(RES_GOLD_SINGLE);
						} else if (material == Material.iron) {
							this.bindTexture(RES_IRON_SINGLE);
						} else if (material == Material.mithril) {
							this.bindTexture(RES_MITHRIL_SINGLE);
						} else if (material == Material.adamantium) {
							this.bindTexture(RES_ADAMANTIUM_SINGLE);
						} else if (material == Material.ancient_metal) {
							this.bindTexture(RES_ANCIENT_METAL_SINGLE);
						} else {
							this.bindTexture(this.isChristmas ? RES_CHRISTMAS_SINGLE : RES_NORMAL_SINGLE);
						}
					}
				} else {
					var14 = this.largeChestModel;
					if (par1TileEntityChest.getChestType() == EnumChestType.trapped) {
						this.bindTexture(RES_TRAPPED_DOUBLE);
					} else if (this.isChristmas) {
						this.bindTexture(RES_CHRISTMAS_DOUBLE);
					} else {
						this.bindTexture(RES_NORMAL_DOUBLE);
					}
				}

				GL11.glPushMatrix();
				GL11.glEnable(32826);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glTranslatef((float)par2, (float)par4 + 1.0F, (float)par6 + 1.0F);
				GL11.glScalef(1.0F, -1.0F, -1.0F);
				GL11.glTranslatef(0.5F, 0.5F, 0.5F);
				short var11 = 0;
				if (var9 == 2) {
					var11 = 180;
				}

				if (var9 == 3) {
					var11 = 0;
				}

				if (var9 == 4) {
					var11 = 90;
				}

				if (var9 == 5) {
					var11 = -90;
				}

				if (var9 == 2 && par1TileEntityChest.adjacentChestXPos != null) {
					GL11.glTranslatef(1.0F, 0.0F, 0.0F);
				}

				if (var9 == 5 && par1TileEntityChest.adjacentChestZPosition != null) {
					GL11.glTranslatef(0.0F, 0.0F, -1.0F);
				}

				GL11.glRotatef((float)var11, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
				float var12 = par1TileEntityChest.prevLidAngle + (par1TileEntityChest.lidAngle - par1TileEntityChest.prevLidAngle) * par8;
				float var13;
				if (par1TileEntityChest.adjacentChestZNeg != null) {
					var13 = par1TileEntityChest.adjacentChestZNeg.prevLidAngle + (par1TileEntityChest.adjacentChestZNeg.lidAngle - par1TileEntityChest.adjacentChestZNeg.prevLidAngle) * par8;
					if (var13 > var12) {
						var12 = var13;
					}
				}

				if (par1TileEntityChest.adjacentChestXNeg != null) {
					var13 = par1TileEntityChest.adjacentChestXNeg.prevLidAngle + (par1TileEntityChest.adjacentChestXNeg.lidAngle - par1TileEntityChest.adjacentChestXNeg.prevLidAngle) * par8;
					if (var13 > var12) {
						var12 = var13;
					}
				}

				var12 = 1.0F - var12;
				var12 = 1.0F - var12 * var12 * var12;
				var14.chestLid.rotateAngleX = -(var12 * 3.1415927F / 2.0F);
				var14.renderAll();
				GL11.glDisable(32826);
				GL11.glPopMatrix();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

		}
	}

	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
		this.renderTileEntityChestAt((TileEntityChest)par1TileEntity, par2, par4, par6, par8);
	}
}
