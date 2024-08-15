package net.minecraft.world.biome;

import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;

public class BiomeGenRiver extends BiomeGenBase {
   public BiomeGenRiver(int par1) {
      super(par1);
      this.spawnableCreatureList.clear();
   }

   public int getBiomeGrassColor() {
      if (this == BiomeGenBase.swampRiver) {
         double var1 = (double)this.getFloatTemperature();
         double var3 = (double)this.getFloatRainfall();
         return ((ColorizerGrass.getGrassColor(var1, var3) & 16711422) + 5115470) / 2;
      } else {
         return super.getBiomeGrassColor();
      }
   }

   public int getBiomeFoliageColor() {
      if (this == BiomeGenBase.swampRiver) {
         double var1 = (double)this.getFloatTemperature();
         double var3 = (double)this.getFloatRainfall();
         return ((ColorizerFoliage.getFoliageColor(var1, var3) & 16711422) + 5115470) / 2;
      } else {
         return super.getBiomeFoliageColor();
      }
   }
}
