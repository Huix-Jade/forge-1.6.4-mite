package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.Minecraft;
import net.minecraft.world.CaveNetworkStub;
import net.minecraft.world.World;
import net.minecraft.world.WorldGenPlants;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeDecorator {
   protected World currentWorld;
   protected Random randomGenerator;
   protected int chunk_X;
   protected int chunk_Z;
   protected BiomeGenBase biome;
   protected WorldGenerator clayGen = new WorldGenClay(4);
   protected WorldGenerator sandGen;
   protected WorldGenerator gravelAsSandGen;
   protected WorldGenMinable dirtGen;
   protected WorldGenMinable gravelGen;
   protected WorldGenMinable coalGen;
   protected WorldGenMinable ironGen;
   protected WorldGenMinable copperGen;
   protected WorldGenMinable silverGen;
   protected WorldGenMinable mithrilGen;
   protected WorldGenMinable adamantiteGen;
   protected WorldGenMinable goldGen;
   protected WorldGenMinable redstoneGen;
   protected WorldGenMinable diamondGen;
   protected WorldGenMinable lapisGen;
   protected WorldGenMinable silverfishGen;
   protected WorldGenerator plantYellowGen;
   protected WorldGenFlowers plantRedGen;
   protected WorldGenerator mushroomBrownGen;
   protected WorldGenerator mushroomRedGen;
   public WorldGenerator bigMushroomGen;
   protected WorldGenerator reedGen;
   protected WorldGenerator cactusGen;
   protected WorldGenerator waterlilyGen;
   protected int waterlilyPerChunk;
   protected int treesPerChunk;
   protected int flowersPerChunk;
   protected int grassPerChunk;
   protected int deadBushPerChunk;
   protected int surface_mushrooms_per_chunk;
   protected int reedsPerChunk;
   protected int cactiPerChunk;
   protected int sandPerChunk;
   protected int sandPerChunk2;
   protected int clayPerChunk;
   protected int bigMushroomsPerChunk;
   protected int bush_patches_per_chunk_tenths;
   public boolean generateLakes;
   protected WorldGenPlants bush_gen;

   public BiomeDecorator(BiomeGenBase par1BiomeGenBase) {
      this.sandGen = new WorldGenSand(7, Block.sand.blockID);
      this.gravelAsSandGen = new WorldGenSand(6, Block.gravel.blockID);
      this.plantYellowGen = new WorldGenFlowers(Block.plantYellow.blockID);
      this.plantRedGen = new WorldGenFlowers(Block.plantRed.blockID);
      this.mushroomBrownGen = new WorldGenFlowers(Block.mushroomBrown.blockID);
      this.mushroomRedGen = new WorldGenFlowers(Block.mushroomRed.blockID);
      this.bigMushroomGen = new WorldGenBigMushroom();
      this.reedGen = new WorldGenReed();
      this.cactusGen = new WorldGenCactus();
      this.waterlilyGen = new WorldGenWaterlily();
      this.flowersPerChunk = 2;
      this.grassPerChunk = 1;
      this.sandPerChunk = 1;
      this.sandPerChunk2 = 3;
      this.clayPerChunk = 1;
      this.generateLakes = true;
      this.biome = par1BiomeGenBase;
      this.dirtGen = new WorldGenMinable(Block.dirt.blockID, 32);
      this.gravelGen = new WorldGenMinable(Block.gravel.blockID, 32);
      this.coalGen = new WorldGenMinable(Block.oreCoal.blockID, 16);
      this.copperGen = new WorldGenMinable(Block.oreCopper.blockID, 6);
      this.silverGen = new WorldGenMinable(Block.oreSilver.blockID, 6);
      this.goldGen = new WorldGenMinable(Block.oreGold.blockID, 4);
      this.ironGen = new WorldGenMinable(Block.oreIron.blockID, 6);
      this.mithrilGen = new WorldGenMinable(Block.oreMithril.blockID, 3);
      this.adamantiteGen = new WorldGenMinable(Block.oreAdamantium.blockID, 3);
      this.redstoneGen = new WorldGenMinable(Block.oreRedstone.blockID, 5);
      this.diamondGen = new WorldGenMinable(Block.oreDiamond.blockID, 3);
      this.lapisGen = new WorldGenMinable(Block.oreLapis.blockID, 3);
      this.silverfishGen = new WorldGenMinable(Block.silverfish.blockID, 3);
      this.bush_gen = new WorldGenPlants(Block.bush);
   }

   public void decorate(World par1World, Random par2Random, int par3, int par4) {
      if (this.currentWorld != null) {
         throw new RuntimeException("Already decorating!!");
      } else {
         this.currentWorld = par1World;
         this.randomGenerator = par2Random;
         this.randomGenerator.setSeed((long)(par3 + par4 * 65536) + par1World.getSeed() * 4294967296L);
         this.chunk_X = par3;
         this.chunk_Z = par4;
         this.decorate();
         this.currentWorld = null;
         this.randomGenerator = null;
      }
   }

   protected void decorate() {
      this.currentWorld.decorating = true;
      this.generateOres();

      int var1;
      int var2;
      int var3;
      for(var1 = 0; var1 < this.sandPerChunk2; ++var1) {
         var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.sandGen.generate(this.currentWorld, this.randomGenerator, var2, this.currentWorld.getTopSolidOrLiquidBlock(var2, var3), var3);
      }

      for(var1 = 0; var1 < this.clayPerChunk; ++var1) {
         var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.clayGen.generate(this.currentWorld, this.randomGenerator, var2, this.currentWorld.getTopSolidOrLiquidBlock(var2, var3), var3);
      }

      for(var1 = 0; var1 < this.sandPerChunk; ++var1) {
         var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.sandGen.generate(this.currentWorld, this.randomGenerator, var2, this.currentWorld.getTopSolidOrLiquidBlock(var2, var3), var3);
      }

      var1 = this.treesPerChunk;
      if (this.randomGenerator.nextInt(10) == 0) {
         ++var1;
      }

      int var4;
      for(var2 = 0; var2 < var1; ++var2) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         WorldGenerator var5 = this.biome.getRandomWorldGenForTrees(this.randomGenerator);
         var5.setScale(1.0, 1.0, 1.0);
         var5.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getHeightValue(var3, var4), var4);
      }

      int var7;
      if (this.biome == BiomeGenBase.plains && this.randomGenerator.nextInt(400) == 0) {
         var7 = this.biome.worldGeneratorBigTree.heightLimit;
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.biome.worldGeneratorBigTree.setHeightLimit(10 + this.randomGenerator.nextInt(5));
         WorldGenerator var5 = this.biome.worldGeneratorBigTree;
         var5.setScale(1.0, 1.0, 1.0);
         var5.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getHeightValue(var3, var4), var4);
         this.biome.worldGeneratorBigTree.setHeightLimit(var7);
      }

      for(var2 = 0; var2 < this.bigMushroomsPerChunk; ++var2) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getHeightValue(var3, var4), var4);
      }

      int bush_patches_per_chunk;
      for(var2 = 0; var2 < this.flowersPerChunk; ++var2) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.randomGenerator.nextInt(128);
         var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.plantYellowGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
         if (this.randomGenerator.nextInt(this.biome.isSwampBiome() ? 3 : 2) == 0) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(128);
            var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            bush_patches_per_chunk = Block.plantRed.getRandomSubtypeForBiome(this.randomGenerator, this.biome);
            if (bush_patches_per_chunk >= 0) {
               this.plantRedGen.setMetadata(bush_patches_per_chunk);
               this.plantRedGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
            }
         }
      }

      for(var2 = 0; var2 < this.grassPerChunk; ++var2) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.randomGenerator.nextInt(128);
         var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         WorldGenerator var6 = this.biome.getRandomWorldGenForGrass(this.randomGenerator);
         var6.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
      }

      for(var2 = 0; var2 < this.deadBushPerChunk; ++var2) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.randomGenerator.nextInt(128);
         var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         (new WorldGenDeadBush(Block.deadBush.blockID)).generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
      }

      for(var2 = 0; var2 < this.waterlilyPerChunk; ++var2) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;

         for(var7 = this.randomGenerator.nextInt(128); var7 > 0 && this.currentWorld.getBlockId(var3, var7 - 1, var4) == 0; --var7) {
         }

         this.waterlilyGen.generate(this.currentWorld, this.randomGenerator, var3, var7, var4);
      }

      for(var2 = 0; var2 < this.surface_mushrooms_per_chunk; ++var2) {
         if (this.randomGenerator.nextInt(6) == 0) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var7 = this.randomGenerator.nextInt(128);
            this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, var3, var7, var4);
         }
      }

      if (this.randomGenerator.nextInt(6) == 0) {
         var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var3 = this.randomGenerator.nextInt(128);
         var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, var2, var3, var4);
      }

      for(bush_patches_per_chunk = 0; bush_patches_per_chunk < 4; ++bush_patches_per_chunk) {
         if (this.currentWorld.isUnderworld()) {
            if (this.randomGenerator.nextInt(4) == 0) {
               var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
               var3 = this.randomGenerator.nextInt(128);
               var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
               this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, var2, var3, var4);
            }
            break;
         }

         if (this.randomGenerator.nextInt(4) == 0) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.randomGenerator.nextInt(32) + 48;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, var2, var3, var4);
         }

         if (this.randomGenerator.nextInt(4) == 0) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.randomGenerator.nextInt(128);
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, var2, var3, var4);
         }
      }

      if (this.biome.temperature >= 0.3F) {
         for(var2 = 0; var2 < this.reedsPerChunk; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var7 = this.randomGenerator.nextInt(128);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, var3, var7, var4);
         }

         for(var2 = 0; var2 < 10; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(128);
            var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.reedGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
         }
      }

      if (this.randomGenerator.nextInt(32) == 0) {
         var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var3 = this.randomGenerator.nextInt(128);
         var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         (new WorldGenPumpkin()).generate(this.currentWorld, this.randomGenerator, var2, var3, var4);
      }

      for(var2 = 0; var2 < this.cactiPerChunk; ++var2) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.randomGenerator.nextInt(128);
         var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.cactusGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
      }

      bush_patches_per_chunk = this.bush_patches_per_chunk_tenths / 10 + (this.randomGenerator.nextInt(10) < this.bush_patches_per_chunk_tenths % 10 ? 1 : 0);

      for(var2 = 0; var2 < bush_patches_per_chunk; ++var2) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.randomGenerator.nextInt(128);
         var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.bush_gen.setMetadata(BlockBush.getMetadataForBushWithBerries(0));
         this.bush_gen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
      }

      if (this.generateLakes) {
         for(int chunk_dx = -1; chunk_dx <= 1; ++chunk_dx) {
            for(int chunk_dz = -1; chunk_dz <= 1; ++chunk_dz) {
               Chunk chunk = this.currentWorld.getChunkFromBlockCoordsIfItExists(this.chunk_X + chunk_dx * 16, this.chunk_Z + chunk_dz * 16);
               if (chunk != null && chunk.getHadNaturallyOccurringMycelium()) {
                  this.currentWorld.decorating = false;
                  return;
               }
            }
         }

         for(var2 = 0; var2 < 70; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(this.randomGenerator.nextInt(120) + 8);
            var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            CaveNetworkStub stub = this.currentWorld.getAsWorldServer().getCaveNetworkStubAt(var3 >> 4, var7 >> 4);
            boolean prevent_water = false;
            boolean prevent_lava = false;
            if (stub != null) {
               if (stub.hasMycelium() || stub.preventsAllLiquids() || this.randomGenerator.nextFloat() < 0.67F) {
                  continue;
               }

               prevent_water = !stub.allowsWater();
               prevent_lava = !stub.allowsLava();
            }

            int liquid_block_id;
            if (this.randomGenerator.nextInt(32) + 16 < var4) {
               if (prevent_water) {
                  continue;
               }

               liquid_block_id = Block.waterMoving.blockID;
            } else if (this.randomGenerator.nextFloat() < 0.95F) {
               if (prevent_lava) {
                  continue;
               }

               liquid_block_id = Block.lavaMoving.blockID;
            } else {
               if (prevent_water) {
                  continue;
               }

               liquid_block_id = Block.waterMoving.blockID;
            }

            WorldGenLiquids.generate(this.currentWorld, this.randomGenerator, liquid_block_id, var3, var4 + this.currentWorld.underworld_y_offset, var7);
         }
      }

      this.currentWorld.decorating = false;
   }

   protected void genMinable(int frequency, WorldGenMinable world_gen_minable) {
      this.genMinable(frequency, world_gen_minable, false);
   }

   protected void genMinable(int frequency, WorldGenMinable world_gen_minable, boolean vein_size_increases_with_depth) {
      int resource_multiplier = 1;
      frequency *= resource_multiplier;
      if (this.currentWorld.underworld_y_offset != 0 && world_gen_minable != this.gravelGen) {
         frequency *= 8;
         if (world_gen_minable == this.adamantiteGen) {
            frequency *= 2;
         }
      }

      while(frequency-- > 0) {
         if (this.randomGenerator.nextInt(10) == 0) {
            int x = this.chunk_X + this.randomGenerator.nextInt(16);
            int y = world_gen_minable.getRandomVeinHeight(this.currentWorld, this.randomGenerator);
            int z = this.chunk_Z + this.randomGenerator.nextInt(16);
            if (y >= 0) {
               world_gen_minable.generate(this.currentWorld, this.randomGenerator, x, y, z, vein_size_increases_with_depth);
            }
         }
      }

   }

   protected void generateOres() {
      if (this.currentWorld.isOverworld()) {
         this.genMinable(200, this.dirtGen);
         this.genMinable(200, this.gravelGen);
         this.genMinable(50, this.coalGen);
         this.genMinable(40, this.copperGen, true);
         this.genMinable(10, this.silverGen, true);
         this.genMinable(20, this.goldGen, true);
         this.genMinable(60, this.ironGen, true);
         this.genMinable(10, this.mithrilGen, true);
         this.genMinable(5, this.silverfishGen, true);
         this.genMinable(10, this.redstoneGen);
         this.genMinable(5, this.diamondGen);
         this.genMinable(5, this.lapisGen);
      } else if (this.currentWorld.isUnderworld()) {
         this.genMinable(300, this.gravelGen);
         this.genMinable(40, this.copperGen, true);
         this.genMinable(10, this.silverGen, true);
         this.genMinable(20, this.goldGen, true);
         this.genMinable(60, this.ironGen, true);
         this.genMinable(10, this.mithrilGen, true);
         this.genMinable(5, this.adamantiteGen, true);
         this.genMinable(10, this.redstoneGen);
         this.genMinable(5, this.diamondGen);
         this.genMinable(5, this.lapisGen);
         if (this.currentWorld.underworld_y_offset != 0) {
            this.genMinable(50, this.silverfishGen);
         }
      } else if (!this.currentWorld.isTheEnd()) {
         Minecraft.setErrorMessage("generateOres: don't know how to handle world " + this.currentWorld);
      }

   }
}
