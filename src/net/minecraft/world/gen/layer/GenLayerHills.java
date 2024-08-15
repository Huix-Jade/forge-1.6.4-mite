package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerHills extends GenLayer {
   public GenLayerHills(long par1, GenLayer par3GenLayer) {
      super(par1);
      this.parent = par3GenLayer;
   }

   public int[] getInts(int par1, int par2, int par3, int par4, int z) {
      int[] var5 = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2, z);
      int[] var6 = IntCache.getIntCache(par3 * par4);
      int par3_plus_2 = par3 + 2;

      for(int var7 = 0; var7 < par4; ++var7) {
         for(int var8 = 0; var8 < par3; ++var8) {
            long par1_1 = (long)(var8 + par1);
            long par3_1 = (long)(var7 + par2);
            this.chunkSeed = this.worldGenSeed;
            this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
            this.chunkSeed += par1_1;
            this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
            this.chunkSeed += par3_1;
            this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
            this.chunkSeed += par1_1;
            this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
            this.chunkSeed += par3_1;
            int var9 = var5[var8 + 1 + (var7 + 1) * par3_plus_2];
            if ((this.chunkSeed >> 24) % 3L == 0L) {
               int var10 = var9;
               if (var9 == BiomeGenBase.desert.biomeID) {
                  var10 = BiomeGenBase.desertHills.biomeID;
               } else if (var9 == BiomeGenBase.forest.biomeID) {
                  var10 = BiomeGenBase.forestHills.biomeID;
               } else if (var9 == BiomeGenBase.taiga.biomeID) {
                  var10 = BiomeGenBase.taigaHills.biomeID;
               } else if (var9 == BiomeGenBase.plains.biomeID) {
                  var10 = BiomeGenBase.forest.biomeID;
               } else if (var9 == BiomeGenBase.icePlains.biomeID) {
                  var10 = BiomeGenBase.iceMountains.biomeID;
               } else if (var9 == BiomeGenBase.jungle.biomeID) {
                  var10 = BiomeGenBase.jungleHills.biomeID;
               }

               if (var10 == var9) {
                  var6[var8 + var7 * par3] = var9;
               } else {
                  int var11 = var5[var8 + 1 + (var7 + 1 - 1) * par3_plus_2];
                  int var12 = var5[var8 + 1 + 1 + (var7 + 1) * par3_plus_2];
                  int var13 = var5[var8 + 1 - 1 + (var7 + 1) * par3_plus_2];
                  int var14 = var5[var8 + 1 + (var7 + 1 + 1) * par3_plus_2];
                  if (var11 == var9 && var12 == var9 && var13 == var9 && var14 == var9) {
                     var6[var8 + var7 * par3] = var10;
                  } else {
                     var6[var8 + var7 * par3] = var9;
                  }
               }
            } else {
               var6[var8 + var7 * par3] = var9;
            }

            this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
            this.chunkSeed += this.worldGenSeed;
         }
      }

      return var6;
   }
}
