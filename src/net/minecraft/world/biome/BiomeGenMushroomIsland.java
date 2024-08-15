package net.minecraft.world.biome;

import net.minecraft.block.Block;

public class BiomeGenMushroomIsland extends BiomeGenBase {
   public BiomeGenMushroomIsland(int par1) {
      super(par1);
      this.theBiomeDecorator.treesPerChunk = -100;
      this.theBiomeDecorator.flowersPerChunk = -100;
      this.theBiomeDecorator.grassPerChunk = -100;
      this.theBiomeDecorator.surface_mushrooms_per_chunk = 0;
      this.theBiomeDecorator.bigMushroomsPerChunk = 0;
      this.topBlock = (byte)Block.mycelium.blockID;
      this.spawnableMonsterList.clear();
      this.spawnableCreatureList.clear();
      this.spawnableWaterCreatureList.clear();
   }
}
