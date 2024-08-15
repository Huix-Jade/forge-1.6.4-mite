package net.minecraft.network;

import java.util.HashMap;
import java.util.Random;
import net.minecraft.util.Debug;
import net.minecraft.world.CaveNetworkGenerator;
import net.minecraft.world.CaveNetworkStub;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;

public class MapGenCaveNetwork extends MapGenBase {
   protected int a = 3;
   private CaveNetworkStub stub;
   public final HashMap cached_stubs = new HashMap();
   public final HashMap cached_cave_network_generators = new HashMap();

   public void generate(IChunkProvider chunk_provider, World world, int chunk_x, int chunk_z, byte[] block_ids) {
      CaveNetworkStub stub = this.getCaveNetworkStubAt(world, chunk_x, chunk_z);
      if (stub != null) {
         this.generateCaveNetwork(chunk_x, chunk_z, block_ids, stub);
      }
   }

   private void generateCaveNetwork(int chunk_x, int chunk_z, byte[] block_ids, CaveNetworkStub stub) {
      int hash = stub.getOriginChunkCoordsHash();
      CaveNetworkGenerator cg = (CaveNetworkGenerator)this.cached_cave_network_generators.get(hash);
      if (cg == null || cg.getOriginChunkX() != stub.getOriginChunkX() || cg.getOriginChunkZ() != stub.getOriginChunkZ()) {
         if (cg != null) {
            Debug.setErrorMessage("generateCaveNetwork: hash collision");
         }

         this.cached_cave_network_generators.put(hash, cg = new CaveNetworkGenerator(stub));
      }

      cg.apply(chunk_x, chunk_z, stub.getOriginChunkX(), stub.getOriginChunkZ(), block_ids);
   }

   private CaveNetworkStub getOrCreateCaveNetworkStub(World world, int origin_chunk_x, int origin_chunk_z, double distance_from_world_origin) {
      int hash = Chunk.getChunkCoordsHash(origin_chunk_x, origin_chunk_z);
      CaveNetworkStub stub = (CaveNetworkStub)this.cached_stubs.get(hash);
      if (stub == null || stub.getOriginChunkX() != origin_chunk_x || stub.getOriginChunkZ() != origin_chunk_z) {
         if (stub != null) {
            Debug.setErrorMessage("getOrCreateCaveNetworkStub: hash collision");
         }

         boolean has_mycelium = distance_from_world_origin >= 1500.0 && this.rand.nextInt(8) == 0;
         long seed = this.rand.nextLong();
         if (world.getSeed() == 1L && origin_chunk_x == -14 && origin_chunk_z == 29) {
            seed = 2617667064333438329L;
         }

         this.cached_stubs.put(hash, stub = new CaveNetworkStub(origin_chunk_x, origin_chunk_z, 64, 48, 64, seed, has_mycelium, this.rand.nextInt(3) > 0, this.rand.nextInt(3) > 0));
      }

      return stub;
   }

   public boolean isOriginOfCaveNetwork(World world, int chunk_x, int chunk_z) {
      if (!this.isGenAllowedInChunk(world, chunk_x, chunk_z)) {
         return false;
      } else {
         this.worldObj = world;
         this.rand.setSeed(world.getSeed());
         long seed_a = this.rand.nextLong();
         long seed_b = this.rand.nextLong();
         long seed_c = (long)chunk_x * seed_a;
         long seed_d = (long)chunk_z * seed_b;
         long chunk_seed = seed_c ^ seed_d ^ world.getSeed();
         this.rand.setSeed(chunk_seed);
         this.random_number_index = (int)chunk_seed & 32767;
         double distance_from_world_origin = world.getDistanceFromWorldOrigin(chunk_x * 16, chunk_z * 16);
         if (world.getSeed() == 1L && chunk_x == -14 && chunk_z == 29) {
            this.stub = this.getOrCreateCaveNetworkStub(world, chunk_x, chunk_z, distance_from_world_origin);
            return true;
         } else if (distance_from_world_origin >= 1000.0 && this.rand.nextInt(200) == 0) {
            Random rand = this.rand;
            this.rand = new Random();

            for(int origin_chunk_x = chunk_x + 1; origin_chunk_x <= chunk_x + this.a; ++origin_chunk_x) {
               for(int origin_chunk_z = chunk_z + 1; origin_chunk_z <= chunk_z + this.a; ++origin_chunk_z) {
                  if (!this.isGenAllowedInChunk(world, origin_chunk_x, origin_chunk_z)) {
                     return false;
                  }

                  if (this.isOriginOfCaveNetwork(world, origin_chunk_x, origin_chunk_z)) {
                     return false;
                  }
               }
            }

            this.rand = rand;
            this.stub = this.getOrCreateCaveNetworkStub(world, chunk_x, chunk_z, distance_from_world_origin);
            return true;
         } else {
            return false;
         }
      }
   }

   public CaveNetworkStub getCaveNetworkStubAt(World world, int chunk_x, int chunk_z) {
      if (!this.isGenAllowedInChunk(world, chunk_x, chunk_z)) {
         return null;
      } else {
         for(int origin_chunk_x = chunk_x - this.a; origin_chunk_x <= chunk_x; ++origin_chunk_x) {
            for(int origin_chunk_z = chunk_z - this.a; origin_chunk_z <= chunk_z; ++origin_chunk_z) {
               if (this.isOriginOfCaveNetwork(world, origin_chunk_x, origin_chunk_z)) {
                  return this.stub;
               }
            }
         }

         return null;
      }
   }

   public boolean isGenAllowedInBiome(BiomeGenBase biome) {
      return biome != BiomeGenBase.ocean;
   }
}
