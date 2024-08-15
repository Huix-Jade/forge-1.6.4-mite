package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class BlockBush extends BlockGrowingPlant {
   public static final int BLUEBERRY = 0;
   public static final String[] types = new String[]{"blueberry"};
   private Icon[] icons;

   protected BlockBush(int id) {
      super(id);
      float width = 0.3F;
      float height = 0.6F;
      this.setBlockBoundsForAllThreads((double)(0.5F - width), 0.0, (double)(0.5F - width), (double)(0.5F + width), (double)height, (double)(0.5F + width));
   }

   public void registerIcons(IconRegister icon_register) {
      this.icons = new Icon[types.length * 2];

      for(int i = 0; i < types.length; ++i) {
         if (types[i] != null) {
            this.icons[i * 2] = icon_register.registerIcon(this.getTextureName() + "/" + types[i]);
            this.icons[i * 2 + 1] = icon_register.registerIcon(this.getTextureName() + "/" + types[i] + "_picked");
         }
      }

   }

   public Icon getIcon(int side, int metadata) {
      return this.icons[this.getBlockSubtype(metadata) * 2 + (hasBerries(metadata) ? 0 : 1)];
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for subtype, bits 4 and 8 used to track berry growth";
   }

   public boolean isValidMetadata(int metadata) {
      return (metadata & 3) < types.length;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata & 3;
   }

   public int getItemSubtype(int metadata) {
      return setBerryGrowth(this.getBlockSubtype(metadata), getMaxBerryGrowth());
   }

   public static int getMaxBerryGrowth() {
      return 3;
   }

   public static boolean hasBerries(int metadata) {
      return getBerryGrowth(metadata) == getMaxBerryGrowth();
   }

   public static int setBerryGrowth(int metadata, int growth) {
      return metadata & 3 | MathHelper.clamp_int(growth, 0, getMaxBerryGrowth()) << 2;
   }

   public static int getBerryGrowth(int metadata) {
      return metadata >> 2;
   }

   public static int incrementBerryGrowth(int metadata) {
      return setBerryGrowth(metadata, getBerryGrowth(metadata) + 1);
   }

   public static int setToMaximumBerryGrowth(int metadata) {
      return setBerryGrowth(metadata, getMaxBerryGrowth());
   }

   public static int getMetadataForBushWithBerries(int subtype) {
      return setToMaximumBerryGrowth(subtype);
   }

   public int getMinAllowedLightValue() {
      return 8;
   }

   public int getMaxAllowedLightValue() {
      return 15;
   }

   public boolean dropsAsSelfWhenTrampled(Entity entity) {
      return false;
   }

   public boolean isBiomeSuitable(BiomeGenBase biome, int metadata) {
      return biome == BiomeGenBase.forest || biome == BiomeGenBase.forestHills;
   }

   public boolean canBePlacedAt(World world, int x, int y, int z, int metadata) {
      return this.isBiomeSuitable(world.getBiomeGenForCoords(x, z), metadata) && world.canBlockSeeTheSky(x, y, z) && super.canBePlacedAt(world, x, y, z, metadata);
   }

   public int getPatchSize(BiomeGenBase biome) {
      return 64;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      int metadata = world.getBlockMetadata(x, y, z);
      if (!hasBerries(metadata)) {
         return false;
      } else {
         if (player.onServer()) {
            this.dropBlockAsEntityItem((new BlockBreakInfo(world, x, y, z)).setPickedBy(player), Item.blueberries);
            world.setBlock(x, y, z, this.blockID, setBerryGrowth(metadata, 0), 2);
         }

         return true;
      }
   }

   public boolean fertilize(World world, int x, int y, int z, ItemStack item_stack) {
      Item item = item_stack.getItem();
      if (item == Item.dyePowder) {
         int metadata = world.getBlockMetadata(x, y, z);
         if (hasBerries(metadata)) {
            return false;
         } else {
            if (!world.isRemote) {
               for(int growth_added = MathHelper.getRandomIntegerInRange(world.rand, 1, 2); growth_added-- > 0; metadata = incrementBerryGrowth(metadata)) {
               }

               world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int dropBlockAsItself(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info) + super.dropBlockAsItself(info);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return !hasBerries(info.getMetadata()) || !info.wasHarvestedByPlayer() && !info.wasSelfDropped() && !info.wasNotLegal() ? 0 : this.dropBlockAsEntityItem(info, Item.blueberries);
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return entity instanceof EntityPlayer && entity.getAsPlayer().inCreativeMode() ? item_stack.getItemSubtype() : setBerryGrowth(item_stack.getItemSubtype(), 0);
   }

   public float getGrowthRate(World world, int x, int y, int z) {
      float growth_rate = 0.1F;
      BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
      growth_rate *= this.getTemperatureGrowthRateModifier(biome.temperature);
      growth_rate *= this.getHumidityGrowthRateModifier(biome.isHighHumidity());
      growth_rate *= this.getGlobalGrowthRateModifierFromMITE();
      return growth_rate;
   }

   public boolean updateTick(World world, int x, int y, int z, Random rand) {
      if (super.updateTick(world, x, y, z, rand)) {
         return true;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         if (hasBerries(metadata)) {
            return false;
         } else {
            int berry_growth = getBerryGrowth(metadata);
            float growth_rate = this.getGrowthRate(world, x, y, z);
            if (growth_rate == 0.0F) {
               return false;
            } else {
               int blv = world.getBlockLightValue(x, y + 1, z);
               if (this.isLightLevelSuitableForGrowth(blv) && rand.nextFloat() < growth_rate) {
                  world.setBlockMetadataWithNotify(x, y, z, incrementBerryGrowth(metadata), 2);
                  return true;
               } else {
                  return false;
               }
            }
         }
      }
   }

   public float getLowestOptimalTemperature() {
      return BiomeGenBase.forestHills.temperature;
   }

   public float getHighestOptimalTemperature() {
      return BiomeGenBase.forest.temperature;
   }

   public float getHumidityGrowthRateModifier(boolean high_humidity) {
      return 1.0F;
   }
}
