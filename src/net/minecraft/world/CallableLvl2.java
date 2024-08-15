package net.minecraft.world;

import java.util.concurrent.Callable;

class CallableLvl2 implements Callable {
   // $FF: synthetic field
   final World theWorld;

   CallableLvl2(World var1) {
      this.theWorld = var1;
   }

   public String getPlayerEntities() {
      return this.theWorld.playerEntities.size() + " total; " + this.theWorld.playerEntities.toString();
   }

   // $FF: synthetic method
   public Object call() {
      return this.getPlayerEntities();
   }
}
