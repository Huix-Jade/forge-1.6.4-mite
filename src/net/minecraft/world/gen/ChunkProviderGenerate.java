package net.minecraft.world.gen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RNG;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.network.MapGenCaveNetwork;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.MapGenScatteredFeature;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;

public final class ChunkProviderGenerate implements IChunkProvider
{
   /** RNG. */
   private Random rand;

   /** A NoiseGeneratorOctaves used in generating terrain */
   private NoiseGeneratorOctaves noiseGen1;

   /** A NoiseGeneratorOctaves used in generating terrain */
   private NoiseGeneratorOctaves noiseGen2;

   /** A NoiseGeneratorOctaves used in generating terrain */
   private NoiseGeneratorOctaves noiseGen3;

   /** A NoiseGeneratorOctaves used in generating terrain */
   private NoiseGeneratorOctaves noiseGen4;

   /** A NoiseGeneratorOctaves used in generating terrain */
   public NoiseGeneratorOctaves noiseGen5;

   /** A NoiseGeneratorOctaves used in generating terrain */
   public NoiseGeneratorOctaves noiseGen6;
   public NoiseGeneratorOctaves mobSpawnerNoise;

   /** Reference to the World object. */
   private World worldObj;

   /** are map structures going to be generated (e.g. strongholds) */
   private final boolean mapFeaturesEnabled;

   /** Holds the overall noise array used in chunk generation */
   private double[] noiseArray;
   private double[] stoneNoise = new double[256];
   private MapGenBase caveGenerator = new MapGenCaves();

   /** Holds Stronghold Generator */
   private MapGenStronghold strongholdGenerator = new MapGenStronghold();

   /** Holds Village Generator */
   private MapGenVillage villageGenerator = new MapGenVillage();

   /** Holds Mineshaft Generator */
   private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
   private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();

   /** Holds ravine generator */
   private MapGenBase ravineGenerator = new MapGenRavine();
   private MapGenCaveNetwork cave_network_generator = new MapGenCaveNetwork();

   /** The biomes that are used to generate the chunk */
   private BiomeGenBase[] biomesForGeneration;

   /** A double array that hold terrain noise from noiseGen3 */
   double[] noise3;

   /** A double array that hold terrain noise */
   double[] noise1;

   /** A double array that hold terrain noise from noiseGen2 */
   double[] noise2;

   /** A double array that hold terrain noise from noiseGen5 */
   double[] noise5;

   /** A double array that holds terrain noise from noiseGen6 */
   double[] noise6;

   /**
    * Used to store the 5x5 parabolic field that is used during terrain generation.
    */
   float[] parabolicField;
   int[][] field_73219_j = new int[32][32];
   private NoiseGeneratorOctaves noiseGen8;
   private double[] stone_noise_2 = new double[256];
   private double[] stone_noise_3 = new double[256];

   public ChunkProviderGenerate(World par1World, long par2, boolean par4)
   {
      this.worldObj = par1World;
      this.mapFeaturesEnabled = par4;
      this.rand = new Random(par2);
      this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
      this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
      this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
      this.noiseGen4 = new NoiseGeneratorOctaves(this.rand, 4);
      this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
      this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
      this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
      this.noiseGen8 = new NoiseGeneratorOctaves(this.rand, 4);
   }

   /**
    * Generates the shape of the terrain for the chunk though its all stone though the water is frozen if the
    * temperature is low enough
    */
   public void generateTerrain(int par1, int par2, byte[] par3ArrayOfByte)
   {
      byte var4 = 4;
      byte var5 = 16;
      byte var6 = 63;
      int var7 = var4 + 1;
      byte var8 = 17;
      int var9 = var4 + 1;
      this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, par1 * 4 - 2, par2 * 4 - 2, var7 + 5, var9 + 5);
      this.noiseArray = this.initializeNoiseField(this.noiseArray, par1 * var4, 0, par2 * var4, var7, var8, var9);

