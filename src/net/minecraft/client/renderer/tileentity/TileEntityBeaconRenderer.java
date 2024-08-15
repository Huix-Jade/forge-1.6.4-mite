package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation field_110629_a = new ResourceLocation("textures/entity/beacon_beam.png");

	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderTileEntityBeaconAt((TileEntityBeacon)par1TileEntity, par2, par4, par6, par8);
	}

	public void renderTileEntityBeaconAt(TileEntityBeacon tileEntityBeacon, double d, double e, double f, float g) {
		float var9 = tileEntityBeacon.func_82125_v_();
		if (var9 > 0.0F) {
			Tessellator var10 = Tessellator.instance;
			this.bindTexture(field_110629_a);
			GL11.glTexParameterf(3553, 10242, 10497.0F);
			GL11.glTexParameterf(3553, 10243, 10497.0F);
			GL11.glDisable(2896);
			GL11.glDisable(2884);
			GL11.glDisable(3042);
			GL11.glDepthMask(true);
			GL11.glBlendFunc(770, 1);
			float var11 = (float)tileEntityBeacon.getWorldObj().getTotalWorldTime() + g;
			float var12 = -var11 * 0.2F - (float)MathHelper.floor_float(-var11 * 0.1F);
			byte var13 = 1;
			double var14 = (double)var11 * 0.025 * (1.0 - (double)(var13 & 1) * 2.5);
			var10.startDrawingQuads();
			var10.setColorRGBA(255, 255, 255, 32);
			double var16 = (double)var13 * 0.2;
			double var18 = 0.5 + Math.cos(var14 + 2.356194490192345) * var16;
			double var20 = 0.5 + Math.sin(var14 + 2.356194490192345) * var16;
			double var22 = 0.5 + Math.cos(var14 + 0.7853981633974483) * var16;
			double var24 = 0.5 + Math.sin(var14 + 0.7853981633974483) * var16;
			double var26 = 0.5 + Math.cos(var14 + 3.9269908169872414) * var16;
			double var28 = 0.5 + Math.sin(var14 + 3.9269908169872414) * var16;
			double var30 = 0.5 + Math.cos(var14 + 5.497787143782138) * var16;
			double var32 = 0.5 + Math.sin(var14 + 5.497787143782138) * var16;
			double var34 = (double)(256.0F * var9);
			double var36 = 0.0;
			double var38 = 1.0;
			double var40 = (double)(-1.0F + var12);
			double var42 = (double)(256.0F * var9) * (0.5 / var16) + var40;
			var10.addVertexWithUV(d + var18, e + var34, f + var20, var38, var42);
			var10.addVertexWithUV(d + var18, e, f + var20, var38, var40);
			var10.addVertexWithUV(d + var22, e, f + var24, var36, var40);
			var10.addVertexWithUV(d + var22, e + var34, f + var24, var36, var42);
			var10.addVertexWithUV(d + var30, e + var34, f + var32, var38, var42);
			var10.addVertexWithUV(d + var30, e, f + var32, var38, var40);
			var10.addVertexWithUV(d + var26, e, f + var28, var36, var40);
			var10.addVertexWithUV(d + var26, e + var34, f + var28, var36, var42);
			var10.addVertexWithUV(d + var22, e + var34, f + var24, var38, var42);
			var10.addVertexWithUV(d + var22, e, f + var24, var38, var40);
			var10.addVertexWithUV(d + var30, e, f + var32, var36, var40);
			var10.addVertexWithUV(d + var30, e + var34, f + var32, var36, var42);
			var10.addVertexWithUV(d + var26, e + var34, f + var28, var38, var42);
			var10.addVertexWithUV(d + var26, e, f + var28, var38, var40);
			var10.addVertexWithUV(d + var18, e, f + var20, var36, var40);
			var10.addVertexWithUV(d + var18, e + var34, f + var20, var36, var42);
			var10.draw();
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glDepthMask(false);
			var10.startDrawingQuads();
			var10.setColorRGBA(255, 255, 255, 32);
			double var44 = 0.2;
			double var15 = 0.2;
			double var17 = 0.8;
			double var19 = 0.2;
			double var21 = 0.2;
			double var23 = 0.8;
			double var25 = 0.8;
			double var27 = 0.8;
			double var29 = (double)(256.0F * var9);
			double var31 = 0.0;
			double var33 = 1.0;
			double var35 = (double)(-1.0F + var12);
			double var37 = (double)(256.0F * var9) + var35;
			var10.addVertexWithUV(d + var44, e + var29, f + var15, var33, var37);
			var10.addVertexWithUV(d + var44, e, f + var15, var33, var35);
			var10.addVertexWithUV(d + var17, e, f + var19, var31, var35);
			var10.addVertexWithUV(d + var17, e + var29, f + var19, var31, var37);
			var10.addVertexWithUV(d + var25, e + var29, f + var27, var33, var37);
			var10.addVertexWithUV(d + var25, e, f + var27, var33, var35);
			var10.addVertexWithUV(d + var21, e, f + var23, var31, var35);
			var10.addVertexWithUV(d + var21, e + var29, f + var23, var31, var37);
			var10.addVertexWithUV(d + var17, e + var29, f + var19, var33, var37);
			var10.addVertexWithUV(d + var17, e, f + var19, var33, var35);
			var10.addVertexWithUV(d + var25, e, f + var27, var31, var35);
			var10.addVertexWithUV(d + var25, e + var29, f + var27, var31, var37);
			var10.addVertexWithUV(d + var21, e + var29, f + var23, var33, var37);
			var10.addVertexWithUV(d + var21, e, f + var23, var33, var35);
			var10.addVertexWithUV(d + var44, e, f + var15, var31, var35);
			var10.addVertexWithUV(d + var44, e + var29, f + var15, var31, var37);
			var10.draw();
			GL11.glEnable(2896);
			GL11.glEnable(3553);
			GL11.glDepthMask(true);
		}

	}
}
