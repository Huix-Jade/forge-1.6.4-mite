package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGelatinousCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;

public final class BlockFarmland extends Block {
   private Icon[] icon_array;

   protected BlockFarmland(int par1) {
      super(par1, Material.dirt, (new BlockConstants()).setNotAlwaysLegal());
      this.setTickRandomly(true);
      this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
      this.setLightOpacity(255);
      this.setCushioning(0.4F);
   }

   public String getMetadataNotes() {
      return "Bits 1, 2, and 4 used for wetness, bit 8 set if fertilized";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      return getStandardFormBoundingBoxFromPool(x, y, z);
   }

   public Icon getIcon(int side, int metadata) {
      return side == 1 ? this.icon_array[(getWetness(metadata) > 0 ? 2 : 0) + (isFertilized(metadata) ? 1 : 0)] : Block.dirt.getBlockTextureFromSide(side);
   }

   public float chanceOfDrying(World world, int x, int z) {
      BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
      float chance = biome.temperature - 0.15F - (biome.isHighHumidity() ? 0.5F : 0.0F);
      if (chance < 0.0F) {
         chance = 0.0F;
      }

      return chance;
   }

   public boolean updateTick(World world, int x, int y, int z, Random rand) {
      if (super.updateTick(world, x, y, z, rand)) {
         return true;
      } else if (isWaterNearby(world, x, y, z)) {
         return water(world, x, y, z);
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         int wetness = getWetness(metadata);
         if (world.canLightningStrikeAt(x, y + 1, z)) {
            if (wetness == getWetnessBits()) {
               return false;
            } else {
               ++wetness;
               return world.setBlockMetadataWithNotify(x, y, z, setWetness(metadata, wetness), 3);
            }
         } else if (rand.nextFloat() >= this.chanceOfDrying(world, x, z)) {
            return false;
         } else if (wetness > 0) {
            return world.setBlockMetadataWithNotify(x, y, z, setWetness(metadata, wetness - 1), 2);
         } else {
            return world.isAirBlock(x, y + 1, z) && !this.isCropsNearby(world, x, y, z) ? world.setBlock(x, y, z, Block.dirt.blockID) : false;
         }
      }
   }

   public void onFallenUpon(World par1World, int par2, int par3, int par4, Entity par5Entity, float par6) {
      if (!(par5Entity instanceof EntityGelatinousCube)) {
         if (!par1World.isRemote && par1World.rand.nextFloat() < par6 - 0.5F) {
            Block block_above = par1World.getBlock(par2, par3 + 1, par4);
            if (block_above instanceof BlockPlant) {
               BlockPlant plant = (BlockPlant)block_above;
               plant.onTrampledBy(par1World, par2, par3 + 1, par4, par5Entity);
            }

            int metadata = par1World.getBlockMetadata(par2, par3, par4);
            if (getWetness(metadata) > 0 || isFertilized(metadata)) {
               return;
            }

            if (!(par5Entity instanceof EntityPlayer) && !par1World.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
               return;
            }

            par1World.setBlock(par2, par3, par4, Block.dirt.blockID);
         }

      }
   }

