package net.minecraft.world.biome;

import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;

public class BiomeGenHell extends BiomeGenBase {
   public BiomeGenHell(int par1) {
      super(par1);
      this.spawnableMonsterList.clear();
      this.spawnableCreatureList.clear();
      this.spawnableWaterCreatureList.clear();
      this.spawnableCaveCreatureList.clear();
      this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 50, 1, 2));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityPigZombie.class, 100, 1, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityMagmaCube.class, 10, 4, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityEarthElemental.class, 40, 1, 1));
   }
}
