package net.minecraft.tileentity;

import java.util.concurrent.Callable;

class CallableTileEntityData implements Callable {
   // $FF: synthetic field
   final TileEntity theTileEntity;

   CallableTileEntityData(TileEntity var1) {
      this.theTileEntity = var1;
   }

   public String callTileEntityDataInfo() {
      int var1 = this.theTileEntity.worldObj.getBlockMetadata(this.theTileEntity.xCoord, this.theTileEntity.yCoord, this.theTileEntity.zCoord);
      if (var1 < 0) {
         return "Unknown? (Got " + var1 + ")";
      } else {
         String var2 = String.format("%4s", Integer.toBinaryString(var1)).replace(" ", "0");
         return String.format("%1$d / 0x%1$X / 0b%2$s", var1, var2);
      }
   }

   // $FF: synthetic method
   public Object call() {
      return this.callTileEntityDataInfo();
   }
}
