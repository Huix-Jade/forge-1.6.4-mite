package net.minecraft.crash;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;

final class CallableBlockType implements Callable {
   // $FF: synthetic field
   final int blockID;

   CallableBlockType(int var1) {
      this.blockID = var1;
   }

   public String callBlockType() {
      try {
         return String.format("ID #%d (%s // %s)", this.blockID, Block.blocksList[this.blockID].getUnlocalizedName(), Block.blocksList[this.blockID].getClass().getCanonicalName());
      } catch (Throwable var2) {
         return "ID #" + this.blockID;
      }
   }

   // $FF: synthetic method
   public Object call() {
      return this.callBlockType();
   }
}
