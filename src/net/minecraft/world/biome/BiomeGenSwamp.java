package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenSwamp extends BiomeGenBase {
   protected BiomeGenSwamp(int par1) {
      super(par1);
      this.theBiomeDecorator.treesPerChunk = 2;
      this.theBiomeDecorator.flowersPerChunk = 1;
      this.theBiomeDecorator.deadBushPerChunk = 1;
      this.theBiomeDecorator.surface_mushrooms_per_chunk = 8;
      this.theBiomeDecorator.reedsPerChunk = 10;
      this.theBiomeDecorator.clayPerChunk = 1;
      this.theBiomeDecorator.waterlilyPerChunk = 4;
      this.waterColorMultiplier = 14745518;
      this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 10, 1, 1));
   }

   public WorldGenerator getRandomWorldGenForTrees(Random par1Random) {
      return this.worldGeneratorSwamp;
   }

   public int getBiomeGrassColor() {
      double var1 = (double)this.getFloatTemperature();
      double var3 = (double)this.getFloatRainfall();
      return ((ColorizerGrass.getGrassColor(var1, var3) & 16711422) + 5115470) / 2;
   }

   public int getBiomeFoliageColor() {
      double var1 = (double)this.getFloatTemperature();
      double var3 = (double)this.getFloatRainfall();
      return ((ColorizerFoliage.getFoliageColor(var1, var3) & 16711422) + 5115470) / 2;
   }
}
