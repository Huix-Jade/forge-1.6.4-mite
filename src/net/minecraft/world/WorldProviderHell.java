package net.minecraft.world;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;

public class WorldProviderHell extends WorldProvider {
   public WorldProviderHell() {
      super(-1, "Nether");
   }

   public void registerWorldChunkManager() {
      this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 1.0F, 0.0F);
   }

   public Vec3 getFogColor(float par1, float par2, EntityLivingBase viewer) {
      return this.worldObj.getWorldVec3Pool().getVecFromPool(0.20000000298023224, 0.029999999329447746, 0.029999999329447746);
   }

   protected void generateLightBrightnessTable() {
      float var1 = 0.1F;

      for(int var2 = 0; var2 <= 15; ++var2) {
         float var3 = 1.0F - (float)var2 / 15.0F;
         this.lightBrightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
      }

   }

   public IChunkProvider createChunkGenerator() {
      return new ChunkProviderHell(this.worldObj, this.worldObj.getSeed());
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
      return y < 128;
   }
}
