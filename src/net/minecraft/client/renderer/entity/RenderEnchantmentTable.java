package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEnchantmentTable extends TileEntitySpecialRenderer {
	private static final ResourceLocation enchantingTableBookTextures = new ResourceLocation("textures/entity/enchanting_table_book.png");
	private ModelBook enchantmentBook = new ModelBook();

	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderTileEntityEnchantmentTableAt((TileEntityEnchantmentTable)par1TileEntity, par2, par4, par6, par8);
	}

	public void renderTileEntityEnchantmentTableAt(TileEntityEnchantmentTable tileEntityEnchantmentTable, double d, double e, double f, float g) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d + 0.5F, (float)e + 0.75F, (float)f + 0.5F);
		float var9 = (float)tileEntityEnchantmentTable.tickCount + g;
		GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(var9 * 0.1F) * 0.01F, 0.0F);

		float var10;
		for(var10 = tileEntityEnchantmentTable.bookRotation2 - tileEntityEnchantmentTable.bookRotationPrev; var10 >= 3.1415927F; var10 -= 6.2831855F) {
		}

		while(var10 < -3.1415927F) {
			var10 += 6.2831855F;
		}

		float var11 = tileEntityEnchantmentTable.bookRotationPrev + var10 * g;
		GL11.glRotatef(-var11 * 180.0F / 3.1415927F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
		this.bindTexture(enchantingTableBookTextures);
		float var12 = tileEntityEnchantmentTable.pageFlipPrev + (tileEntityEnchantmentTable.pageFlip - tileEntityEnchantmentTable.pageFlipPrev) * g + 0.25F;
		float var13 = tileEntityEnchantmentTable.pageFlipPrev + (tileEntityEnchantmentTable.pageFlip - tileEntityEnchantmentTable.pageFlipPrev) * g + 0.75F;
		var12 = (var12 - (float)MathHelper.truncateDoubleToInt((double)var12)) * 1.6F - 0.3F;
		var13 = (var13 - (float)MathHelper.truncateDoubleToInt((double)var13)) * 1.6F - 0.3F;
		if (var12 < 0.0F) {
			var12 = 0.0F;
		}

		if (var13 < 0.0F) {
			var13 = 0.0F;
		}

		if (var12 > 1.0F) {
			var12 = 1.0F;
		}

		if (var13 > 1.0F) {
			var13 = 1.0F;
		}

		float var14 = tileEntityEnchantmentTable.bookSpreadPrev + (tileEntityEnchantmentTable.bookSpread - tileEntityEnchantmentTable.bookSpreadPrev) * g;
		GL11.glEnable(2884);
		this.enchantmentBook.render((Entity)null, var9, var12, var13, var14, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}
}
