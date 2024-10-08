package net.minecraft.world.biome;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.entity.monster.EntityEnderman;

public class BiomeGenEnd extends BiomeGenBase {
   public BiomeGenEnd(int par1) {
      super(par1);
      this.spawnableMonsterList.clear();
      this.spawnableCreatureList.clear();
      this.spawnableWaterCreatureList.clear();
      this.spawnableCaveCreatureList.clear();
      this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 100, 4, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityEarthElemental.class, 20, 1, 4));
      this.topBlock = (byte)Block.dirt.blockID;
      this.fillerBlock = (byte)Block.dirt.blockID;
      this.theBiomeDecorator = new BiomeEndDecorator(this);
   }

   public int getSkyColorByTemp(float par1) {
      return 0;
   }
}
