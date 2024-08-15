package net.minecraft.world.gen.structure;

import java.util.concurrent.Callable;
import net.minecraft.world.ChunkCoordIntPair;

class CallableChunkPosHash implements Callable {
   // $FF: synthetic field
   final int field_85165_a;
   // $FF: synthetic field
   final int field_85163_b;
   // $FF: synthetic field
   final MapGenStructure theMapStructureGenerator;

   CallableChunkPosHash(MapGenStructure var1, int var2, int var3) {
      this.theMapStructureGenerator = var1;
      this.field_85165_a = var2;
      this.field_85163_b = var3;
   }

   public String callChunkPositionHash() {
      return String.valueOf(ChunkCoordIntPair.chunkXZ2Int(this.field_85165_a, this.field_85163_b));
   }

   // $FF: synthetic method
   public Object call() {
      return this.callChunkPositionHash();
   }
}
