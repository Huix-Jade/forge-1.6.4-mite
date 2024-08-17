package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RNG;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;

public abstract class WorldProvider {
   public static final float[] moonPhaseFactors = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   public World worldObj;
   public WorldType terrainType;
   public String field_82913_c;
   public WorldChunkManager worldChunkMgr;
   public final boolean isHellWorld;
   public final boolean hasNoSky;
   public float[] lightBrightnessTable = new float[16];
   public int dimensionId;
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
      } else {
         return DimensionManager.createProviderFor(par0);
      }
   }

   public float getCloudHeight() {
      return this.terrainType.getCloudHeight();
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


   /*======================================= Forge Start =========================================*/
   private IRenderHandler skyRenderer = null;
   private IRenderHandler cloudRenderer = null;

   /**
    * Sets the providers current dimension ID, used in default getSaveFolder()
    * Added to allow default providers to be registered for multiple dimensions.
    *
    * @param dim Dimension ID
    */
   public void setDimension(int dim)
   {
      this.dimensionId = dim;
   }

   /**
    * Returns the sub-folder of the world folder that this WorldProvider saves to.
    * EXA: DIM1, DIM-1
    * @return The sub-folder name to save this world's chunks to.
    */
   public String getSaveFolder()
   {
      return (dimensionId == 0 ? null : "DIM" + dimensionId);
   }

   /**
    * A message to display to the user when they transfer to this dimension.
    *
    * @return The message to be displayed
    */
   public String getWelcomeMessage()
   {
      if (this instanceof WorldProviderEnd)
      {
         return "Entering the End";
      }
      else if (this instanceof WorldProviderHell)
      {
         return "Entering the Nether";
      }
      return null;
   }

   /**
    * A Message to display to the user when they transfer out of this dismension.
    *
    * @return The message to be displayed
    */
   public String getDepartMessage()
   {
      if (this instanceof WorldProviderEnd)
      {
         return "Leaving the End";
      }
      else if (this instanceof WorldProviderHell)
      {
         return "Leaving the Nether";
      }
      return null;
   }

   /**
    * The dimensions movement factor. Relative to normal overworld.
    * It is applied to the players position when they transfer dimensions.
    * Exa: Nether movement is 8.0
    * @return The movement factor
    */
   public double getMovementFactor()
   {
      if (this instanceof WorldProviderHell)
      {
         return 8.0;
      }
      return 1.0;
   }


   public IRenderHandler getSkyRenderer()
   {
      return this.skyRenderer;
   }


   public void setSkyRenderer(IRenderHandler skyRenderer)
   {
      this.skyRenderer = skyRenderer;
   }


   public IRenderHandler getCloudRenderer()
   {
      return cloudRenderer;
   }


   public void setCloudRenderer(IRenderHandler renderer)
   {
      cloudRenderer = renderer;
   }

   public ChunkCoordinates getRandomizedSpawnPoint()
   {
      ChunkCoordinates chunkcoordinates = new ChunkCoordinates(this.worldObj.getSpawnPoint());

      boolean isAdventure = worldObj.getWorldInfo().getGameType() == EnumGameType.ADVENTURE;
      int spawnFuzz = terrainType.getSpawnFuzz();
      int spawnFuzzHalf = spawnFuzz / 2;

      if (!hasNoSky && !isAdventure)
      {
         chunkcoordinates.posX += this.worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf;
         chunkcoordinates.posZ += this.worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf;
         chunkcoordinates.posY = this.worldObj.getTopSolidOrLiquidBlock(chunkcoordinates.posX, chunkcoordinates.posZ);
      }

      return chunkcoordinates;
   }

   /**
    * Determine if the cusor on the map should 'spin' when rendered, like it does for the player in the nether.
    *
    * @param entity The entity holding the map, playername, or frame-ENTITYID
    * @param x X Position
    * @param y Y Position
    * @param z Z Postion
    * @return True to 'spin' the cursor
    */
   public boolean shouldMapSpin(String entity, double x, double y, double z)
   {
      return dimensionId < 0;
   }

   /**
    * Determines the dimension the player will be respawned in, typically this brings them back to the overworld.
    *
    * @param player The player that is respawning
    * @return The dimension to respawn the player in
    */
   public int getRespawnDimension(EntityPlayerMP player)
   {
      return 0;
   }

   /*======================================= Start Moved From World =========================================*/

   public BiomeGenBase getBiomeGenForCoords(int x, int z)
   {
      return worldObj.getBiomeGenForCoordsBody(x, z);
   }

   public boolean isDaytime()
   {
      return worldObj.skylightSubtracted < 4;
   }


   public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
   {
      return worldObj.getSkyColorBody(cameraEntity, partialTicks);
   }


   public Vec3 drawClouds(float partialTicks)
   {
      return worldObj.drawCloudsBody(partialTicks);
   }


   public float getStarBrightness(float par1)
   {
      return worldObj.getStarBrightnessBody(par1);
   }

   public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful)
   {
      worldObj.spawnHostileMobs = allowHostile;
      worldObj.spawnPeacefulMobs = allowPeaceful;
   }

   public void calculateInitialWeather()
   {
      worldObj.calculateInitialWeatherBody();
   }

   public void updateWeather()
   {
      worldObj.updateWeatherBody();
   }

//   public void toggleRain()
//   {
//      worldObj.worldInfo.setRainTime(1);
//   }

   public boolean canBlockFreeze(int x, int y, int z, boolean byWater)
   {
      return worldObj.canBlockFreezeBody(x, y, z, byWater);
   }

   public boolean canSnowAt(int x, int y, int z)
   {
      return worldObj.canSnowAtBody(x, y, z);
   }

//   public void setWorldTime(long time)
//   {
//      worldObj.worldInfo.setWorldTime(time);
//   }

   public long getSeed()
   {
      return worldObj.worldInfo.getSeed();
   }

//   public long getWorldTime()
//   {
//      return worldObj.worldInfo.getWorldTime();
//   }

   public ChunkCoordinates getSpawnPoint()
   {
      WorldInfo info = worldObj.worldInfo;
      return new ChunkCoordinates(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
   }

   public void setSpawnPoint(int x, int y, int z)
   {
      worldObj.worldInfo.setSpawnPosition(x, y, z);
   }

   public boolean canMineBlock(EntityPlayer player, int x, int y, int z)
   {
      return worldObj.canMineBlockBody(player, x, y, z);
   }

   public boolean isBlockHighHumidity(int x, int y, int z)
   {
      return worldObj.getBiomeGenForCoords(x, z).isHighHumidity();
   }

   public int getHeight()
   {
      return 256;
   }

   public int getActualHeight()
   {
      return hasNoSky ? 128 : 256;
   }

   public double getHorizon()
   {
      return worldObj.worldInfo.getTerrainType().getHorizon(worldObj);
   }

//   public void resetRainAndThunder()
//   {
//      worldObj.worldInfo.setRainTime(0);
//      worldObj.worldInfo.setRaining(false);
//      worldObj.worldInfo.setThunderTime(0);
//      worldObj.worldInfo.setThundering(false);
//   }

   public boolean canDoLightning(Chunk chunk)
   {
      return true;
   }

   public boolean canDoRainSnowIce(Chunk chunk)
   {
      return true;
   }
}

