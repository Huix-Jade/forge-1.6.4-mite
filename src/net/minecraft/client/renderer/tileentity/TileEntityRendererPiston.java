package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererPiston extends TileEntitySpecialRenderer {
	private RenderBlocks blockRenderer;

	public void renderPiston(TileEntityPiston par1TileEntityPiston, double par2, double par4, double par6, float par8) {
		Block var9 = Block.blocksList[par1TileEntityPiston.getStoredBlockID()];
		if (var9 != null && par1TileEntityPiston.getProgress(par8) < 1.0F) {
			Tessellator var10 = Tessellator.instance;
			this.bindTexture(TextureMap.locationBlocksTexture);
			RenderHelper.disableStandardItemLighting();
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(3042);
			GL11.glDisable(2884);
			if (Minecraft.isAmbientOcclusionEnabled()) {
				GL11.glShadeModel(7425);
			} else {
				GL11.glShadeModel(7424);
			}

			var10.startDrawingQuads();
			var10.setTranslation((double)((float)par2 - (float)par1TileEntityPiston.xCoord + par1TileEntityPiston.getOffsetX(par8)), (double)((float)par4 - (float)par1TileEntityPiston.yCoord + par1TileEntityPiston.getOffsetY(par8)), (double)((float)par6 - (float)par1TileEntityPiston.zCoord + par1TileEntityPiston.getOffsetZ(par8)));
			var10.setColorRGBA(1, 1, 1, 255);
			if (var9 == Block.pistonExtension && par1TileEntityPiston.getProgress(par8) < 0.5F) {
				this.blockRenderer.renderPistonExtensionAllFaces(var9, par1TileEntityPiston.xCoord, par1TileEntityPiston.yCoord, par1TileEntityPiston.zCoord, false);
			} else if (par1TileEntityPiston.shouldRenderHead() && !par1TileEntityPiston.isExtending()) {
				Block.pistonExtension.setHeadTexture(((BlockPistonBase)var9).getPistonExtensionTexture());
				this.blockRenderer.renderPistonExtensionAllFaces(Block.pistonExtension, par1TileEntityPiston.xCoord, par1TileEntityPiston.yCoord, par1TileEntityPiston.zCoord, par1TileEntityPiston.getProgress(par8) < 0.5F);
				Block.pistonExtension.clearHeadTexture();
				var10.setTranslation((double)((float)par2 - (float)par1TileEntityPiston.xCoord), (double)((float)par4 - (float)par1TileEntityPiston.yCoord), (double)((float)par6 - (float)par1TileEntityPiston.zCoord));
				this.blockRenderer.renderPistonBaseAllFaces(var9, par1TileEntityPiston.xCoord, par1TileEntityPiston.yCoord, par1TileEntityPiston.zCoord);
			} else {
				this.blockRenderer.renderBlockAllFaces(var9, par1TileEntityPiston.xCoord, par1TileEntityPiston.yCoord, par1TileEntityPiston.zCoord);
			}

			var10.setTranslation(0.0, 0.0, 0.0);
			var10.draw();
			RenderHelper.enableStandardItemLighting();
		}

	}

	public void onWorldChange(World par1World) {
		this.blockRenderer = new RenderBlocks(par1World);
	}

	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
		this.renderPiston((TileEntityPiston)par1TileEntity, par2, par4, par6, par8);
	}
}
