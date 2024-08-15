package net.minecraft.world;

import java.util.concurrent.Callable;

class CallableLvl3 implements Callable {
   // $FF: synthetic field
   final World theWorld;

   CallableLvl3(World var1) {
      this.theWorld = var1;
   }

   public String getChunkProvider() {
      return this.theWorld.chunkProvider.makeString();
   }

   // $FF: synthetic method
   public Object call() {
      return this.getChunkProvider();
   }
}
