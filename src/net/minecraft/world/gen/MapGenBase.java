package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenBase {
   protected int range = 8;
   protected Random rand = new Random();
   protected World worldObj;
   protected int random_number_index;

   public void generate(IChunkProvider par1IChunkProvider, World par2World, int par3, int par4, byte[] par5ArrayOfByte) {
      if (this.isGenAllowedInChunk(par2World, par3, par4)) {
         int var6 = this.range;
         this.worldObj = par2World;
         this.rand.setSeed(par2World.getSeed());
         long var7 = this.rand.nextLong();
         long var9 = this.rand.nextLong();

         for(int var11 = par3 - var6; var11 <= par3 + var6; ++var11) {
            for(int var12 = par4 - var6; var12 <= par4 + var6; ++var12) {
               if (this.isGenAllowedInChunk(par2World, var11, var12)) {
                  long var13 = (long)var11 * var7;
                  long var15 = (long)var12 * var9;
                  this.rand.setSeed(var13 ^ var15 ^ par2World.getSeed());
                  this.random_number_index = (int)(var13 ^ var15 ^ par2World.getSeed()) & 32767;
                  this.recursiveGenerate(par2World, var11, var12, par3, par4, par5ArrayOfByte);
               }
            }
         }

      }
   }

   protected void recursiveGenerate(World par1World, int par2, int par3, int par4, int par5, byte[] par6ArrayOfByte) {
   }

   public boolean isGenAllowedInChunk(World world, int chunk_x, int chunk_z) {
      return this.isGenAllowedInBiome(world.getBiomeGenForCoords(chunk_x * 16, chunk_z * 16));
   }

   public boolean isGenAllowedInBiome(BiomeGenBase biome) {
      return true;
   }
}
