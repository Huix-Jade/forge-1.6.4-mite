package net.minecraft.tileentity;

import java.util.concurrent.Callable;

class CallableTileEntityName implements Callable {
   // $FF: synthetic field
   final TileEntity theTileEntity;

   CallableTileEntityName(TileEntity var1) {
      this.theTileEntity = var1;
   }

   public String callTileEntityName() {
      return (String)TileEntity.getClassToNameMap().get(this.theTileEntity.getClass()) + " // " + this.theTileEntity.getClass().getCanonicalName();
   }

   // $FF: synthetic method
   public Object call() {
      return this.callTileEntityName();
   }
}
