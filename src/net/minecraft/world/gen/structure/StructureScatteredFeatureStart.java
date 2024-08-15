package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.mite.MITEConstant;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class StructureScatteredFeatureStart extends StructureStart {
   public StructureScatteredFeatureStart() {
   }

   public StructureScatteredFeatureStart(World par1World, Random par2Random, int par3, int par4) {
      super(par3, par4);
      BiomeGenBase var5 = par1World.getBiomeGenForCoords(par3 * 16 + 8, par4 * 16 + 8);
      if (MITEConstant.forced_biome_for_scattered_feature_testing != null) {
         var5 = MITEConstant.forced_biome_for_scattered_feature_testing;
      }

      double distance_from_world_spawn = par1World.getDistanceFromWorldSpawn(par3 * 16, par4 * 16);
      if (var5 != BiomeGenBase.jungle && var5 != BiomeGenBase.jungleHills) {
         if (var5 == BiomeGenBase.swampland) {
            ComponentScatteredFeatureSwampHut var8 = new ComponentScatteredFeatureSwampHut(par2Random, par3 * 16, par4 * 16);
            this.components.add(var8);
         } else {
            ComponentScatteredFeatureDesertPyramid var7 = new ComponentScatteredFeatureDesertPyramid(par2Random, par3 * 16, par4 * 16);
            if (distance_from_world_spawn >= 2000.0) {
               this.components.add(var7);
            }
         }
      } else {
         ComponentScatteredFeatureJunglePyramid var6 = new ComponentScatteredFeatureJunglePyramid(par2Random, par3 * 16, par4 * 16);
         if (distance_from_world_spawn >= 2000.0) {
            this.components.add(var6);
         }
      }

      this.updateBoundingBox();
   }
}
