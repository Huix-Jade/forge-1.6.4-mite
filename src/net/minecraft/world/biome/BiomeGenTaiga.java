package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.EntityDireWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenTaiga extends BiomeGenBase {
   public BiomeGenTaiga(int par1) {
      super(par1);
      this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 10, 1, 3));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityDireWolf.class, 5, 1, 3));
      this.theBiomeDecorator.treesPerChunk = 10;
      this.theBiomeDecorator.grassPerChunk = 1;
   }

   public WorldGenerator getRandomWorldGenForTrees(Random par1Random) {
      return (WorldGenerator)(par1Random.nextInt(3) == 0 ? new WorldGenTaiga1() : new WorldGenTaiga2(false));
   }
}
