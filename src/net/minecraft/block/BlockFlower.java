package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockFlower extends BlockPlant {
   protected BlockFlower(int id, Material material) {
      super(id, material);
      float size = 0.2F;
      this.setBlockBoundsForAllThreads((double)(0.5F - size), 0.0, (double)(0.5F - size), (double)(0.5F + size), (double)(size * 3.0F), (double)(0.5F + size));
   }

   protected BlockFlower(int id) {
      this(id, Material.plants);
   }

   public int getMinAllowedLightValue() {
      return 8;
   }

   public int getMaxAllowedLightValue() {
      return 15;
   }

   public boolean dropsAsSelfWhenTrampled(Entity entity) {
      return !(entity instanceof EntityPig) && !(entity instanceof EntitySheep) && !(entity instanceof EntityCow);
   }

   public boolean isBiomeSuitable(BiomeGenBase biome, int metadata) {
      return true;
   }

   public int getPatchSize(BiomeGenBase biome) {
      return biome.isSwampBiome() ? 16 : 32;
   }
}
