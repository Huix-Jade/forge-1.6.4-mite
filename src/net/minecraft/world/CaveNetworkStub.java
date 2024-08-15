package net.minecraft.world;

import net.minecraft.util.StringHelper;
import net.minecraft.world.chunk.Chunk;

public final class CaveNetworkStub {
   private final int origin_chunk_x;
   private final int origin_chunk_z;
   private final int size_x;
   private final int size_y;
   private final int size_z;
   private final long seed;
   private long legacy_seed;
   private final boolean has_mycelium;
   private final boolean allows_water;
   private final boolean allows_lava;

   public CaveNetworkStub(int origin_chunk_x, int origin_chunk_z, int size_x, int size_y, int size_z, long seed, boolean has_mycelium, boolean allows_water, boolean allows_lava) {
      this.origin_chunk_x = origin_chunk_x;
      this.origin_chunk_z = origin_chunk_z;
      this.size_x = size_x;
      this.size_y = size_y;
      this.size_z = size_z;
      this.seed = seed;
      if (seed == 2617667064333438329L && origin_chunk_x == -14 && origin_chunk_z == 29) {
         has_mycelium = true;
         this.setLegacySeed(2375913967323326907L);
      }

      this.has_mycelium = has_mycelium;
      if (has_mycelium) {
         allows_water = false;
         allows_lava = false;
      }

      this.allows_water = allows_water;
      this.allows_lava = allows_lava;
   }

   public CaveNetworkStub setLegacySeed(long legacy_seed) {
      this.legacy_seed = legacy_seed;
      return this;
   }

   public boolean hasLegacySeed() {
      return this.getLegacySeed() != 0L;
   }

   public long getLegacySeed() {
      return this.legacy_seed;
   }

   public int getOriginChunkX() {
      return this.origin_chunk_x;
   }

   public int getOriginChunkZ() {
      return this.origin_chunk_z;
   }

   public int getOriginChunkCoordsHash() {
      return Chunk.getChunkCoordsHash(this.getOriginChunkX(), this.getOriginChunkZ());
   }

   public int getSizeX() {
      return this.size_x;
   }

   public int getSizeY() {
      return this.size_y;
   }

   public int getSizeZ() {
      return this.size_z;
   }

   public long getSeed() {
      return this.seed;
   }

   public boolean hasMycelium() {
      return this.has_mycelium;
   }

   public boolean allowsWater() {
      return this.allows_water;
   }

   public boolean allowsLava() {
      return this.allows_lava;
   }

   public boolean preventsAllLiquids() {
      return !this.allowsWater() && !this.allowsLava();
   }

   public String toString() {
      return "CN Stub: [" + this.origin_chunk_x + "," + this.origin_chunk_z + "], M=" + StringHelper.getBooleanAsLetter(this.hasMycelium()) + ", W=" + StringHelper.getBooleanAsLetter(this.allowsWater()) + ", L=" + StringHelper.getBooleanAsLetter(this.allowsLava()) + ", seed=" + this.seed + (this.hasLegacySeed() ? " (legacy)" : "");
   }
}
