package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RNG;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

public abstract class WorldProvider {
   public static final float[] moonPhaseFactors = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   public World worldObj;
   public WorldType terrainType;
   public String field_82913_c;
   public WorldChunkManager worldChunkMgr;
   public final boolean isHellWorld;
   public final boolean hasNoSky;
   public float[] lightBrightnessTable = new float[16];
   public final int dimensionId;
   private float[] colorsSunriseSunset = new float[4];
   private final boolean is_underworld;
   private final boolean is_nether;
   private final boolean is_overworld;
   private final boolean is_the_end;
   private final String name;
   private final int block_domain_radius;
   private int weather_forecast;
   private int last_forecast_day;

   public WorldProvider(int dimension_id, String name) {
      this.dimensionId = dimension_id;
      boolean is_underworld = false;
      boolean is_nether = false;
      boolean is_overworld = false;
      boolean is_the_end = false;
      if (this.dimensionId == -2) {
         is_underworld = true;
      } else if (this.dimensionId == -1) {
         is_nether = true;
      } else if (this.dimensionId == 0) {
         is_overworld = true;
      } else if (this.dimensionId == 1) {
         is_the_end = true;
      } else {
         Minecraft.setErrorMessage("WorldProvider: Unrecognized dimension id " + this.dimensionId);
      }

      this.is_underworld = is_underworld;
      this.is_nether = is_nether;
      this.is_overworld = is_overworld;
      this.is_the_end = is_the_end;
      this.block_domain_radius = 524288 / (is_nether ? 8 : 1);
      this.hasNoSky = !is_overworld;
      this.isHellWorld = is_nether;
      this.name = name;
   }

   public final void registerWorld(World par1World) {
      this.worldObj = par1World;
      this.terrainType = par1World.getWorldInfo().getTerrainType();
      this.field_82913_c = par1World.getWorldInfo().getGeneratorOptions();
      this.registerWorldChunkManager();
      this.generateLightBrightnessTable();
   }

   protected void generateLightBrightnessTable() {
      float var1 = 0.0F;

      for(int var2 = 0; var2 <= 15; ++var2) {
         float var3 = 1.0F - (float)var2 / 15.0F;
         this.lightBrightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
         if (var2 > 0 && var2 < 15) {
            float extra_drop_off = 0.001F * (float)(15 - var2);
            if (extra_drop_off > 0.0F) {
               float[] var10000 = this.lightBrightnessTable;
               var10000[var2] -= extra_drop_off;
            }
         }
      }

   }

