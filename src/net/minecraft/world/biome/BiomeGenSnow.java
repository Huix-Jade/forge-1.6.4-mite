package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.EntityDireWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenSnow extends BiomeGenBase {
   public BiomeGenSnow(int par1) {
      super(par1);
      this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 4, 1, 3));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityDireWolf.class, 1, 1, 3));
   }

   public WorldGenerator getRandomWorldGenForTrees(Random par1Random) {
      return (WorldGenerator)(par1Random.nextInt(3) == 0 ? new WorldGenTaiga1() : new WorldGenTaiga2(false));
   }
}
