package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;

public class WorldProviderEnd extends WorldProvider {
   public boolean heal_ender_dragon;

   public WorldProviderEnd() {
      super(1, "The End");
   }

   public void registerWorldChunkManager() {
      this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.sky, 0.5F, 0.0F);
   }

   public IChunkProvider createChunkGenerator() {
      return new ChunkProviderEnd(this.worldObj, this.worldObj.getSeed());
   }

   public float calculateCelestialAngle(long par1, float par3) {
      return 0.0F;
   }

   public float[] calcSunriseSunsetColors(float par1, float par2) {
      return null;
   }

   public Vec3 getFogColor(float par1, float par2, EntityLivingBase viewer) {
      int var3 = 10518688;
      float var4 = MathHelper.cos(par1 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
      if (var4 < 0.0F) {
         var4 = 0.0F;
      }

      if (var4 > 1.0F) {
         var4 = 1.0F;
      }

      float var5 = (float)(var3 >> 16 & 255) / 255.0F;
      float var6 = (float)(var3 >> 8 & 255) / 255.0F;
      float var7 = (float)(var3 & 255) / 255.0F;
      var5 *= var4 * 0.0F + 0.15F;
      var6 *= var4 * 0.0F + 0.15F;
      var7 *= var4 * 0.0F + 0.15F;
      return this.worldObj.getWorldVec3Pool().getVecFromPool((double)var5, (double)var6, (double)var7);
   }

   public boolean isSkyColored() {
      return false;
   }

   public boolean canRespawnHere() {
      return false;
   }

   public float getCloudHeight() {
      return 8.0F;
   }

   public boolean canCoordinateBeSpawn(int par1, int par2) {
      int var3 = this.worldObj.getFirstUncoveredBlock(par1, par2);
      return var3 == 0 ? false : Block.blocksList[var3].is_always_solid;
   }

   public ChunkCoordinates getEntrancePortalLocation() {
      return new ChunkCoordinates(100, 50, 0);
   }

   public int getAverageGroundLevel() {
      return 50;
   }

   public boolean doesXZShowFog(int x, int y, int z) {
      return true;
   }
}
