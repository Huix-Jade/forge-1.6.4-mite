package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityEnderChestRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation field_110637_a = new ResourceLocation("textures/entity/chest/ender.png");
	private ModelChest theEnderChestModel = new ModelChest();


	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderEnderChest((TileEntityEnderChest)par1TileEntity, par2, par4, par6, par8);
	}

	public void renderEnderChest(TileEntityEnderChest tileEntityEnderChest, double d, double e, double f, float g) {
		int var9 = 0;
		if (tileEntityEnderChest.hasWorldObj()) {
			var9 = tileEntityEnderChest.getBlockMetadata();
		}

		this.bindTexture(field_110637_a);
		GL11.glPushMatrix();
		GL11.glEnable(32826);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float)d, (float)e + 1.0F, (float)f + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		short var10 = 0;
		if (var9 == 2) {
			var10 = 180;
		}

		if (var9 == 3) {
			var10 = 0;
		}

		if (var9 == 4) {
			var10 = 90;
		}

		if (var9 == 5) {
			var10 = -90;
		}

		GL11.glRotatef((float)var10, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		float var11 = tileEntityEnderChest.prevLidAngle + (tileEntityEnderChest.lidAngle - tileEntityEnderChest.prevLidAngle) * g;
		var11 = 1.0F - var11;
		var11 = 1.0F - var11 * var11 * var11;
		this.theEnderChestModel.chestLid.rotateAngleX = -(var11 * 3.1415927F / 2.0F);
		this.theEnderChestModel.renderAll();
		GL11.glDisable(32826);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
