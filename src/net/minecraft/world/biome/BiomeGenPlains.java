package net.minecraft.world.biome;

import net.minecraft.entity.passive.EntityHorse;

public class BiomeGenPlains extends BiomeGenBase {
   protected BiomeGenPlains(int par1) {
      super(par1);
      this.spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 5, 1, 2));
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.flowersPerChunk = 4;
      this.theBiomeDecorator.grassPerChunk = 10;
   }
}
