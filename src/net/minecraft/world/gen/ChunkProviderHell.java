package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public final class ChunkProviderHell implements IChunkProvider {
   private Random hellRNG;
   private NoiseGeneratorOctaves netherNoiseGen1;
   private NoiseGeneratorOctaves netherNoiseGen2;
   private NoiseGeneratorOctaves netherNoiseGen3;
   private NoiseGeneratorOctaves slowsandGravelNoiseGen;
   private NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
   public NoiseGeneratorOctaves netherNoiseGen6;
   public NoiseGeneratorOctaves netherNoiseGen7;
   private World worldObj;
   private double[] noiseField;
   public MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
   private double[] slowsandNoise = new double[256];
   private double[] gravelNoise = new double[256];
   private double[] netherrackExclusivityNoise = new double[256];
   private MapGenBase netherCaveGenerator = new MapGenCavesHell();
   double[] noiseData1;
   double[] noiseData2;
   double[] noiseData3;
   double[] noiseData4;
   double[] noiseData5;

   public ChunkProviderHell(World par1World, long par2) {
      this.worldObj = par1World;
      this.hellRNG = new Random(par2);
      this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
      this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
      this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
      this.netherNoiseGen6 = new NoiseGeneratorOctaves(this.hellRNG, 10);
      this.netherNoiseGen7 = new NoiseGeneratorOctaves(this.hellRNG, 16);
   }

   public void generateNetherTerrain(int par1, int par2, byte[] par3ArrayOfByte) {
      byte var4 = 4;
      byte var5 = 32;
      int var6 = var4 + 1;
      byte var7 = 17;
      int var8 = var4 + 1;
      this.noiseField = this.initializeNoiseField(this.noiseField, par1 * var4, 0, par2 * var4, var6, var7, var8);

      for(int var9 = 0; var9 < var4; ++var9) {
         for(int var10 = 0; var10 < var4; ++var10) {
            for(int var11 = 0; var11 < 16; ++var11) {
               double var12 = 0.125;
               double var14 = this.noiseField[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 0];
               double var16 = this.noiseField[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 0];
               double var18 = this.noiseField[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 0];
               double var20 = this.noiseField[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 0];
               double var22 = (this.noiseField[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 1] - var14) * var12;
               double var24 = (this.noiseField[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
               double var26 = (this.noiseField[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 1] - var18) * var12;
               double var28 = (this.noiseField[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

               for(int var30 = 0; var30 < 8; ++var30) {
                  double var31 = 0.25;
                  double var33 = var14;
                  double var35 = var16;
                  double var37 = (var18 - var14) * var31;
                  double var39 = (var20 - var16) * var31;

                  for(int var41 = 0; var41 < 4; ++var41) {
                     int var42 = var41 + var9 * 4 << 11 | 0 + var10 * 4 << 7 | var11 * 8 + var30;
                     short var43 = 128;
                     double var44 = 0.25;
                     double var46 = var33;
                     double var48 = (var35 - var33) * var44;

                     for(int var50 = 0; var50 < 4; ++var50) {
                        int var51 = 0;
                        if (var11 * 8 + var30 < var5) {
                           var51 = Block.lavaStill.blockID;
                        }

                        if (var46 > 0.0) {
                           var51 = Block.netherrack.blockID;
                        }

                        par3ArrayOfByte[var42] = (byte)var51;
                        var42 += var43;
                        var46 += var48;
                     }

                     var33 += var37;
                     var35 += var39;
                  }

                  var14 += var22;
                  var16 += var24;
                  var18 += var26;
                  var20 += var28;
               }
            }
         }
      }

   }

   public void replaceBlocksForBiome(int par1, int par2, byte[] par3ArrayOfByte) {
      byte var4 = 64;
      double var5 = 0.03125;
      this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, par1 * 16, par2 * 16, 0, 16, 16, 1, var5, var5, 1.0);
      this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, par1 * 16, 109, par2 * 16, 16, 1, 16, var5, 1.0, var5);
      this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen.generateNoiseOctaves(this.netherrackExclusivityNoise, par1 * 16, par2 * 16, 0, 16, 16, 1, var5 * 2.0, var5 * 2.0, var5 * 2.0);

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            boolean var9 = this.slowsandNoise[var7 + var8 * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
            boolean var10 = this.gravelNoise[var7 + var8 * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
            int var11 = (int)(this.netherrackExclusivityNoise[var7 + var8 * 16] / 3.0 + 3.0 + this.hellRNG.nextDouble() * 0.25);
            int var12 = -1;
            byte var13 = (byte)Block.netherrack.blockID;
            byte var14 = (byte)Block.netherrack.blockID;

            for(int var15 = 127; var15 >= 0; --var15) {
               int var16 = (var8 * 16 + var7) * 128 + var15;
               if (var15 < 127 - this.hellRNG.nextInt(5) && var15 > 0) {
                  byte var17 = par3ArrayOfByte[var16];
                  if (var17 == 0) {
                     var12 = -1;
                  } else if (var17 == Block.netherrack.blockID) {
                     if (var12 == -1) {
                        if (var11 <= 0) {
                           var13 = 0;
                           var14 = (byte)Block.netherrack.blockID;
                        } else if (var15 >= var4 - 4 && var15 <= var4 + 1) {
                           var13 = (byte)Block.netherrack.blockID;
                           var14 = (byte)Block.netherrack.blockID;
                           if (var10) {
                              var13 = (byte)Block.gravel.blockID;
                           }

                           if (var10) {
                              var14 = (byte)Block.netherrack.blockID;
                           }

                           if (var9) {
                              var13 = (byte)Block.slowSand.blockID;
                           }

                           if (var9) {
                              var14 = (byte)Block.slowSand.blockID;
                           }
                        }

                        if (var15 < var4 && var13 == 0) {
                           var13 = (byte)Block.lavaStill.blockID;
                        }

                        var12 = var11;
                        if (var15 >= var4 - 1) {
                           par3ArrayOfByte[var16] = var13;
                        } else {
                           par3ArrayOfByte[var16] = var14;
                        }
                     } else if (var12 > 0) {
                        --var12;
                        par3ArrayOfByte[var16] = var14;
                     }
                  }
               } else {
                  par3ArrayOfByte[var16] = (byte)Block.mantleOrCore.blockID;
               }
            }
         }
      }

   }

   public Chunk loadChunk(int par1, int par2) {
      return this.provideChunk(par1, par2);
   }

   public Chunk provideChunk(int par1, int par2) {
      if (!this.worldObj.isChunkWithinBlockDomain(par1, par2)) {
         Chunk chunk = new Chunk(this.worldObj, par1, par2);
         chunk.generateHeightMap(false);
         return chunk;
      } else {
         this.hellRNG.setSeed((long)par1 * 341873128712L + (long)par2 * 132897987541L);
         byte[] var3 = new byte['耀'];
         this.generateNetherTerrain(par1, par2, var3);
         this.replaceBlocksForBiome(par1, par2, var3);
         this.netherCaveGenerator.generate(this, this.worldObj, par1, par2, var3);
         this.genNetherBridge.generate(this, this.worldObj, par1, par2, var3);
         Chunk var4 = new Chunk(this.worldObj, var3, par1, par2);
         BiomeGenBase[] var5 = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, par1 * 16, par2 * 16, 16, 16);
         byte[] var6 = var4.getBiomeArray();

         for(int var7 = 0; var7 < var6.length; ++var7) {
            var6[var7] = (byte)var5[var7].biomeID;
         }

         var4.generateHeightMap(false);
         var4.resetRelightChecks();
         return var4;
      }
   }

   private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7) {
      if (par1ArrayOfDouble == null) {
         par1ArrayOfDouble = new double[par5 * par6 * par7];
      }

      double var8 = 684.412;
      double var10 = 2053.236;
      this.noiseData4 = this.netherNoiseGen6.generateNoiseOctaves(this.noiseData4, par2, par3, par4, par5, 1, par7, 1.0, 0.0, 1.0);
      this.noiseData5 = this.netherNoiseGen7.generateNoiseOctaves(this.noiseData5, par2, par3, par4, par5, 1, par7, 100.0, 0.0, 100.0);
      this.noiseData1 = this.netherNoiseGen3.generateNoiseOctaves(this.noiseData1, par2, par3, par4, par5, par6, par7, var8 / 80.0, var10 / 60.0, var8 / 80.0);
      this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, par2, par3, par4, par5, par6, par7, var8, var10, var8);
      this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, par2, par3, par4, par5, par6, par7, var8, var10, var8);
      int var12 = 0;
      int var13 = 0;
      double[] var14 = new double[par6];

      int var15;
      for(var15 = 0; var15 < par6; ++var15) {
         var14[var15] = Math.cos((double)var15 * Math.PI * 6.0 / (double)par6) * 2.0;
         double var16 = (double)var15;
         if (var15 > par6 / 2) {
            var16 = (double)(par6 - 1 - var15);
         }

         if (var16 < 4.0) {
            var16 = 4.0 - var16;
            var14[var15] -= var16 * var16 * var16 * 10.0;
         }
      }

      for(var15 = 0; var15 < par5; ++var15) {
         for(int var36 = 0; var36 < par7; ++var36) {
            double var17 = (this.noiseData4[var13] + 256.0) / 512.0;
            if (var17 > 1.0) {
               var17 = 1.0;
            }

            double var19 = 0.0;
            double var21 = this.noiseData5[var13] / 8000.0;
            if (var21 < 0.0) {
               var21 = -var21;
            }

            var21 = var21 * 3.0 - 3.0;
            if (var21 < 0.0) {
               var21 /= 2.0;
               if (var21 < -1.0) {
                  var21 = -1.0;
               }

               var21 /= 1.4;
               var21 /= 2.0;
               var17 = 0.0;
            } else {
               if (var21 > 1.0) {
                  var21 = 1.0;
               }

               var21 /= 6.0;
            }

            var17 += 0.5;
            var21 = var21 * (double)par6 / 16.0;
            ++var13;

            for(int var23 = 0; var23 < par6; ++var23) {
               double var24 = 0.0;
               double var26 = var14[var23];
               double var28 = this.noiseData2[var12] / 512.0;
               double var30 = this.noiseData3[var12] / 512.0;
               double var32 = (this.noiseData1[var12] / 10.0 + 1.0) / 2.0;
               if (var32 < 0.0) {
                  var24 = var28;
               } else if (var32 > 1.0) {
                  var24 = var30;
               } else {
                  var24 = var28 + (var30 - var28) * var32;
               }

               var24 -= var26;
               double var34;
               if (var23 > par6 - 4) {
                  var34 = (double)((float)(var23 - (par6 - 4)) / 3.0F);
                  var24 = var24 * (1.0 - var34) + -10.0 * var34;
               }

               if ((double)var23 < var19) {
                  var34 = (var19 - (double)var23) / 4.0;
                  if (var34 < 0.0) {
                     var34 = 0.0;
                  }

                  if (var34 > 1.0) {
                     var34 = 1.0;
                  }

                  var24 = var24 * (1.0 - var34) + -10.0 * var34;
               }

               par1ArrayOfDouble[var12] = var24;
               ++var12;
            }
         }
      }

      return par1ArrayOfDouble;
   }

   public boolean chunkExists(int par1, int par2) {
      return true;
   }

   public Chunk getChunkIfItExists(int chunk_x, int chunk_z) {
      Minecraft.setErrorMessage("getChunkIfItExists: called for ChunkProviderHell");
      return null;
   }

   public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {
      BlockFalling.fallInstantly = true;
      int var4 = par2 * 16;
      int var5 = par3 * 16;
      this.genNetherBridge.generateStructuresInChunk(this.worldObj, this.hellRNG, par2, par3);

      int var6;
      int var7;
      int var8;
      int var9;
      for(var6 = 0; var6 < 8; ++var6) {
         var7 = var4 + this.hellRNG.nextInt(16) + 8;
         var8 = this.hellRNG.nextInt(120) + 4;
         var9 = var5 + this.hellRNG.nextInt(16) + 8;
         (new WorldGenHellLava(Block.lavaMoving.blockID, false)).generate(this.worldObj, this.hellRNG, var7, var8, var9);
      }

      var6 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1;

      int var10;
      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var4 + this.hellRNG.nextInt(16) + 8;
         var9 = this.hellRNG.nextInt(120) + 4;
         var10 = var5 + this.hellRNG.nextInt(16) + 8;
         (new WorldGenFire()).generate(this.worldObj, this.hellRNG, var8, var9, var10);
      }

      var6 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1);

      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var4 + this.hellRNG.nextInt(16) + 8;
         var9 = this.hellRNG.nextInt(120) + 4;
         var10 = var5 + this.hellRNG.nextInt(16) + 8;
         (new WorldGenGlowStone1()).generate(this.worldObj, this.hellRNG, var8, var9, var10);
      }

      for(var7 = 0; var7 < 10; ++var7) {
         var8 = var4 + this.hellRNG.nextInt(16) + 8;
         var9 = this.hellRNG.nextInt(128);
         var10 = var5 + this.hellRNG.nextInt(16) + 8;
         (new WorldGenGlowStone2()).generate(this.worldObj, this.hellRNG, var8, var9, var10);
      }

      if (this.hellRNG.nextInt(1) == 0) {
         var7 = var4 + this.hellRNG.nextInt(16) + 8;
         var8 = this.hellRNG.nextInt(128);
         var9 = var5 + this.hellRNG.nextInt(16) + 8;
         (new WorldGenFlowers(Block.mushroomBrown.blockID)).generate(this.worldObj, this.hellRNG, var7, var8, var9);
      }

      for(int i = 0; i < 16; ++i) {
         var7 = var4 + this.hellRNG.nextInt(16) + 8;
         var8 = this.hellRNG.nextInt(128);
         var9 = var5 + this.hellRNG.nextInt(16) + 8;
         (new WorldGenDeadBush(Block.deadBush.blockID)).generate(this.worldObj, this.hellRNG, var7, var8, var9);
      }

      WorldGenMinable var12 = new WorldGenMinable(Block.oreNetherQuartz.blockID, 13, Block.netherrack.blockID);

      int var11;
      for(var8 = 0; var8 < 16; ++var8) {
         var9 = var4 + this.hellRNG.nextInt(16);
         var10 = this.hellRNG.nextInt(108) + 10;
         var11 = var5 + this.hellRNG.nextInt(16);
         var12.generate(this.worldObj, this.hellRNG, var9, var10, var11);
      }

      if (this.worldObj.worldInfo.getEarliestMITEReleaseRunIn() >= 139) {
         var12 = (new WorldGenMinable(Block.oreGold.blockID, 8, Block.netherrack.blockID)).setMinableBlockMetadata(2);

         for(var8 = 0; var8 < 2; ++var8) {
            var9 = var4 + this.hellRNG.nextInt(16);
            var10 = this.hellRNG.nextInt(108) + 10;
            var11 = var5 + this.hellRNG.nextInt(16);
            var12.generate(this.worldObj, this.hellRNG, var9, var10, var11);
         }
      }

      if (this.worldObj.worldInfo.getEarliestMITEReleaseRunIn() >= 0) {
         var12 = (new WorldGenMinable(Block.silverfish.blockID, 8, Block.netherrack.blockID)).setMinableBlockMetadata(3);
         int num_veins = this.hellRNG.nextInt(7) + 2;

         for(var8 = 0; var8 < num_veins; ++var8) {
            var9 = var4 + this.hellRNG.nextInt(16);
            var10 = this.hellRNG.nextInt(108) + 10;
            var11 = var5 + this.hellRNG.nextInt(16);
            var12.generate(this.worldObj, this.hellRNG, var9, var10, var11);
         }
      }

      for(var8 = 0; var8 < 16; ++var8) {
         var9 = var4 + this.hellRNG.nextInt(16);
         var10 = this.hellRNG.nextInt(108) + 10;
         var11 = var5 + this.hellRNG.nextInt(16);
         (new WorldGenHellLava(Block.lavaMoving.blockID, true)).generate(this.worldObj, this.hellRNG, var9, var10, var11);
      }

      BlockFalling.fallInstantly = false;
   }

   public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
      return true;
   }

   public void saveExtraData() {
   }

   public boolean unloadQueuedChunks() {
      return false;
   }

   public boolean canSave() {
      return true;
   }

   public String makeString() {
      return "HellRandomLevelSource";
   }

   public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4) {
      if (par1EnumCreatureType == EnumCreatureType.monster) {
         if (this.genNetherBridge.hasStructureAt(par2, par3, par4)) {
            return this.genNetherBridge.getSpawnList();
         }

         if (this.genNetherBridge.func_142038_b(par2, par3, par4) && this.worldObj.getBlockId(par2, par3 - 1, par4) == Block.netherBrick.blockID) {
            return this.genNetherBridge.getSpawnList();
         }
      }

      BiomeGenBase var5 = this.worldObj.getBiomeGenForCoords(par2, par4);
      return var5 == null ? null : var5.getSpawnableList(par1EnumCreatureType);
   }

   public ChunkPosition findClosestStructure(World par1World, String par2Str, int par3, int par4, int par5) {
      return null;
   }

   public int getLoadedChunkCount() {
      return 0;
   }

   public void recreateStructures(int par1, int par2) {
      this.genNetherBridge.generate(this, this.worldObj, par1, par2, (byte[])null);
   }
}