   private boolean isCropsNearby(World par1World, int par2, int par3, int par4) {
      byte var5 = 1;

      for(int var6 = par2 - var5; var6 <= par2 + var5; ++var6) {
         for(int var7 = par4 - var5; var7 <= par4 + var5; ++var7) {
            Block block = Block.blocksList[par1World.getBlockId(var6, par3 + 1, var7)];
            if (block instanceof BlockCrops || block instanceof BlockStem) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isWaterNearby(World par1World, int par2, int par3, int par4) {
      int dx;
      int dy;
      int dz;
      for(dx = par2 - 4; dx <= par2 + 4; ++dx) {
         for(dy = par3; dy <= par3 + 1; ++dy) {
            for(dz = par4 - 4; dz <= par4 + 4; ++dz) {
               if (par1World.getBlockMaterial(dx, dy, dz) == Material.water) {
                  return true;
               }
            }
         }
      }

      for(dx = -1; dx <= 1; ++dx) {
         for(dy = 0; dy <= 1; ++dy) {
            for(dz = -1; dz <= 1; ++dz) {
               Block block = Block.blocksList[par1World.getBlockId(par2 + dx, par3 + dy, par4 + dz)];
               if (block != null && (block.blockMaterial == Material.snow || block.blockMaterial == Material.craftedSnow || block.blockMaterial == Material.ice)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      Block block_above = world.getBlockWithRefreshedBounds(x, y + 1, z);

      if (block_above instanceof IPlantable && canSustainPlant(world, x, y, z, ForgeDirection.UP, (IPlantable)block_above)) {
         return true;
      }

      if (block_above instanceof BlockBush) {
         return false;
      } else {
         return block_above == null || block_above instanceof BlockGrowingPlant
                 || block_above == Block.mushroomBrown
                 || block_above.minY[Minecraft.getThreadIndex()] > 0.0;
      }
   }

   public boolean onNotLegal(World world, int x, int y, int z, int metadata) {
      return world.setBlock(x, y, z, dirt.blockID);
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Block.dirt);
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Block.dirt.blockID;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.icon_array = new Icon[4];
      this.icon_array[0] = par1IconRegister.registerIcon(this.getTextureName() + "_dry");
      this.icon_array[1] = par1IconRegister.registerIcon(this.getTextureName() + "_dry_fertilized");
      this.icon_array[2] = par1IconRegister.registerIcon(this.getTextureName() + "_wet");
      this.icon_array[3] = par1IconRegister.registerIcon(this.getTextureName() + "_wet_fertilized");
   }

   public static int getWetnessBits() {
      return 7;
   }

   public static int getWetness(int metadata) {
      return metadata & getWetnessBits();
   }

   public static int setWetness(int metadata, int wetness) {
      return metadata & ~getWetnessBits() | wetness & getWetnessBits();
   }

   public static int getFertilizedBit() {
      return 8;
   }

   public static boolean isFertilized(int metadata) {
      return (metadata & getFertilizedBit()) != 0;
   }

   public static int setFertilized(int metadata, boolean fertilized) {
      return fertilized ? metadata | getFertilizedBit() : metadata & ~getFertilizedBit();
   }

   public static void setFertilized(World world, int x, int y, int z, boolean fertilized) {
      if (fertilized) {
         world.blockFX(EnumBlockFX.manure, x, y, z);
      }

      world.setBlockMetadataWithNotify(x, y, z, setFertilized(world.getBlockMetadata(x, y, z), fertilized), 2);
   }

   public static boolean water(World world, int x, int y, int z) {
      int metadata = world.getBlockMetadata(x, y, z);
      if (getWetness(metadata) < 7) {
         if (!world.isRemote) {
            world.setBlockMetadataWithNotify(x, y, z, setWetness(metadata, 7), 2);
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean fertilize(World world, int x, int y, int z, ItemStack item_stack, EntityPlayer player) {
      Item item = item_stack.getItem();
      if (item == Item.manure) {
         int metadata = world.getBlockMetadata(x, y, z);
         if (!isFertilized(metadata)) {
            if (!world.isRemote) {
               setFertilized(world, x, y, z, true);
               if (player != null) {
                  player.triggerAchievement(AchievementList.soilEnrichment);
               }

               checkForMyceliumConditions(world, x, y, z, player);
            }

            return true;
         }
      } else if (item == Item.dyePowder) {
         Block block = Block.blocksList[world.getBlockId(x, y + 1, z)];
         if (block instanceof BlockCrops) {
            return ((BlockCrops)block).fertilize(world, x, y + 1, z, item_stack);
         }

         if (block instanceof BlockStem) {
            return ((BlockStem)block).fertilizeStem(world, x, y + 1, z, item_stack);
         }
      } else {
         if (item == Item.bowlWater || ItemPotion.isBottleOfWater(item_stack)) {
            if (world.provider.isHellWorld) {
               if (!world.isRemote) {
                  world.blockFX(EnumBlockFX.water_evaporation_in_hell, x, y + 1, z);
               }

               return true;
            } else if (water(world, x, y, z)) {
               checkForMyceliumConditions(world, x, y, z, player);
               return true;
            } else {
               return false;
            }
         }

         if (item instanceof ItemBucket && ((ItemBucket)item).contains(Material.water)) {
            if (world.provider.isHellWorld) {
               if (!world.isRemote) {
                  world.blockFX(EnumBlockFX.water_evaporation_in_hell, x, y + 1, z);
               }

               return true;
            }

            boolean used = false;

            for(int dx = -1; dx <= 1; ++dx) {
               for(int dz = -1; dz <= 1; ++dz) {
                  if (world.getBlockId(x + dx, y, z + dz) == Block.tilledField.blockID) {
                     int metadata = world.getBlockMetadata(x + dx, y, z + dz);
                     if (getWetness(metadata) < 7) {
                        if (!world.isRemote) {
                           world.setBlockMetadataWithNotify(x + dx, y, z + dz, setWetness(metadata, 7), 2);
                           checkForMyceliumConditions(world, x + dx, y, z + dz, player);
                        }

                        used = true;
                     }
                  }
               }
            }

            return used;
         }
      }

      return false;
   }

   public void onBlockAdded(World world, int x, int y, int z) {
      if (isWaterNearby(world, x, y, z)) {
         water(world, x, y, z);
      }

   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }

   public boolean isFaceFlatAndSolid(int metadata, EnumFace face) {
      return face.isBottom();
   }

   public static boolean checkForMyceliumConditions(World world, int x, int y, int z, EntityPlayer player) {
      if (mushroomBrown.canConvertBlockBelowToMycelium(world, x, y + 1, z)) {
         if (player != null) {
            player.triggerAchievement(AchievementList.makeMycelium);
         }

         return true;
      } else {
         return false;
      }
   }
}
