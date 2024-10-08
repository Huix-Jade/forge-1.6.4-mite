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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.*;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.*;

public class BiomeDecorator {
   public World currentWorld;
   public Random randomGenerator;
   public int chunk_X;
   public int chunk_Z;
   public BiomeGenBase biome;
   public WorldGenerator clayGen = new WorldGenClay(4);
   public WorldGenerator sandGen;
   public WorldGenerator gravelAsSandGen;
   public WorldGenMinable dirtGen;
   public WorldGenMinable gravelGen;
   public WorldGenMinable coalGen;
   public WorldGenMinable ironGen;
   public WorldGenMinable copperGen;
   public WorldGenMinable silverGen;
   public WorldGenMinable mithrilGen;
   public WorldGenMinable adamantiteGen;
   public WorldGenMinable goldGen;
   public WorldGenMinable redstoneGen;
   public WorldGenMinable diamondGen;
   public WorldGenMinable lapisGen;
   public WorldGenMinable silverfishGen;
   public WorldGenerator plantYellowGen;
   public WorldGenFlowers plantRedGen;
   public WorldGenerator mushroomBrownGen;
   public WorldGenerator mushroomRedGen;
   public WorldGenerator bigMushroomGen;
   public WorldGenerator reedGen;
   public WorldGenerator cactusGen;
   public WorldGenerator waterlilyGen;
   public int waterlilyPerChunk;
   public int treesPerChunk;
   public int flowersPerChunk;
   public int grassPerChunk;
   public int deadBushPerChunk;
   public int surface_mushrooms_per_chunk;
   public int reedsPerChunk;
   public int cactiPerChunk;
   public int sandPerChunk;
   public int sandPerChunk2;
   public int clayPerChunk;
   public int bigMushroomsPerChunk;
   public int bush_patches_per_chunk_tenths;
   public boolean generateLakes;
   public WorldGenPlants bush_gen;

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
      MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(currentWorld, randomGenerator, chunk_X, chunk_Z));
      this.currentWorld.decorating = true;
      this.generateOres();

      int i;
      int j;
      int var3;
      boolean doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, SAND);
      for (i = 0; doGen && i < this.sandPerChunk2; ++i) {
         j = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.sandGen.generate(this.currentWorld, this.randomGenerator, j, this.currentWorld.getTopSolidOrLiquidBlock(j, var3), var3);
      }

      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, SAND_PASS2);
      for (i = 0; doGen && i < this.sandPerChunk; ++i) {
         j = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.clayGen.generate(this.currentWorld, this.randomGenerator, j, this.currentWorld.getTopSolidOrLiquidBlock(j, var3), var3);
      }

      for(i = 0; i < this.sandPerChunk; ++i) {
         j = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.sandGen.generate(this.currentWorld, this.randomGenerator, j, this.currentWorld.getTopSolidOrLiquidBlock(j, var3), var3);
      }

      i = this.treesPerChunk;
      if (this.randomGenerator.nextInt(10) == 0) {
         ++i;
      }

      int var4;
      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, TREE);
      for (j = 0; doGen && j < i; ++j) {
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

      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, BIG_SHROOM);
      for (j = 0; doGen && j < this.bigMushroomsPerChunk; ++j) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getHeightValue(var3, var4), var4);
      }

      int bush_patches_per_chunk;
      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, FLOWERS);
      for (j = 0; doGen && j < this.flowersPerChunk; ++j) {
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

      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, GRASS);
      for (j = 0; doGen && j < this.grassPerChunk; ++j) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.randomGenerator.nextInt(128);
         var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         WorldGenerator var6 = this.biome.getRandomWorldGenForGrass(this.randomGenerator);
         var6.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
      }

      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, DEAD_BUSH);
      for (j = 0; doGen && j < this.deadBushPerChunk; ++j) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.randomGenerator.nextInt(128);
         var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         (new WorldGenDeadBush(Block.deadBush.blockID)).generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
      }

      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, LILYPAD);
      for (j = 0; doGen && j < this.waterlilyPerChunk; ++j)
      {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;

         for(var7 = this.randomGenerator.nextInt(128); var7 > 0 && this.currentWorld.getBlockId(var3, var7 - 1, var4) == 0; --var7) {
         }

         this.waterlilyGen.generate(this.currentWorld, this.randomGenerator, var3, var7, var4);
      }

      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, SHROOM);
      for (j = 0; doGen && j < this.surface_mushrooms_per_chunk; ++j)
      {
         if (this.randomGenerator.nextInt(6) == 0) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var7 = this.randomGenerator.nextInt(128);
            this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, var3, var7, var4);
         }
      }

      if (doGen && this.randomGenerator.nextInt(6) == 0) {
         j = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var3 = this.randomGenerator.nextInt(128);
         var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, j, var3, var4);
      }

      for(bush_patches_per_chunk = 0; bush_patches_per_chunk < 4; ++bush_patches_per_chunk) {
         if (this.currentWorld.isUnderworld()) {
            if (this.randomGenerator.nextInt(4) == 0) {
               j = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
               var3 = this.randomGenerator.nextInt(128);
               var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
               this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, j, var3, var4);
            }
            break;
         }

         if (doGen && this.randomGenerator.nextInt(4) == 0) {
            j = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.randomGenerator.nextInt(32) + 48;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, j, var3, var4);
         }

         if (doGen && this.randomGenerator.nextInt(4) == 0) {
            j = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.randomGenerator.nextInt(128);
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, j, var3, var4);
         }
      }

      if (this.biome.temperature >= 0.3F) {
         doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, REED);
         for (j = 0; doGen && j < this.reedsPerChunk; ++j) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var7 = this.randomGenerator.nextInt(128);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, var3, var7, var4);
         }

         for(j = 0; doGen && j < 10; ++j) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(128);
            var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.reedGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
         }
      }

      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, PUMPKIN);
      if (doGen && this.randomGenerator.nextInt(32) == 0) {
         j = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var3 = this.randomGenerator.nextInt(128);
         var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         (new WorldGenPumpkin()).generate(this.currentWorld, this.randomGenerator, j, var3, var4);
      }

      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, CACTUS);
      for (j = 0; doGen && j < this.cactiPerChunk; ++j) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.randomGenerator.nextInt(128);
         var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.cactusGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
      }

      bush_patches_per_chunk = this.bush_patches_per_chunk_tenths / 10 + (this.randomGenerator.nextInt(10) < this.bush_patches_per_chunk_tenths % 10 ? 1 : 0);

      for(j = 0; j < bush_patches_per_chunk; ++j) {
         var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
         var4 = this.randomGenerator.nextInt(128);
         var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
         this.bush_gen.setMetadata(BlockBush.getMetadataForBushWithBerries(0));
         this.bush_gen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
      }

      doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, LAKE);
      if (doGen && this.generateLakes) {
         for(int chunk_dx = -1; chunk_dx <= 1; ++chunk_dx) {
            for(int chunk_dz = -1; chunk_dz <= 1; ++chunk_dz) {
               Chunk chunk = this.currentWorld.getChunkFromBlockCoordsIfItExists(this.chunk_X + chunk_dx * 16, this.chunk_Z + chunk_dz * 16);
               if (chunk != null && chunk.getHadNaturallyOccurringMycelium()) {
                  this.currentWorld.decorating = false;
                  return;
               }
            }
         }

         for(j = 0; j < 70; ++j) {
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
      MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(currentWorld, randomGenerator, chunk_X, chunk_Z));
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
         MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(currentWorld, randomGenerator, chunk_X, chunk_Z));
         if (TerrainGen.generateOre(currentWorld, randomGenerator, dirtGen, chunk_X, chunk_Z, DIRT))
            this.genMinable(200, this.dirtGen);
         if (TerrainGen.generateOre(currentWorld, randomGenerator, gravelGen, chunk_X, chunk_Z, GRAVEL))
            this.genMinable(200, this.gravelGen);
         if (TerrainGen.generateOre(currentWorld, randomGenerator, coalGen, chunk_X, chunk_Z, COAL))
            this.genMinable(50, this.coalGen);
         this.genMinable(40, this.copperGen, true);
         this.genMinable(10, this.silverGen, true);
         if (TerrainGen.generateOre(currentWorld, randomGenerator, goldGen, chunk_X, chunk_Z, GOLD))
            this.genMinable(20, this.goldGen, true);
         if (TerrainGen.generateOre(currentWorld, randomGenerator, ironGen, chunk_X, chunk_Z, IRON))
            this.genMinable(60, this.ironGen, true);
         this.genMinable(10, this.mithrilGen, true);
         this.genMinable(5, this.silverfishGen, true);
         if (TerrainGen.generateOre(currentWorld, randomGenerator, redstoneGen, chunk_X, chunk_Z, REDSTONE))
            this.genMinable(10, this.redstoneGen);
         if (TerrainGen.generateOre(currentWorld, randomGenerator, diamondGen, chunk_X, chunk_Z, DIAMOND))
            this.genMinable(5, this.diamondGen);
         if (TerrainGen.generateOre(currentWorld, randomGenerator, lapisGen, chunk_X, chunk_Z, LAPIS))
            this.genMinable(5, this.lapisGen);
         MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(currentWorld, randomGenerator, chunk_X, chunk_Z));
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
