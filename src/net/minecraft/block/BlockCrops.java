package net.minecraft.block;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;

public class BlockCrops extends BlockGrowingPlant {
   protected int num_growth_stages;
   protected Icon[] iconArray;
   protected Icon[] iconArrayBlighted;

   protected BlockCrops(int block_id, int num_growth_stages) {
      super(block_id);
      this.num_growth_stages = num_growth_stages;
      this.setTickRandomly(true);
      float var2 = 0.5F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var2), 0.0, (double)(0.5F - var2), (double)(0.5F + var2), 0.25, (double)(0.5F + var2));
      this.setCreativeTab((CreativeTabs)null);
      this.setHardness(0.02F);
      this.setStepSound(soundGrassFootstep);
      this.disableStats();
      this.setCushioning(0.2F);
   }

   public String getMetadataNotes() {
      return "Bits 1, 2, and 4 used to track growth, bit 8 set if crop is blighted";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public float getLowestOptimalTemperature() {
      return 0.8F;
   }

   public float getHighestOptimalTemperature() {
      return 1.2F;
   }

   public float getHumidityGrowthRateModifier(boolean high_humidity) {
      return 1.0F;
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below == tilledField;
   }

   protected int getDeadCropBlockId() {
      return Block.cropsDead.blockID;
   }

   public boolean updateTick(World world, int x, int y, int z, Random rand) {
      if (super.updateTick(world, x, y, z, rand)) {
         return true;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         float growth_rate = this.getGrowthRate(world, x, y, z);
         if (growth_rate == 0.0F && !BlockFarmland.isWaterNearby(world, x, y - 1, z)) {
            if (rand.nextFloat() < 0.05F) {
               if (this.isMature(metadata)) {
                  this.dropBlockAsEntityItem((new BlockBreakInfo(world, x, y, z)).setDrought());
                  return world.setBlock(x, y, z, 0);
               } else {
                  return world.setBlock(x, y, z, this.getDeadCropBlockId(), this.getGrowth(metadata), 3);
               }
            } else {
               return false;
            }
         } else {
            int dx;
            if (this.isBlighted(metadata)) {
               if (rand.nextInt(64) == 0) {
                  return world.setBlock(x, y, z, this.getDeadCropBlockId(), this.isMature(metadata) ? this.getGrowth(metadata) - 1 : this.getGrowth(metadata), 3);
               } else if (rand.nextBoolean()) {
                  return false;
               } else {
                  dx = rand.nextInt(3) - 1;
                  int dy = rand.nextInt(3) - 1;
                  int dz = rand.nextInt(3) - 1;
                  if ((dx != 0 || dy != 0 || dz != 0) && world.blockExists(x + dx, y + dy, z + dz)) {
                     int block_id = world.getBlockId(x + dx, y + dy, z + dz);
                     Block block = Block.getBlock(block_id);
                     if (block instanceof BlockCrops && !((BlockCrops)block).isDead()) {
                        metadata = world.getBlockMetadata(x + dx, y + dy, z + dz);
                        if (!this.isBlighted(metadata)) {
                           world.setBlockMetadataWithNotify(x + dx, y + dy, z + dz, this.setBlighted(metadata, true), 2);
                        }
                     }
                  }

                  return false;
               }
            } else if (world.isBloodMoonNight() && !this.isBlighted(metadata) && rand.nextFloat() < 0.25F && !MinecraftServer.getServer().isDedicatedServer() && (!world.hasStandardFormOpaqueBlockAbove(x, y, z) || world.isOutdoors(x, y, z)) && world.setBlockMetadataWithNotify(x, y, z, this.setBlighted(metadata, true), 2)) {
               return true;
            } else {
               dx = world.getBlockLightValue(x, y + 1, z);
               if (rand.nextFloat() < this.chanceOfBlightPerRandomTick() * getBlightChanceModifierForBiome(world.getBiomeGenForCoords(x, z)) * (1.0F - (float)dx / 16.0F)) {
                  if (!this.isBlighted(metadata)) {
                     return world.setBlockMetadataWithNotify(x, y, z, this.setBlighted(metadata, true), 2);
                  }
               } else if (this.isLightLevelSuitableForGrowth(dx)) {
                  if (growth_rate == 0.0F || this.isMature(metadata)) {
                     return false;
                  }

                  if (rand.nextInt((int)(25.0F / growth_rate) + 1) == 0) {
                     world.setBlockMetadataWithNotify(x, y, z, this.incrementGrowth(metadata), 2);
                     int blockId = world.getBlockId(x, y - 1, z);
                     if (rand.nextInt(256) == 0 ) {
                        if (blocksList[blockId] != null
                                && blocksList[blockId].canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this)) {
                           metadata = world.getBlockMetadata(x, y - 1, z);
                           if (BlockFarmland.isFertilized(metadata)) {
                              world.setBlockMetadataWithNotify(x, y - 1, z, BlockFarmland.setFertilized(metadata, false), 2);
                           }
                        }
                     }

                     return true;
                  }
               }

               return false;
            }
         }
      }
   }

   public static float getBlightChanceModifierForBiome(BiomeGenBase biome) {
      float temperature = biome.temperature;
      float delta_temperature;
      if (temperature < 1.0F) {
         delta_temperature = 1.0F - temperature;
      } else if (temperature > 1.2F) {
         delta_temperature = temperature - 1.2F;
      } else {
         delta_temperature = 0.0F;
      }

      return Math.max((1.0F - delta_temperature) * (biome.isHighHumidity() ? 1.5F : 1.0F), 0.0F);
   }

   public boolean fertilize(World world, int x, int y, int z, ItemStack item_stack) {
      Item item = item_stack.getItem();
      if (item != Item.dyePowder) {
         return false;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         return this.isBlighted(metadata) && world.setBlockMetadataWithNotify(x, y, z, this.setBlighted(metadata, false), 2);
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
         var5 *= this.getProximityGrowthRateModifier(par1World, par2, par3, par4);
         return var5;
      }
   }

   public Icon getIcon(int side, int metadata) {
      return this.isBlighted(metadata) ? this.iconArrayBlighted[this.getGrowthStage(metadata)] : this.iconArray[this.getGrowthStage(metadata)];
   }

   public int getRenderType() {
      return 6;
   }

   protected int getSeedItem() {
      return Item.seeds.itemID;
   }

   protected int getCropItem() {
      return Item.wheat.itemID;
   }

   protected int getMatureYield() {
      return 1;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return this.getSeedItem();
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.iconArray = new Icon[this.num_growth_stages];
      if (this.chanceOfBlightPerRandomTick() > 0.0F) {
         this.iconArrayBlighted = new Icon[this.num_growth_stages];
      }

      for(int i = 0; i < this.num_growth_stages; ++i) {
         this.iconArray[i] = par1IconRegister.registerIcon("crops/" + this.getTextureName() + "/" + i);
         if (this.chanceOfBlightPerRandomTick() > 0.0F) {
            this.iconArrayBlighted[i] = par1IconRegister.registerIcon("crops/" + this.getTextureName() + "/blighted/" + i);
         }
      }

   }

   public float getBlockHardness(int metadata) {
      return metadata == 0 ? 0.0F : super.getBlockHardness(metadata) * (float)(this.isMature(metadata) ? 2 : 1);
   }

   public float chanceOfBlightPerRandomTick() {
      return 5.0E-4F;
   }

   public int getBlightBit() {
      return 8;
   }

   public int getGrowthBits() {
      return 7;
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

   public int getGrowthStage(int metadata) {
      return this.getGrowth(metadata);
   }

   public boolean isMature(int metadata) {
      return this.getGrowth(metadata) == this.getMaxGrowth();
   }

   public boolean isBlighted(int metadata) {
      return (metadata & this.getBlightBit()) != 0;
   }

   public int setBlighted(int metadata, boolean blighted) {
      if (blighted) {
         metadata |= this.getBlightBit();
      } else {
         metadata &= ~this.getBlightBit();
      }

      return metadata;
   }

   public final void setBlighted(World world, int x, int y, int z, boolean is_blighted) {
      if (!this.isDead()) {
         int metadata = world.getBlockMetadata(x, y, z);
         if (this.isBlighted(metadata) != is_blighted) {
            world.setBlockMetadataWithNotify(x, y, z, this.setBlighted(metadata, is_blighted), 2);
         }

      }
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (!info.wasHarvestedByPlayer() && !info.wasDrought() && !info.wasSnowedUpon() && !info.wasSelfDropped()) {
         return 0;
      } else {
         if (info.wasDrought()) {
            playCropPopSound(info);
         }

         if (this.isBlighted(info.getMetadata())) {
            return 0;
         } else {
            ItemStack item_stack = info.getHarvesterItemStack();
            Item item = item_stack == null ? null : item_stack.getItem();
            ItemTool tool = item instanceof ItemTool ? (ItemTool)item : null;
            float harvesting_enchantment;
            if (tool != null && tool.isEffectiveAgainstBlock(this, info.getMetadata())) {
               harvesting_enchantment = EnchantmentHelper.getEnchantmentLevelFraction(Enchantment.harvesting, info.getHarvesterItemStack()) * 0.5F;
            } else {
               harvesting_enchantment = 0.0F;
            }

            int num_drops;
            if (this.getGrowth(info.getMetadata()) == 0) {
               num_drops = this.dropBlockAsEntityItem(info, this.getSeedItem(), 0, 1, 1.0F);
            } else {
               if (!this.isMature(info.getMetadata()) || info.wasSelfDropped()) {
                  return 0;
               }

               num_drops = this.dropBlockAsEntityItem(info, this.getCropItem(), 0, this.getMatureYield(), 1.0F + harvesting_enchantment);
            }

            if (info.wasSnowedUpon() && num_drops > 0) {
               playCropPopSound(info);
            }

            return num_drops;
         }
      }
   }

   public boolean isDead() {
      return false;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.isDead() ? "dead" : "living";
   }

   public void makeSoundWhenPlaced(World world, int x, int y, int z, int metadata) {
      StepSound step_sound = Block.tilledField.stepSound;
      if (step_sound != null) {
         world.playSoundAtBlock(x, y, z, step_sound.getPlaceSound(), 0.2F, step_sound.getPitch() * 0.8F);
      }

   }

   public static void playCropPopSound(BlockBreakInfo info) {
      info.playSoundEffectAtBlock("random.pop", 0.05F, ((info.world.rand.nextFloat() - info.world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
   }

   @Override
   public EnumPlantType getPlantType(World world, int x, int y, int z) {
      return EnumPlantType.Crop;
   }

   @Override
   public int getPlantID(World world, int x, int y, int z) {
      return EnumPlantType.Crop.ordinal();
   }

   @Override
   public int getPlantMetadata(World world, int x, int y, int z) {
      return this.getDefaultMetadata(world, x, y, z);
   }
}
