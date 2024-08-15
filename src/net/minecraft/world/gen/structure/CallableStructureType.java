package net.minecraft.world.gen.structure;

import java.util.concurrent.Callable;

class CallableStructureType implements Callable {
   // $FF: synthetic field
   final MapGenStructure theMapStructureGenerator;

   CallableStructureType(MapGenStructure var1) {
      this.theMapStructureGenerator = var1;
   }

   public String callStructureType() {
      return this.theMapStructureGenerator.getClass().getCanonicalName();
   }

   // $FF: synthetic method
   public Object call() {
      return this.callStructureType();
   }
}
