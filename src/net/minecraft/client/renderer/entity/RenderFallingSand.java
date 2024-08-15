package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderFallingSand extends Render {
	private final RenderBlocks sandRenderBlocks = new RenderBlocks();

	public RenderFallingSand() {
		this.shadowSize = 0.5F;
	}

	public void doRenderFallingSand(EntityFallingSand par1EntityFallingSand, double par2, double par4, double par6, float par8, float par9) {
		World var10 = par1EntityFallingSand.worldObj;
		Block var11 = Block.blocksList[par1EntityFallingSand.blockID];
		if (var10.getBlockId(MathHelper.floor_double(par1EntityFallingSand.posX), MathHelper.floor_double(par1EntityFallingSand.posY), MathHelper.floor_double(par1EntityFallingSand.posZ)) != par1EntityFallingSand.blockID) {
			GL11.glPushMatrix();
			GL11.glTranslatef((float)par2, (float)par4, (float)par6);
			this.bindEntityTexture(par1EntityFallingSand);
			GL11.glDisable(2896);
			int x = par1EntityFallingSand.getBlockPosX();
			int y = par1EntityFallingSand.getBlockPosY();
			int z = par1EntityFallingSand.getBlockPosZ();
			int previous_metadata = var10.getBlockMetadata(x, y, z);
			var10.setBlockMetadataWithNotify(x, y, z, par1EntityFallingSand.metadata, 4);
			Tessellator var12;
			if (var11 instanceof BlockAnvil && var11.getRenderType() == 35) {
				this.sandRenderBlocks.blockAccess = var10;
				var12 = Tessellator.instance;
				var12.startDrawingQuads();
				var12.setTranslation((double)((float)(-MathHelper.floor_double(par1EntityFallingSand.posX)) - 0.5F), (double)((float)(-MathHelper.floor_double(par1EntityFallingSand.posY)) - 0.5F), (double)((float)(-MathHelper.floor_double(par1EntityFallingSand.posZ)) - 0.5F));
				this.sandRenderBlocks.renderBlockAnvilMetadata((BlockAnvil)var11, MathHelper.floor_double(par1EntityFallingSand.posX), MathHelper.floor_double(par1EntityFallingSand.posY), MathHelper.floor_double(par1EntityFallingSand.posZ), par1EntityFallingSand.metadata);
				var12.setTranslation(0.0, 0.0, 0.0);
				var12.draw();
			} else if (var11.getRenderType() == 27) {
				this.sandRenderBlocks.blockAccess = var10;
				var12 = Tessellator.instance;
				var12.startDrawingQuads();
				var12.setTranslation((double)((float)(-MathHelper.floor_double(par1EntityFallingSand.posX)) - 0.5F), (double)((float)(-MathHelper.floor_double(par1EntityFallingSand.posY)) - 0.5F), (double)((float)(-MathHelper.floor_double(par1EntityFallingSand.posZ)) - 0.5F));
				this.sandRenderBlocks.renderBlockDragonEgg((BlockDragonEgg)var11, MathHelper.floor_double(par1EntityFallingSand.posX), MathHelper.floor_double(par1EntityFallingSand.posY), MathHelper.floor_double(par1EntityFallingSand.posZ));
				var12.setTranslation(0.0, 0.0, 0.0);
				var12.draw();
			} else if (var11 != null) {
				if (var11.isAlwaysStandardFormCube()) {
					this.sandRenderBlocks.setRenderBoundsForStandardFormBlock();
				} else {
					this.sandRenderBlocks.setRenderBoundsForNonStandardFormBlock(var11);
				}

				this.sandRenderBlocks.renderBlockSandFalling(var11, var10, MathHelper.floor_double(par1EntityFallingSand.posX), MathHelper.floor_double(par1EntityFallingSand.posY), MathHelper.floor_double(par1EntityFallingSand.posZ), par1EntityFallingSand.metadata);
			}

			var10.setBlockMetadataWithNotify(x, y, z, previous_metadata, 4);
			GL11.glEnable(2896);
			GL11.glPopMatrix();
		}

	}

	protected ResourceLocation getFallingSandTextures(EntityFallingSand par1EntityFallingSand) {
		return TextureMap.locationBlocksTexture;
	}

	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return this.getFallingSandTextures((EntityFallingSand)par1Entity);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.doRenderFallingSand((EntityFallingSand)par1Entity, par2, par4, par6, par8, par9);
	}
}
