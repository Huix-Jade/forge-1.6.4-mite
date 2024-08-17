package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.Debug;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraftforge.common.EnumPlantType;

public class BlockMushroom extends BlockPlant {
   protected BlockMushroom(int id) {
      super(id);
      float size = 0.2F;
      this.setBlockBoundsForAllThreads((double)(0.5F - size), 0.0, (double)(0.5F - size), (double)(0.5F + size), (double)(size * 2.0F), (double)(0.5F + size));
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used to track growth";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 4;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if (super.updateTick(world, x, y, z, random)) {
         return true;
      } else {
         int ran = random.nextInt(100);
         if (ran == 0) {
            byte var6 = 4;
            int var7 = 5;

            int var8;
            int var9;
            int var10;
            for(var8 = x - var6; var8 <= x + var6; ++var8) {
               for(var9 = z - var6; var9 <= z + var6; ++var9) {
                  for(var10 = y - 1; var10 <= y + 1; ++var10) {
                     if (world.getBlockId(var8, var10, var9) == this.blockID) {
                        --var7;
                        if (var7 <= 0) {
                           return false;
                        }
                     }
                  }
               }
            }

            var8 = x + random.nextInt(3) - 1;
            var9 = y + random.nextInt(2) - random.nextInt(2);
            var10 = z + random.nextInt(3) - 1;

            for(int var11 = 0; var11 < 4; ++var11) {
               if (world.isAirBlock(var8, var9, var10) && this.isLegalAt(world, var8, var9, var10, 0)) {
                  x = var8;
                  y = var9;
                  z = var10;
               }

               var8 = x + random.nextInt(3) - 1;
               var9 = y + random.nextInt(2) - random.nextInt(2);
               var10 = z + random.nextInt(3) - 1;
            }

            if (world.isAirBlock(var8, var9, var10) && this.isLegalAt(world, var8, var9, var10, 0)) {
               if (this == Block.mushroomBrown && world.getBlock(var8, var9 - 1, var10) == Block.mycelium && random.nextInt(4) > 0) {
                  return false;
               }

               world.setBlock(var8, var9, var10, this.blockID, 0, 2);
            }
         } else if (ran != 1 && ran != 2) {
            if (this == Block.mushroomBrown && ran < 10 && this.canConvertBlockBelowToMycelium(world, x, y, z)) {
               world.setBlock(x, y - 1, z, Block.mycelium.blockID);
            }
         } else if (this == Block.mushroomBrown) {
            this.tryGrowGiantMushroom(world, x, y, z, (EntityPlayer)null);
         }

         return false;
      }
   }

   public boolean canConvertBlockBelowToMycelium(World world, int x, int y, int z) {
      if (world.getBlock(x, y, z) != mushroomBrown) {
         return false;
      } else {
         --y;
         Block block = world.getBlock(x, y, z);
         if (block != tilledField) {
            return false;
         } else {
            BlockFarmland farmland = (BlockFarmland)block;
            int metadata = world.getBlockMetadata(x, y, z);
            if (BlockFarmland.getWetness(metadata) != 0 && BlockFarmland.isFertilized(metadata)) {
               ++y;
               return !world.isOutdoors(x, y, z) && this.isLightLevelSuitable(world, x, y, z);
            } else {
               return false;
            }
         }
      }
   }

   public boolean canBePlacedAt(World world, int x, int y, int z, int metadata) {
      if (this == Block.mushroomRed) {
         if (!world.isOutdoors(x, y, z)) {
            return false;
         }

         for(int dx = -1; dx <= 1; ++dx) {
            for(int dz = -1; dz <= 1; ++dz) {
               if (world.isAirOrPassableBlock(x + dx, y, z + dz, false) && world.canBlockSeeTheSky(x + dx, y, z + dz)) {
                  return false;
               }
            }
         }
      } else if (this == mushroomBrown && world.isOutdoors(x, y, z)) {
         return false;
      }

      return super.canBePlacedAt(world, x, y, z, metadata);
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      return (world.provider.hasNoSky || !world.canBlockSeeTheSky(x, y, z)) && super.isLegalAt(world, x, y, z, metadata);
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      if (block_below == null) {
         return false;
      } else if (!Block.opaqueCubeLookup[block_below.blockID] && block_below != Block.tilledField) {
         return false;
      } else if (block_below != Block.sand && block_below != Block.blockClay) {
         if (this == Block.mushroomRed && block_below != Block.grass) {
            return false;
         } else {
            return this != Block.mushroomBrown || block_below == Block.stone || block_below == Block.gravel || block_below == Block.dirt || block_below == Block.tilledField || block_below == Block.mycelium;
         }
      } else {
         return false;
      }
   }

   public boolean ignoresLightCheckIfUnderOpenSky() {
      return false;
   }

   public int getMinAllowedLightValue() {
      return 0;
   }

   public int getMaxAllowedLightValue() {
      return this == mushroomBrown ? BlockMycelium.getLightValueTolerance() : 15;
   }

   public boolean fertilizeMushroom(World world, int x, int y, int z, ItemStack item_stack, EntityPlayer player) {
      return item_stack != null && item_stack.getItem() == Item.manure ? this.tryGrowGiantMushroom(world, x, y, z, player) : false;
   }

   public final boolean tryGrowGiantMushroom(World world, int x, int y, int z, EntityPlayer player) {
      if (world.isRemote) {
         Debug.setErrorMessage("tryGrowGiantMushroom: called on client?");
      }

      int metadata = world.getBlockMetadata(x, y, z);
      if (!this.isLegalAt(world, x, y, z, metadata)) {
         return false;
      } else {
         Block block_below = world.getBlock(x, y - 1, z);
         if (this == Block.mushroomRed) {
            if (block_below != Block.grass) {
               return false;
            }

            if (!world.isOutdoors(x, y, z)) {
               return false;
            }
         } else if (this == Block.mushroomBrown) {
            if (block_below != Block.mycelium) {
               return false;
            }

            if (world.isOutdoors(x, y, z)) {
               return false;
            }
         }

         metadata = this.incrementGrowth(metadata);
         if (!this.isMature(metadata)) {
            world.setBlockMetadataWithNotify(x, y, z, metadata, 6);
            return true;
         } else {
            world.setBlockToAir(x, y, z);
            WorldGenBigMushroom var7 = null;
            if (this.blockID == Block.mushroomBrown.blockID) {
               var7 = new WorldGenBigMushroom(0);
            } else if (this.blockID == Block.mushroomRed.blockID) {
               var7 = new WorldGenBigMushroom(1);
            }

            if (var7 != null && var7.generate(world, world.rand, x, y, z)) {
               if (player != null) {
                  player.triggerAchievement(AchievementList.supersizeMe);
               }

               return true;
            } else {
               world.setBlock(x, y, z, this.blockID, metadata, 3);
               return false;
            }
         }
      }
   }

   public int getGrowthBits() {
      return 3;
   }

   public int getMaxGrowth() {
      return this.getGrowthBits();
   }

   public int getGrowth(int metadata) {
      return metadata & this.getGrowthBits();
   }

   public int incrementGrowth(int metadata) {
      if (this.getGrowth(metadata) < this.getMaxGrowth()) {
         ++metadata;
      }

      return metadata;
   }

   public boolean isMature(int metadata) {
      return this.getGrowth(metadata) == this.getMaxGrowth();
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this == mushroomBrown ? "brown, small" : (this == mushroomRed ? "red, small" : super.getNameDisambiguationForReferenceFile(metadata));
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (super.onBlockPlacedMITE(world, x, y, z, metadata, placer, test_only)) {
         if (!test_only && placer instanceof EntityPlayer) {
            BlockFarmland.checkForMyceliumConditions(world, x, y - 1, z, (EntityPlayer)placer);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public EnumPlantType getPlantType(World world, int x, int y, int z) {
      return EnumPlantType.Unknown;
   }

   @Override
   public int getPlantID(World world, int x, int y, int z) {
      return 0;
   }

   @Override
   public int getPlantMetadata(World world, int x, int y, int z) {
      return 0;
   }
}
