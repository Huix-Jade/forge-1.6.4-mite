package net.minecraft.world;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.ChunkProviderUnderworld;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderUnderworld extends WorldProvider {
   public WorldProviderUnderworld() {
      super(-2, "Underworld");
   }

   public void registerWorldChunkManager() {
      this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.underworld, 1.0F, 0.0F);
   }

   public Vec3 getFogColor(float par1, float par2, EntityLivingBase viewer) {
      int day_of_cycle = viewer.worldObj.getDayOfWorld() % 32;
      int distance_from_peak = Math.abs(day_of_cycle - 16);
      float grayscale = (float)distance_from_peak * 0.004F;
      return this.worldObj.getWorldVec3Pool().getVecFromPool((double)grayscale, (double)grayscale, (double)grayscale);
   }

   public IChunkProvider createChunkGenerator() {
      return new ChunkProviderUnderworld(this.worldObj, this.worldObj.getSeed());
   }

   public boolean canCoordinateBeSpawn(int par1, int par2) {
      return false;
   }

   public float calculateCelestialAngle(long par1, float par3) {
      return 0.5F;
   }

   public boolean canRespawnHere() {
      return false;
   }

   public boolean doesXZShowFog(int x, int y, int z) {
      return false;
   }
}