   protected void registerWorldChunkManager() {
      if (this.worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT) {
         FlatGeneratorInfo var1 = FlatGeneratorInfo.createFlatGeneratorFromString(this.worldObj.getWorldInfo().getGeneratorOptions());
         this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.biomeList[var1.getBiome()], 0.5F, 0.5F);
      } else {
         this.worldChunkMgr = new WorldChunkManager(this.worldObj);
      }

   }

   public IChunkProvider createChunkGenerator() {
      return (IChunkProvider)(this.terrainType == WorldType.FLAT ? new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.field_82913_c) : new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled()));
   }

   public boolean canCoordinateBeSpawn(int par1, int par2) {
      return this.worldObj.getFirstUncoveredBlock(par1, par2) == Block.grass.blockID;
   }

   public float calculateCelestialAngle(long par1, float par3) {
      int var4 = (int)(par1 % 24000L);
      float var5 = ((float)var4 + par3) / 24000.0F - 0.25F;
      if (var5 < 0.0F) {
         ++var5;
      }

      if (var5 > 1.0F) {
         --var5;
      }

      float var6 = var5;
      var5 = 1.0F - (float)((Math.cos((double)var5 * Math.PI) + 1.0) / 2.0);
      var5 = var6 + (var5 - var6) / 3.0F;
      return var5;
   }

   public int getMoonPhase(long par1) {
      return (int)(par1 / 24000L + 1L) % 8;
   }

   public final boolean isSurfaceWorld() {
      return this.is_overworld;
   }

   public float[] calcSunriseSunsetColors(float par1, float par2) {
      float var3 = 0.4F;
      float var4 = MathHelper.cos(par1 * 3.1415927F * 2.0F) - 0.0F;
      float var5 = -0.0F;
      if (var4 >= var5 - var3 && var4 <= var5 + var3) {
         float var6 = (var4 - var5) / var3 * 0.5F + 0.5F;
         float var7 = 1.0F - (1.0F - MathHelper.sin(var6 * 3.1415927F)) * 0.99F;
         var7 *= var7;
         this.colorsSunriseSunset[0] = var6 * 0.3F + 0.7F;
         this.colorsSunriseSunset[1] = var6 * var6 * 0.7F + 0.2F;
         this.colorsSunriseSunset[2] = var6 * var6 * 0.0F + 0.2F;
         this.colorsSunriseSunset[3] = var7;
         float g1;
         float b1;
         float[] var10000;
         if (this.worldObj.getHourOfDay() < 12) {
            if (this.last_forecast_day != this.worldObj.getDayOfWorld()) {
               if (!this.worldObj.willStormStartToday(4000) && !this.worldObj.isStormingTodayAt(6000)) {
                  if (!this.worldObj.willPrecipitationStartToday(4000) && !this.worldObj.isPrecipitatingTodayAt(6000)) {
                     this.weather_forecast = 0;
                  } else {
                     this.weather_forecast = 1;
                  }
               } else {
                  this.weather_forecast = 2;
               }

               this.last_forecast_day = this.worldObj.getDayOfWorld();
            }

            float r1;
            float r2;
            float g2;
            float b2;
            float w2;
            float w1;
            if (this.weather_forecast == 0) {
               r1 = this.colorsSunriseSunset[0];
               var10000 = this.colorsSunriseSunset;
               g1 = var10000[1] *= 1.2F;
               b1 = this.colorsSunriseSunset[2];
               r2 = (this.colorsSunriseSunset[0] + 1.0F) * 0.5F;
               g2 = (this.colorsSunriseSunset[1] + 0.7F) * 0.5F;
               b2 = (this.colorsSunriseSunset[2] + 0.4F) * 0.5F;
               w2 = var6 * var6;
               w1 = 1.0F - w2;
               this.colorsSunriseSunset[0] = r1 * w1 + r2 * w2;
               this.colorsSunriseSunset[1] = g1 * w1 + g2 * w2;
               this.colorsSunriseSunset[2] = b1 * w1 + b2 * w2;
            } else if (this.weather_forecast == 2) {
               r1 = this.colorsSunriseSunset[0];
               g1 = this.colorsSunriseSunset[1];
               b1 = this.colorsSunriseSunset[2];
               r2 = (this.colorsSunriseSunset[0] + 1.0F) * 0.5F;
               g2 = (this.colorsSunriseSunset[1] + 0.0F) * 0.5F;
               b2 = (this.colorsSunriseSunset[2] + 0.0F) * 0.5F;
               w2 = var6;
               w1 = 1.0F - w2;
               this.colorsSunriseSunset[0] = r1 * w1 + r2 * w2;
               this.colorsSunriseSunset[1] = g1 * w1 + g2 * w2;
               this.colorsSunriseSunset[2] = b1 * w1 + b2 * w2;
            }
         } else {
            int index = (int)(this.worldObj.getSeed() * 15771L + this.worldObj.getWorldCreationTime() + (long)this.worldObj.getDayOfWorld());
            ++index;
            g1 = 1.0F + RNG.float_1_minus_float_1[index & 32767] * 0.6F;
            if (g1 < 0.6F) {
               g1 = 1.0F + g1 - 0.5F;
            }

            this.colorsSunriseSunset[2] = MathHelper.clamp_float(this.colorsSunriseSunset[2] * g1, 0.0F, 1.0F);
            if (g1 > 0.6F && g1 < 1.4F) {
               ++index;
               b1 = 1.0F + RNG.float_1_minus_float_1[index & 32767] * 0.2F;
               if (b1 > 1.1F) {
                  b1 = 1.0F;
               }

               this.colorsSunriseSunset[1] = MathHelper.clamp_float(this.colorsSunriseSunset[1] * b1, 0.0F, 1.0F);
            }
         }

         var10000 = this.colorsSunriseSunset;
         var10000[3] *= 1.0F - this.worldObj.getRainStrength(par2);
         return this.colorsSunriseSunset;
      } else {
         return null;
      }
   }

   public Vec3 getFogColor(float par1, float par2, EntityLivingBase viewer) {
      float var3 = MathHelper.cos(par1 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      float var4 = 0.7529412F;
      float var5 = 0.84705883F;
      float var6 = 1.0F;
      var4 *= var3 * 0.94F + 0.06F;
      var5 *= var3 * 0.94F + 0.06F;
      var6 *= var3 * 0.91F + 0.09F;
      return this.worldObj.getWorldVec3Pool().getVecFromPool((double)var4, (double)var5, (double)var6);
   }

   public boolean canRespawnHere() {
      return true;
   }

   public static WorldProvider getProviderForDimension(int par0) {
      if (par0 == -2) {
         return new WorldProviderUnderworld();
      } else if (par0 == -1) {
         return new WorldProviderHell();
      } else if (par0 == 0) {
         return new WorldProviderSurface();
      } else {
         return par0 == 1 ? new WorldProviderEnd() : null;
      }
   }

   public float getCloudHeight() {
      return 128.0F;
   }

   public boolean isSkyColored() {
      return true;
   }

   public ChunkCoordinates getEntrancePortalLocation() {
      return null;
   }

   public int getAverageGroundLevel() {
      return this.terrainType == WorldType.FLAT ? 4 : 64;
   }

   public boolean getWorldHasVoidParticles() {
      return !this.isTheEnd();
   }

   public boolean getWorldHasVoidFog() {
      return this.isSurfaceWorld();
   }

   public double getVoidFogYFactor() {
      return this.terrainType == WorldType.FLAT ? 1.0 : 0.03125;
   }

   public boolean doesXZShowFog(int x, int y, int z) {
      return false;
   }

   public boolean drawGuiVignette() {
      return !this.isUnderworld();
   }

   public final String getDimensionName() {
      return this.name;
   }

   public final boolean isUnderworld() {
      return this.is_underworld;
   }

   public final boolean isTheNether() {
      return this.is_nether;
   }

   public final boolean isTheEnd() {
      return this.is_the_end;
   }

   public final int getBlockDomainRadius() {
      return this.block_domain_radius;
   }
}
