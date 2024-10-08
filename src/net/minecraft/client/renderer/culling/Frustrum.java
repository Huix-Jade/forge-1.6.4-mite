package net.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public final class Frustrum implements ICamera {
   private ClippingHelper clippingHelper = ClippingHelperImpl.getInstance();
   private double xPosition;
   private double yPosition;
   private double zPosition;

   public void setPosition(double par1, double par3, double par5) {
      this.xPosition = par1;
      this.yPosition = par3;
      this.zPosition = par5;
   }

   public boolean isBoxInFrustum(double par1, double par3, double par5, double par7, double par9, double par11) {
      return this.clippingHelper.isBoxInFrustumMITE(par1 - this.xPosition, par3 - this.yPosition, par5 - this.zPosition, par7 - this.xPosition, par9 - this.yPosition, par11 - this.zPosition);
   }

   public boolean isBoundingBoxInFrustum(AxisAlignedBB par1AxisAlignedBB) {
      return this.isBoxInFrustum(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ, par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
   }
}
