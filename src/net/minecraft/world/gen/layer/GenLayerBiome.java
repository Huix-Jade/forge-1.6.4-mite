package net.minecraft.world.gen.layer;

import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerBiome extends GenLayer {
   private BiomeGenBase[] allowedBiomes;

   public GenLayerBiome(long par1, GenLayer par3GenLayer, WorldType par4WorldType) {
      super(par1);
      this.parent = par3GenLayer;

      this.allowedBiomes = new BiomeGenBase[] {BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills,
              BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga, BiomeGenBase.jungle};
      if (par4WorldType == WorldType.DEFAULT_1_1) {
         this.allowedBiomes = new BiomeGenBase[] {BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills,
                 BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga};
      }

   }

   private float getLatitudeTemperature(int z) {
      return MathHelper.clamp_float(0.7F + (float)z / 1024.0F, -0.1F, 2.0F);
   }

   private boolean doesBiomeHaveValidTemperature(BiomeGenBase biome, int z, float tolerance) {
      float latitude_temperature = this.getLatitudeTemperature(z);
      float min_biome_temperature = latitude_temperature - tolerance;
      float max_biome_temperature = latitude_temperature + tolerance;
      return biome.temperature >= min_biome_temperature && biome.temperature <= max_biome_temperature;
   }

   private int getRandomBiomeWithValidTemperature(int z) {
      float tolerance = 0.2F;

      int biome_id;
      do {
         biome_id = this.allowedBiomes[this.nextInt(this.allowedBiomes.length)].biomeID;
         if (tolerance < 0.5F) {
            tolerance += 0.01F;
         }
      } while(!this.doesBiomeHaveValidTemperature(BiomeGenBase.biomeList[biome_id], z, tolerance));

      return biome_id;
   }

   public int[] getInts(int par1, int par2, int par3, int par4, int z) {
      int[] var5 = this.parent.getInts(par1, par2, par3, par4, z);
      int[] var6 = IntCache.getIntCache(par3 * par4);

      for(int var7 = 0; var7 < par4; ++var7) {
         for(int var8 = 0; var8 < par3; ++var8) {
            this.initChunkSeed((long)(var8 + par1), (long)(var7 + par2));
            int var9 = var5[var8 + var7 * par3];
            if (var9 == 0) {
               var6[var8 + var7 * par3] = 0;
            } else if (var9 == 1) {
               var6[var8 + var7 * par3] = this.allowedBiomes[this.nextInt(this.allowedBiomes.length)].biomeID;
            } else {
               int var10 = this.allowedBiomes[this.nextInt(this.allowedBiomes.length)].biomeID;
               if (var10 == BiomeGenBase.taiga.biomeID) {
                  var6[var8 + var7 * par3] = var10;
               } else {
                  var6[var8 + var7 * par3] = BiomeGenBase.icePlains.biomeID;
               }
            }
         }
      }

      return var6;
   }
}
