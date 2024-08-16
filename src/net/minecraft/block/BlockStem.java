package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;

public final class BlockStem extends BlockGrowingPlant {
   private final Block fruitType;
   private Icon theIcon;

   protected BlockStem(int par1, Block par2Block) {
      super(par1);
      this.fruitType = par2Block;
      this.setTickRandomly(true);
      float var3 = 0.125F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var3), 0.0, (double)(0.5F - var3), (double)(0.5F + var3), 0.25, (double)(0.5F + var3));
      this.setCreativeTab((CreativeTabs)null);
   }

   public String getMetadataNotes() {
      return "Bits 1, 2, and 4 used to track growth, bit 8 set if stem is dead";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public float getLowestOptimalTemperature() {
      return this.fruitType == Block.pumpkin ? 0.6F : 1.0F;
   }

   public float getHighestOptimalTemperature() {
      return this.fruitType == Block.pumpkin ? 1.0F : 1.4F;
   }

   public float getHumidityGrowthRateModifier(boolean high_humidity) {
      return this.fruitType == Block.melon && high_humidity ? 1.2F : 1.0F;
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below == tilledField;
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (super.updateTick(par1World, par2, par3, par4, par5Random)) {
         return true;
      } else {
         int metadata = par1World.getBlockMetadata(par2, par3, par4);
         if (isDead(metadata)) {
            return false;
         } else {
            boolean state_changed = false;
            float growth_modifier = this.getGrowthRate(par1World, par2, par3, par4);
            if (growth_modifier == 0.0F && !BlockFarmland.isWaterNearby(par1World, par2, par3 - 1, par4)) {
               if (par5Random.nextFloat() < 0.05F) {
                  par1World.setBlockMetadataWithNotify(par2, par3, par4, setDead(metadata, true), 2);
               }

               return true;
            } else {
               if (par1World.getBlockLightValue(par2, par3 + 1, par4) >= this.getMinAllowedLightValueForGrowth()) {
                  float var6 = growth_modifier;
                  if (var6 == 0.0F) {
                     return false;
                  }

                  if (par5Random.nextInt((int)(25.0F / var6) + 1) == 0) {
                     int var7 = getGrowth(metadata);
                     if (var7 < 7) {
                        ++var7;
                        par1World.setBlockMetadataWithNotify(par2, par3, par4, var7, 2);
                        state_changed = true;
                     } else {
                        if (par5Random.nextInt(2) == 0) {
                           return false;
                        }

                        if (par1World.getBlockId(par2 - 1, par3, par4) == this.fruitType.blockID) {
                           return false;
                        }

                        if (par1World.getBlockId(par2 + 1, par3, par4) == this.fruitType.blockID) {
                           return false;
                        }

                        if (par1World.getBlockId(par2, par3, par4 - 1) == this.fruitType.blockID) {
                           return false;
                        }

                        if (par1World.getBlockId(par2, par3, par4 + 1) == this.fruitType.blockID) {
                           return false;
                        }

                        int var8 = par5Random.nextInt(4);
                        int var9 = par2;
                        int var10 = par4;
                        if (var8 == 0) {
                           var9 = par2 - 1;
                        }

                        if (var8 == 1) {
                           ++var9;
                        }

                        if (var8 == 2) {
                           var10 = par4 - 1;
                        }

                        if (var8 == 3) {
                           ++var10;
                        }

                        int var11 = par1World.getBlockId(var9, par3 - 1, var10);
                        boolean isSoil = (blocksList[var11] != null && blocksList[var11].
                                canSustainPlant(par1World, var9, par3 - 1, var10, ForgeDirection.UP, this));
                        if (par1World.isAirBlock(var9, par3, var10) && (isSoil || var11 == Block.dirt.blockID || var11 == Block.grass.blockID))
                        {
                           par1World.setBlock(var9, par3, var10, this.fruitType.blockID);
                           if (par5Random.nextFloat() < 0.1F) {
                              par1World.setBlockMetadataWithNotify(par2, par3, par4, setDead(metadata, true), 2);
                              state_changed = true;
                           }
                        }
                     }

                     if (par5Random.nextInt(256) == 0 && par1World.getBlockId(par2, par3 - 1, par4) == Block.tilledField.blockID) {
                        metadata = par1World.getBlockMetadata(par2, par3 - 1, par4);
                        if (BlockFarmland.isFertilized(metadata)) {
                           par1World.setBlockMetadataWithNotify(par2, par3 - 1, par4, BlockFarmland.setFertilized(metadata, false), 2);
                        }
                     }
                  }
               }

               return state_changed;
            }
         }
      }
   }

   public boolean fertilizeStem(World world, int x, int y, int z, ItemStack item_stack) {
      if (item_stack.getItem() == Item.dyePowder) {
         int metadata = world.getBlockMetadata(x, y, z);
         if (isDead(metadata)) {
            return false;
         } else {
            int growth = getGrowth(metadata);
            if (growth != 7 && this.getGrowthRate(world, x, y, z) != 0.0F) {
               if (!world.isRemote) {
                  growth += MathHelper.getRandomIntegerInRange(world.rand, 2, 5);
                  if (growth > 7) {
                     growth = 7;
                  }

                  world.setBlockMetadataWithNotify(x, y, z, setGrowth(metadata, growth), 2);
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public float getGrowthRate(World par1World, int par2, int par3, int par4) {
      Block block_below = Block.blocksList[par1World.getBlockId(par2, par3 - 1, par4)];
      int block_below_metadata = par1World.getBlockMetadata(par2, par3 - 1, par4);
      if (block_below == Block.tilledField && BlockFarmland.getWetness(block_below_metadata) == 0) {
         return 0.0F;
      } else {
         float var5 = 1.0F;
         int var6 = par1World.getBlockId(par2, par3, par4 - 1);
         int var7 = par1World.getBlockId(par2, par3, par4 + 1);
         int var8 = par1World.getBlockId(par2 - 1, par3, par4);
         int var9 = par1World.getBlockId(par2 + 1, par3, par4);
         int var10 = par1World.getBlockId(par2 - 1, par3, par4 - 1);
         int var11 = par1World.getBlockId(par2 + 1, par3, par4 - 1);
         int var12 = par1World.getBlockId(par2 + 1, par3, par4 + 1);
         int var13 = par1World.getBlockId(par2 - 1, par3, par4 + 1);
         boolean var14 = var8 == this.blockID || var9 == this.blockID;
         boolean var15 = var6 == this.blockID || var7 == this.blockID;
         boolean var16 = var10 == this.blockID || var11 == this.blockID || var12 == this.blockID || var13 == this.blockID;

         for(int var17 = par2 - 1; var17 <= par2 + 1; ++var17) {
            for(int var18 = par4 - 1; var18 <= par4 + 1; ++var18) {
               int var19 = par1World.getBlockId(var17, par3 - 1, var18);
               float var20 = 0.0F;
               if (var19 == Block.tilledField.blockID) {
                  var20 = 1.0F;
                  if (par1World.getBlockMetadata(var17, par3 - 1, var18) > 0) {
                     var20 = 3.0F;
                  }
               }

               if (var17 != par2 || var18 != par4) {
                  var20 /= 4.0F;
               }

               var5 += var20;
            }
         }

         if (var16 || var14 && var15) {
            var5 /= 2.0F;
         }

         if (block_below == Block.tilledField && BlockFarmland.isFertilized(block_below_metadata)) {
            var5 *= 1.5F;
         }

         BiomeGenBase biome = par1World.getBiomeGenForCoords(par2, par4);
         var5 *= this.getTemperatureGrowthRateModifier(biome.temperature);
         var5 *= this.getHumidityGrowthRateModifier(biome.isHighHumidity());
         var5 *= this.getGlobalGrowthRateModifierFromMITE();
         return var5;
      }
   }

   public int getRenderColor(int metadata) {
      int growth = isDead(metadata) ? 7 : getGrowth(metadata);
      int r = growth * 32;
      int g = 255 - growth * 8;
      int b = growth * 4;
      if (isDead(metadata)) {
         r = (r + 296) / 3;
         g = (g + 200) / 3;
         b = (b + 80) / 3;
      }

      return r << 16 | g << 8 | b;
   }

   public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return this.getRenderColor(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      float var1 = 0.125F;
      this.setBlockBoundsForCurrentThread((double)(0.5F - var1), 0.0, (double)(0.5F - var1), (double)(0.5F + var1), 0.25, (double)(0.5F + var1));
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int index = Minecraft.getThreadIndex();
      this.maxY[index] = (double)((float)(getGrowth(par1IBlockAccess.getBlockMetadata(par2, par3, par4)) * 2 + 2) / 16.0F);
      if (this.getState(par1IBlockAccess, par2, par3, par4) >= 0) {
         this.maxY[index] = 0.625;
      }

      float var5 = 0.125F;
      this.setBlockBoundsForCurrentThread((double)(0.5F - var5), 0.0, (double)(0.5F - var5), (double)(0.5F + var5), (double)((float)this.maxY[index]), (double)(0.5F + var5));
   }

   public int getRenderType() {
      return 19;
   }

   public int getState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      return var5 < 7 ? -1 : (par1IBlockAccess.getBlockId(par2 - 1, par3, par4) == this.fruitType.blockID ? 0 : (par1IBlockAccess.getBlockId(par2 + 1, par3, par4) == this.fruitType.blockID ? 1 : (par1IBlockAccess.getBlockId(par2, par3, par4 - 1) == this.fruitType.blockID ? 2 : (par1IBlockAccess.getBlockId(par2, par3, par4 + 1) == this.fruitType.blockID ? 3 : -1))));
   }

   public int getSeedItem() {
      return this.fruitType == Block.pumpkin ? Item.pumpkinSeeds.itemID : (this.fruitType == Block.melon ? Item.melonSeeds.itemID : -1);
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.wasSnowedUpon() && getGrowth(info.getMetadata()) == 0) {
         BlockCrops.playCropPopSound(info);
         return this.dropBlockAsEntityItem(info, this.getSeedItem(), 0, 1, 1.0F);
      } else {
         return info.wasHarvestedByPlayer() && !isDead(info.getMetadata()) && getGrowth(info.getMetadata()) <= 0 ? this.dropBlockAsEntityItem(info, this.getSeedItem(), 0, 1, 1.0F) : 0;
      }
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return this.fruitType == Block.pumpkin ? Item.pumpkinSeeds.itemID : (this.fruitType == Block.melon ? Item.melonSeeds.itemID : 0);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_disconnected");
      this.theIcon = par1IconRegister.registerIcon(this.getTextureName() + "_connected");
   }

   public Icon getStemIcon() {
      return this.theIcon;
   }

   public static int getGrowthBits() {
      return 7;
   }

   public static int getMaxGrowth() {
      return getGrowthBits();
   }

   public static int getGrowth(int metadata) {
      return metadata & getGrowthBits();
   }

   public static int incrementGrowth(int metadata) {
      if (getGrowth(metadata) < getMaxGrowth()) {
         ++metadata;
      }

      return metadata;
   }

   public static int setGrowth(int metadata, int growth) {
      return metadata & ~getGrowthBits() | growth & getGrowthBits();
   }

   public static int getGrowthStage(int metadata) {
      return getGrowth(metadata);
   }

   public static boolean isMature(int metadata) {
      return getGrowth(metadata) == getMaxGrowth();
   }

   public static int getDeadBit() {
      return 8;
   }

   public static boolean isDead(int metadata) {
      return (metadata & getDeadBit()) != 0;
   }

   public static int setDead(int metadata, boolean dead) {
      if (dead) {
         metadata |= getDeadBit();
      } else {
         metadata &= ~getDeadBit();
      }

      return metadata;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.fruitType == pumpkin ? "pumpkin" : (this.fruitType == melon ? "melon" : super.getNameDisambiguationForReferenceFile(metadata));
   }

   public void makeSoundWhenPlaced(World world, int x, int y, int z, int metadata) {
      StepSound step_sound = Block.tilledField.stepSound;
      if (step_sound != null) {
         world.playSoundAtBlock(x, y, z, step_sound.getPlaceSound(), 0.2F, step_sound.getPitch() * 0.8F);
      }

   }
}
