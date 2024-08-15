package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiverMix extends GenLayer {
   private GenLayer biomePatternGeneratorChain;
   private GenLayer riverPatternGeneratorChain;

   public GenLayerRiverMix(long par1, GenLayer par3GenLayer, GenLayer par4GenLayer) {
      super(par1);
      this.biomePatternGeneratorChain = par3GenLayer;
      this.riverPatternGeneratorChain = par4GenLayer;
   }

   public void initWorldGenSeed(long par1) {
      this.biomePatternGeneratorChain.initWorldGenSeed(par1);
      this.riverPatternGeneratorChain.initWorldGenSeed(par1);
      super.initWorldGenSeed(par1);
   }

   public int[] getInts(int par1, int par2, int par3, int par4, int z) {
      int[] var5 = this.biomePatternGeneratorChain.getInts(par1, par2, par3, par4, z);
      int[] var6 = this.riverPatternGeneratorChain.getInts(par1, par2, par3, par4, z);
      int[] var7 = IntCache.getIntCache(par3 * par4);

      for(int var8 = 0; var8 < par3 * par4; ++var8) {
         if (var5[var8] == BiomeGenBase.ocean.biomeID) {
            var7[var8] = var5[var8];
         } else if (var6[var8] >= 0) {
            int biome_id = var5[var8];
            if (biome_id == BiomeGenBase.icePlains.biomeID) {
               var7[var8] = BiomeGenBase.frozenRiver.biomeID;
            } else if (biome_id != BiomeGenBase.desert.biomeID && biome_id != BiomeGenBase.desertHills.biomeID) {
               if (biome_id != BiomeGenBase.jungle.biomeID && biome_id != BiomeGenBase.jungleHills.biomeID) {
                  if (biome_id == BiomeGenBase.swampland.biomeID) {
                     var7[var8] = BiomeGenBase.swampRiver.biomeID;
                  } else {
                     var7[var8] = var6[var8];
                  }
               } else {
                  var7[var8] = BiomeGenBase.jungleRiver.biomeID;
               }
            } else {
               var7[var8] = BiomeGenBase.desertRiver.biomeID;
            }
         } else {
            var7[var8] = var5[var8];
         }
      }

      return var7;
   }
}