      for (int var10 = 0; var10 < var4; ++var10)
      {
         for (int var11 = 0; var11 < var4; ++var11)
         {
            for (int var12 = 0; var12 < var5; ++var12)
            {
               double var13 = 0.125D;
               double var15 = this.noiseArray[((var10 + 0) * var9 + var11 + 0) * var8 + var12 + 0];
               double var17 = this.noiseArray[((var10 + 0) * var9 + var11 + 1) * var8 + var12 + 0];
               double var19 = this.noiseArray[((var10 + 1) * var9 + var11 + 0) * var8 + var12 + 0];
               double var21 = this.noiseArray[((var10 + 1) * var9 + var11 + 1) * var8 + var12 + 0];
               double var23 = (this.noiseArray[((var10 + 0) * var9 + var11 + 0) * var8 + var12 + 1] - var15) * var13;
               double var25 = (this.noiseArray[((var10 + 0) * var9 + var11 + 1) * var8 + var12 + 1] - var17) * var13;
               double var27 = (this.noiseArray[((var10 + 1) * var9 + var11 + 0) * var8 + var12 + 1] - var19) * var13;
               double var29 = (this.noiseArray[((var10 + 1) * var9 + var11 + 1) * var8 + var12 + 1] - var21) * var13;

               for (int var31 = 0; var31 < 8; ++var31)
               {
                  double var32 = 0.25D;
                  double var34 = var15;
                  double var36 = var17;
                  double var38 = (var19 - var15) * var32;
                  double var40 = (var21 - var17) * var32;

                  for (int var42 = 0; var42 < 4; ++var42)
                  {
                     int var43 = var42 + var10 * 4 << 11 | 0 + var11 * 4 << 7 | var12 * 8 + var31;
                     short var44 = 128;
                     var43 -= var44;
                     double var45 = 0.25D;
                     double var47 = (var36 - var34) * var45;
                     double var49 = var34 - var47;

                     for (int var51 = 0; var51 < 4; ++var51)
                     {
                        if ((var49 += var47) > 0.0D)
                        {
                           par3ArrayOfByte[var43 += var44] = (byte) Block.stone.blockID;
                        }
                        else if (var12 * 8 + var31 < var6)
                        {
                           par3ArrayOfByte[var43 += var44] = (byte)Block.waterStill.blockID;
                        }
                        else
                        {
                           par3ArrayOfByte[var43 += var44] = 0;
                        }
                     }

                     var34 += var38;
                     var36 += var40;
                  }

                  var15 += var23;
                  var17 += var25;
                  var19 += var27;
                  var21 += var29;
               }
            }
         }
      }
   }

   public static void placeRandomCobwebs(int chunk_x, int chunk_z, byte[] block_ids, Random rand)
   {
      int random_number_index = rand.nextInt();
      byte web_block_id = (byte)Block.web.blockID;
      byte lava_still_block_id = (byte)Block.lavaStill.blockID;
      byte lava_moving_block_id = (byte)Block.lavaMoving.blockID;
      byte stone_block_id = (byte)Block.stone.blockID;
      short frequency = 128;

      for (int attempts = 0; attempts < frequency; ++attempts)
      {
         ++random_number_index;
         int x = RNG.int_14_plus_1[random_number_index & 32767];
         ++random_number_index;
         int y = RNG.int_126_plus_1[random_number_index & 32767];
         ++random_number_index;
         int z = RNG.int_14_plus_1[random_number_index & 32767];
         int index = (z * 16 + x) * 128 + y;

         if (block_ids[index] == 0)
         {
            byte block_id_above = block_ids[index + 1];
            byte block_id_below = block_ids[index - 1];
            byte block_id_front = block_ids[index + 128];
            byte block_id_back = block_ids[index - 128];
            byte block_id_right = block_ids[index + 2048];
            byte block_id_left = block_ids[index - 2048];
            int solid_face_adjacent_blocks = 0;

            if (block_id_above != 0)
            {
               ++solid_face_adjacent_blocks;
            }

            if (block_id_below != 0)
            {
               ++solid_face_adjacent_blocks;
            }

            if (block_id_front != 0)
            {
               ++solid_face_adjacent_blocks;
            }

            if (block_id_back != 0)
            {
               ++solid_face_adjacent_blocks;
            }

            if (block_id_right != 0)
            {
               ++solid_face_adjacent_blocks;
            }

            if (block_id_left != 0)
            {
               ++solid_face_adjacent_blocks;
            }

            if (solid_face_adjacent_blocks >= 4 && block_id_below != lava_still_block_id && block_id_below != lava_moving_block_id && (block_id_above == stone_block_id || block_id_below == stone_block_id || block_id_front == stone_block_id || block_id_back == stone_block_id || block_id_right == stone_block_id || block_id_left == stone_block_id))
            {
               block_ids[index] = web_block_id;
               attempts -= frequency * 4;
            }
         }
      }
   }

   /**
    * Replaces the stone that was placed in with blocks that match the biome
    */
   public void replaceBlocksForBiome(int par1, int par2, byte[] par3ArrayOfByte, BiomeGenBase[] par4ArrayOfBiomeGenBase)
   {
      boolean var5 = this.worldObj.worldInfo.getEarliestMITEReleaseRunIn() >= 165;
      boolean var6 = this.worldObj.worldInfo.getEarliestMITEReleaseRunIn() >= 168;
      byte var7 = 63;
      double var8 = 0.03125D;

      if (var6)
      {
         this.stoneNoise = this.noiseGen4.generateNoiseOctaves(this.stoneNoise, par1 * 16, 0, par2 * 16, 16, 1, 16, var8 * 2.0D, var8 * 2.0D, var8 * 2.0D);
         this.stone_noise_2 = this.noiseGen4.generateNoiseOctaves(this.stone_noise_2, par1 * 16, 0, par2 * 16, 16, 1, 16, var8 * 16.0D, var8 * 16.0D, var8 * 16.0D);
         this.stone_noise_3 = this.noiseGen8.generateNoiseOctaves(this.stone_noise_3, par1 * 16, 0, par2 * 16, 16, 1, 16, var8 * 32.0D, var8 * 32.0D, var8 * 32.0D);
      }
      else
      {
         this.stoneNoise = this.noiseGen4.generateNoiseOctaves(this.stoneNoise, par1 * 16, par2 * 16, 0, 16, 16, 1, var8 * 2.0D, var8 * 2.0D, var8 * 2.0D);
      }

      for (int var10 = 0; var10 < 16; ++var10)
      {
         for (int var11 = 0; var11 < 16; ++var11)
         {
            BiomeGenBase var12 = par4ArrayOfBiomeGenBase[var11 + var10 * 16];
            float var13 = var12.getFloatTemperature();
            int var14 = var10 + var11 * 16;
            double var15;

            if (var6)
            {
               var15 = this.stoneNoise[var14] / 3.0D + 3.0D - 0.5D;
            }
            else
            {
               var15 = this.stoneNoise[var14] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D;
            }

            int var17 = (int)var15;
            int var18 = -1;
            byte var19 = var12.topBlock;
            byte var20 = var12.fillerBlock;
            int var21 = var6 ? (int)(this.stone_noise_2[var14] / 3.0D + 1.0D) : 0;

            for (int var22 = 127; var22 >= 0; --var22)
            {
               int var23 = (var11 * 16 + var10) * 128 + var22;

               if (var22 <= 0 + this.rand.nextInt(5))
               {
                  par3ArrayOfByte[var23] = (byte)Block.bedrock.blockID;
               }
               else
               {
                  byte var24 = par3ArrayOfByte[var23];

                  if (var24 == 0)
                  {
                     var18 = -1;
                  }
                  else if (var24 == Block.stone.blockID)
                  {
                     if (var18 == -1)
                     {
                        boolean var25 = var22 >= var7;

                        if (var17 <= 0)
                        {
                           if (var6)
                           {
                              var19 = var15 < 0.25D ? 0 : (var21 <= 0 && var25 ? var12.topBlock : 0);

                              if (var19 == 0 && var25 && var15 > 0.7D)
                              {
                                 if (var15 > 0.95D)
                                 {
                                    var19 = var12.topBlock;
                                 }
                                 else if (this.stone_noise_3[var14] < 1.0D)
                                 {
                                    var19 = (byte)Block.stone.blockID;
                                 }
                              }
                           }
                           else
                           {
                              var19 = var5 ? var12.topBlock : 0;
                           }

                           var20 = (byte)Block.stone.blockID;
                        }
                        else if (var22 >= var7 - 4 && var22 <= var7 + 1)
                        {
                           var19 = var12.topBlock;
                           var20 = var12.fillerBlock;
                        }

                        if (!var25 && var19 == 0)
                        {
                           if (var13 < 0.15F)
                           {
                              var19 = (byte)Block.ice.blockID;
                           }
                           else
                           {
                              var19 = (byte)Block.waterStill.blockID;
                           }
                        }

                        var18 = var17;

                        if (var22 >= var7 - 1)
                        {
                           par3ArrayOfByte[var23] = var19;
                        }
                        else
                        {
                           par3ArrayOfByte[var23] = var20;
                        }
                     }
                     else if (var18 > 0)
                     {
                        --var18;
                        par3ArrayOfByte[var23] = var20;

                        if (var18 == 0 && var20 == Block.sand.blockID)
                        {
                           var18 = this.rand.nextInt(4);
                           var20 = (byte)Block.sandStone.blockID;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   /**
    * loads or generates the chunk at the chunk location specified
    */
   public Chunk loadChunk(int par1, int par2)
   {
      return this.provideChunk(par1, par2);
   }

   /**
    * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
    * specified chunk from the map seed and chunk seed
    */
   public Chunk provideChunk(int par1, int par2)
   {
      if (!this.worldObj.isChunkWithinBlockDomain(par1, par2))
      {
         Chunk var7 = new Chunk(this.worldObj, par1, par2);
         var7.generateSkylightMap(true);
         return var7;
      }
      else
      {
         this.rand.setSeed((long)par1 * 341873128712L + (long)par2 * 132897987541L);
         byte[] var3 = new byte[32768];
         this.generateTerrain(par1, par2, var3);
         this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, par1 * 16, par2 * 16, 16, 16);
         this.replaceBlocksForBiome(par1, par2, var3, this.biomesForGeneration);
         this.caveGenerator.generate(this, this.worldObj, par1, par2, var3);
         this.ravineGenerator.generate(this, this.worldObj, par1, par2, var3);

         if (this.worldObj.worldInfo.getEarliestMITEReleaseRunIn() >= 139)
         {
            this.cave_network_generator.generate(this, this.worldObj, par1, par2, var3);
         }

         placeRandomCobwebs(par1, par2, var3, this.rand);

         if (this.mapFeaturesEnabled)
         {
            this.mineshaftGenerator.generate(this, this.worldObj, par1, par2, var3);
            this.villageGenerator.generate(this, this.worldObj, par1, par2, var3);
            this.strongholdGenerator.generate(this, this.worldObj, par1, par2, var3);
            this.scatteredFeatureGenerator.generate(this, this.worldObj, par1, par2, var3);
         }

         if (this.worldObj.pending_sand_falls != null && this.worldObj.worldInfo.getEarliestMITEReleaseRunIn() >= 189)
         {
            this.performSandFalls(this.worldObj.pending_sand_falls, var3);
            this.worldObj.pending_sand_falls = null;
         }

         Chunk var4 = new Chunk(this.worldObj, var3, par1, par2);
         byte[] var5 = var4.getBiomeArray();

         for (int var6 = 0; var6 < var5.length; ++var6)
         {
            var5[var6] = (byte)this.biomesForGeneration[var6].biomeID;
         }

         var4.generateSkylightMap(true);

         if (this.worldObj.pending_sand_falls != null)
         {
            var4.pending_sand_falls = this.worldObj.pending_sand_falls;
            this.worldObj.pending_sand_falls = null;
         }

         return var4;
      }
   }

   /**
    * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the
    * size.
    */
   private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7)
   {
      if (par1ArrayOfDouble == null)
      {
         par1ArrayOfDouble = new double[par5 * par6 * par7];
      }

      if (this.parabolicField == null)
      {
         this.parabolicField = new float[25];

         for (int var8 = -2; var8 <= 2; ++var8)
         {
            for (int var9 = -2; var9 <= 2; ++var9)
            {
               float var10 = 10.0F / MathHelper.sqrt_float((float)(var8 * var8 + var9 * var9) + 0.2F);
               this.parabolicField[var8 + 2 + (var9 + 2) * 5] = var10;
            }
         }
      }

      double var44 = 684.412D;
      double var45 = 684.412D;
      this.noise5 = this.noiseGen5.generateNoiseOctaves(this.noise5, par2, par4, par5, par7, 1.121D, 1.121D, 0.5D);
      this.noise6 = this.noiseGen6.generateNoiseOctaves(this.noise6, par2, par4, par5, par7, 200.0D, 200.0D, 0.5D);
      this.noise3 = this.noiseGen3.generateNoiseOctaves(this.noise3, par2, par3, par4, par5, par6, par7, var44 / 80.0D, var45 / 160.0D, var44 / 80.0D);
      this.noise1 = this.noiseGen1.generateNoiseOctaves(this.noise1, par2, par3, par4, par5, par6, par7, var44, var45, var44);
      this.noise2 = this.noiseGen2.generateNoiseOctaves(this.noise2, par2, par3, par4, par5, par6, par7, var44, var45, var44);
      boolean var12 = false;
      boolean var13 = false;
      int var14 = 0;
      int var15 = 0;

      for (int var16 = 0; var16 < par5; ++var16)
      {
         for (int var17 = 0; var17 < par7; ++var17)
         {
            float var18 = 0.0F;
            float var19 = 0.0F;
            float var20 = 0.0F;
            byte var21 = 2;
            BiomeGenBase var22 = this.biomesForGeneration[var16 + 2 + (var17 + 2) * (par5 + 5)];

            for (int var23 = -var21; var23 <= var21; ++var23)
            {
               for (int var24 = -var21; var24 <= var21; ++var24)
               {
                  BiomeGenBase var25 = this.biomesForGeneration[var16 + var23 + 2 + (var17 + var24 + 2) * (par5 + 5)];
                  float var26 = this.parabolicField[var23 + 2 + (var24 + 2) * 5] / (var25.minHeight + 2.0F);

                  if (var25.minHeight > var22.minHeight)
                  {
                     var26 /= 2.0F;
                  }

                  var18 += var25.maxHeight * var26;
                  var19 += var25.minHeight * var26;
                  var20 += var26;
               }
            }

            var18 /= var20;
            var19 /= var20;
            var18 = var18 * 0.9F + 0.1F;
            var19 = (var19 * 4.0F - 1.0F) / 8.0F;
            double var46 = this.noise6[var15] / 8000.0D;

            if (var46 < 0.0D)
            {
               var46 = -var46 * 0.3D;
            }

            var46 = var46 * 3.0D - 2.0D;

            if (var46 < 0.0D)
            {
               var46 /= 2.0D;

               if (var46 < -1.0D)
               {
                  var46 = -1.0D;
               }

               var46 /= 1.4D;
               var46 /= 2.0D;
            }
            else
            {
               if (var46 > 1.0D)
               {
                  var46 = 1.0D;
               }

               var46 /= 8.0D;
            }

            ++var15;

            for (int var47 = 0; var47 < par6; ++var47)
            {
               double var48 = (double)var19;
               double var28 = (double)var18;
               var48 += var46 * 0.2D;
               var48 = var48 * (double)par6 / 16.0D;
               double var30 = (double)par6 / 2.0D + var48 * 4.0D;
               double var32 = 0.0D;
               double var34 = ((double)var47 - var30) * 12.0D * 128.0D / 128.0D / var28;

               if (var34 < 0.0D)
               {
                  var34 *= 4.0D;
               }

               double var36 = this.noise1[var14] / 512.0D;
               double var38 = this.noise2[var14] / 512.0D;
               double var40 = (this.noise3[var14] / 10.0D + 1.0D) / 2.0D;

               if (var40 < 0.0D)
               {
                  var32 = var36;
               }
               else if (var40 > 1.0D)
               {
                  var32 = var38;
               }
               else
               {
                  var32 = var36 + (var38 - var36) * var40;
               }

               var32 -= var34;

               if (var47 > par6 - 4)
               {
                  double var42 = (double)((float)(var47 - (par6 - 4)) / 3.0F);
                  var32 = var32 * (1.0D - var42) + -10.0D * var42;
               }

               par1ArrayOfDouble[var14] = var32;
               ++var14;
            }
         }
      }

      return par1ArrayOfDouble;
   }

   /**
    * Checks to see if a chunk exists at x, y
    */
   public boolean chunkExists(int par1, int par2)
   {
      return true;
   }

   public Chunk getChunkIfItExists(int chunk_x, int chunk_z)
   {
      Minecraft.setErrorMessage("getChunkIfItExists: called for ChunkProviderGenerate");
      return null;
   }

   /**
    * Populates chunk with ores etc etc
    */
   public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
   {
      BlockFalling.fallInstantly = true;
      int var4 = par2 * 16;
      int var5 = par3 * 16;
      BiomeGenBase var6 = this.worldObj.getBiomeGenForCoords(var4 + 16, var5 + 16);
      this.rand.setSeed(this.worldObj.getSeed());
      long var7 = this.rand.nextLong() / 2L * 2L + 1L;
      long var9 = this.rand.nextLong() / 2L * 2L + 1L;
      this.rand.setSeed((long)par2 * var7 + (long)par3 * var9 ^ this.worldObj.getSeed());
      boolean var11 = false;

      if (this.mapFeaturesEnabled)
      {
         this.mineshaftGenerator.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);
         var11 = this.villageGenerator.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);
         this.strongholdGenerator.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);
         this.scatteredFeatureGenerator.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);
      }

      int var12;
      int var13;
      int var14;
      int var15;

      for (var15 = 0; var15 < 4; ++var15)
      {
         if (!var11 && this.rand.nextInt(6) == 0)
         {
            var12 = var4 + this.rand.nextInt(16) + 8;
            var13 = this.rand.nextInt(128);
            var14 = var5 + this.rand.nextInt(16) + 8;
            int var16;

            for (var16 = 1; var16 < var15; ++var16)
            {
               if (var13 > 16)
               {
                  var13 = this.rand.nextInt(var13);
               }
            }

            if (Math.random() * 32.0D >= (double)(var13 - 16))
            {
               if (this.rand.nextInt(20) == 0)
               {
                  var16 = Block.waterStill.blockID;
               }
               else
               {
                  var16 = Block.lavaStill.blockID;
               }
            }
            else
            {
               if (var6 == BiomeGenBase.desert || var6 == BiomeGenBase.desertHills)
               {
                  continue;
               }

               var16 = Block.waterStill.blockID;
            }

            (new WorldGenLakes(var16)).generate(this.worldObj, this.rand, var12, var13, var14);
         }
      }

      for (var12 = 0; var12 < 8; ++var12)
      {
         var13 = var4 + this.rand.nextInt(16) + 8;
         var14 = this.rand.nextInt(128);
         var15 = var5 + this.rand.nextInt(16) + 8;
         (new WorldGenDungeons()).generate(this.worldObj, this.rand, var13, var14, var15);
      }

      var6.decorate(this.worldObj, this.rand, var4, var5);
      var4 += 8;
      var5 += 8;

      for (var12 = 0; var12 < 16; ++var12)
      {
         for (var13 = 0; var13 < 16; ++var13)
         {
            var14 = this.worldObj.getPrecipitationHeight(var4 + var12, var5 + var13);

            if (this.worldObj.isBlockFreezable(var12 + var4, var14 - 1, var13 + var5))
            {
               this.worldObj.setBlock(var12 + var4, var14 - 1, var13 + var5, Block.ice.blockID, 0, 2);
            }

            if (var14 > 63 && this.worldObj.isAirBlock(var12 + var4, 63, var13 + var5) && this.worldObj.isBlockFreezable(var12 + var4, 62, var13 + var5))
            {
               this.worldObj.setBlock(var12 + var4, 62, var13 + var5, Block.ice.blockID, 0, 2);
            }

            if (this.worldObj.canSnowAt(var12 + var4, var14, var13 + var5))
            {
               this.worldObj.setBlock(var12 + var4, var14, var13 + var5, Block.snow.blockID, 0, 2);
            }
         }
      }

      SpawnerAnimals.performWorldGenSpawning(this.worldObj, var6, EnumCreatureType.animal, var4, var5, 16, 16, this.rand);
      SpawnerAnimals.performWorldGenSpawning(this.worldObj, var6, EnumCreatureType.aquatic, var4, var5, 16, 16, this.rand);
      BlockFalling.fallInstantly = false;
   }

   /**
    * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
    * Return true if all chunks have been saved.
    */
   public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
   {
      return true;
   }

   /**
    * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
    * unimplemented.
    */
   public void saveExtraData() {}

   /**
    * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
    */
   public boolean unloadQueuedChunks()
   {
      return false;
   }

   /**
    * Returns if the IChunkProvider supports saving.
    */
   public boolean canSave()
   {
      return true;
   }

   /**
    * Converts the instance data to a readable string.
    */
   public String makeString()
   {
      return "RandomLevelSource";
   }

   /**
    * Returns a list of creatures of the specified type that can spawn at the given location.
    */
   public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
   {
      BiomeGenBase var5 = this.worldObj.getBiomeGenForCoords(par2, par4);
      return var5 == null ? null : (par1EnumCreatureType == EnumCreatureType.monster && this.scatteredFeatureGenerator.func_143030_a(par2, par3, par4) ? this.scatteredFeatureGenerator.getScatteredFeatureSpawnList() : var5.getSpawnableList(par1EnumCreatureType));
   }

   /**
    * Returns the location of the closest structure of the specified type. If not found returns null.
    */
   public ChunkPosition findClosestStructure(World par1World, String par2Str, int par3, int par4, int par5)
   {
      return "Stronghold".equals(par2Str) && this.strongholdGenerator != null ? this.strongholdGenerator.getNearestInstance(par1World, par3, par4, par5) : null;
   }

   public int getLoadedChunkCount()
   {
      return 0;
   }

   public void recreateStructures(int par1, int par2)
   {
      if (this.mapFeaturesEnabled)
      {
         this.mineshaftGenerator.generate(this, this.worldObj, par1, par2, (byte[])null);
         this.villageGenerator.generate(this, this.worldObj, par1, par2, (byte[])null);
         this.strongholdGenerator.generate(this, this.worldObj, par1, par2, (byte[])null);
         this.scatteredFeatureGenerator.generate(this, this.worldObj, par1, par2, (byte[])null);
      }
   }

   public MapGenCaveNetwork getMapGenCaveNetwork()
   {
      return this.cave_network_generator;
   }

   private void performSandFalls(HashMap pending_sand_falls, byte[] block_ids)
   {
      byte block_id_sand = (byte)Block.sand.blockID;
      byte block_id_web = (byte)Block.web.blockID;
      Iterator i = pending_sand_falls.entrySet().iterator();
      label43:

      while (i.hasNext())
      {
         Map.Entry entry = (Map.Entry)i.next();
         int xz_index = ((Integer)entry.getKey()).intValue();
         int y = ((Integer)entry.getValue()).intValue();
         int local_x = xz_index % 16;
         int local_z = xz_index / 16;
         int index_at_y_equals_0 = local_x << 11 | local_z << 7;
         int index = index_at_y_equals_0 | y;

         if (block_ids[index] == block_id_sand)
         {
            int num_sand_blocks = 1;

            while (true)
            {
               ++index;

               if (block_ids[index] != block_id_sand)
               {
                  int max_y = y + num_sand_blocks - 1;

                  while (true)
                  {
                     byte block_id = block_ids[index_at_y_equals_0 + y - 1];

                     if (block_id != 0 && block_id != block_id_web)
                     {
                        --y;

                        while (true)
                        {
                           ++y;

                           if (y > max_y)
                           {
                              continue label43;
                           }

                           int var10001 = index_at_y_equals_0 + y;
                           --num_sand_blocks;
                           block_ids[var10001] = num_sand_blocks < 0 ? 0 : block_id_sand;
                        }
                     }

                     --y;
                  }
               }

               ++num_sand_blocks;
            }
         }
      }
   }
}
