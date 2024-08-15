package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerSwampRivers extends GenLayer {
   public GenLayerSwampRivers(long par1, GenLayer par3GenLayer) {
      super(par1);
      this.parent = par3GenLayer;
   }

   public int[] getInts(int par1, int par2, int par3, int par4, int z) {
      int[] var5 = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2, z);
      int[] var6 = IntCache.getIntCache(par3 * par4);

      for(int var7 = 0; var7 < par4; ++var7) {
         for(int var8 = 0; var8 < par3; ++var8) {
            this.initChunkSeed((long)(var8 + par1), (long)(var7 + par2));
            int var9 = var5[var8 + 1 + (var7 + 1) * (par3 + 2)];
            if (var9 == BiomeGenBase.swampland.biomeID && this.nextInt(6) == 0 || (var9 == BiomeGenBase.jungle.biomeID || var9 == BiomeGenBase.jungleHills.biomeID) && this.nextInt(8) == 0) {
               if (var9 != BiomeGenBase.jungle.biomeID && var9 != BiomeGenBase.jungleHills.biomeID) {
                  if (var9 == BiomeGenBase.swampland.biomeID) {
                     var6[var8 + var7 * par3] = BiomeGenBase.swampRiver.biomeID;
                  } else {
                     var6[var8 + var7 * par3] = BiomeGenBase.river.biomeID;
                  }
               } else {
                  var6[var8 + var7 * par3] = BiomeGenBase.jungleRiver.biomeID;
               }
            } else {
               var6[var8 + var7 * par3] = var9;
            }
         }
      }

      return var6;
   }
}
