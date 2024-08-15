package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenForest extends BiomeGenBase {
   public BiomeGenForest(int par1) {
      super(par1);
      this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 10, 1, 3));
      this.theBiomeDecorator.treesPerChunk = 10;
      this.theBiomeDecorator.grassPerChunk = 2;
      this.theBiomeDecorator.surface_mushrooms_per_chunk = 2;
      this.theBiomeDecorator.bush_patches_per_chunk_tenths = 2;
   }

   public WorldGenerator getRandomWorldGenForTrees(Random par1Random) {
      return (WorldGenerator)(par1Random.nextInt(5) == 0 ? this.worldGeneratorForest : (par1Random.nextInt(10) == 0 ? this.worldGeneratorBigTree : this.worldGeneratorTrees));
   }
}
