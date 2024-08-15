package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import static net.minecraftforge.common.EnumPlantType.*;

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

   @Override
   public EnumPlantType getPlantType(World world, int x, int y, int z)
   {
      if (blockID == crops.blockID        ) return Crop;
      if (blockID == deadBush.blockID     ) return Desert;
      if (blockID == waterlily.blockID    ) return Water;
      if (blockID == mushroomRed.blockID  ) return Cave;
      if (blockID == mushroomBrown.blockID) return Cave;
      if (blockID == netherStalk.blockID  ) return Nether;
      if (blockID == sapling.blockID      ) return Plains;
      if (blockID == melonStem.blockID    ) return Crop;
      if (blockID == pumpkinStem.blockID  ) return Crop;
      if (blockID == tallGrass.blockID    ) return Plains;
      return Plains;
   }

   @Override
   public int getPlantID(World world, int x, int y, int z)
   {
      return blockID;
   }

   @Override
   public int getPlantMetadata(World world, int x, int y, int z)
   {
      return world.getBlockMetadata(x, y, z);
   }
}
