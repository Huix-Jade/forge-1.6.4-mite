package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDaylightDetector;

public class TileEntityDaylightDetector extends TileEntity {
   public void updateEntity() {
      if (this.worldObj != null && !this.worldObj.isRemote && this.worldObj.getTotalWorldTime() % 20L == 0L) {
         Block block = this.getBlockType();
         if (block != null && block instanceof BlockDaylightDetector) {
            ((BlockDaylightDetector)block).updateLightLevel(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
         }
      }

   }
}
