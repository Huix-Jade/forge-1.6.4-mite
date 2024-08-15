package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class TileEntitySpecialRenderer {
	protected TileEntityRenderer tileEntityRenderer;

	public abstract void renderTileEntityAt(TileEntity tileEntity, double d, double e, double f, float g);

	protected void bindTexture(ResourceLocation resourceLocation) {
		TextureManager var2 = this.tileEntityRenderer.renderEngine;
		if (var2 != null) {
			var2.bindTexture(resourceLocation);
		}

	}

	public void setTileEntityRenderer(TileEntityRenderer tileEntityRenderer) {
		this.tileEntityRenderer = tileEntityRenderer;
	}

	public void onWorldChange(World world) {
	}

	public FontRenderer getFontRenderer() {
		return this.tileEntityRenderer.getFontRenderer();
	}
}
