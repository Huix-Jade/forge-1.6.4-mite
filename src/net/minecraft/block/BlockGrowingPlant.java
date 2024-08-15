package net.minecraft.block;

import net.minecraft.world.World;

public abstract class BlockGrowingPlant extends BlockPlant {
   public BlockGrowingPlant(int block_id) {
      super(block_id);
      this.setMaxStackSize(8);
   }

   public abstract float getLowestOptimalTemperature();

   public abstract float getHighestOptimalTemperature();

   public float getTemperatureTolerance() {
      return 1.0F;
   }

   public float getGlobalGrowthRateModifierFromMITE() {
      return 0.25F;
   }

   public float getTemperatureGrowthRateModifier(float temperature) {
      float delta_temp;
      if (temperature < this.getLowestOptimalTemperature()) {
         delta_temp = this.getLowestOptimalTemperature() - temperature;
      } else {
         if (!(temperature > this.getHighestOptimalTemperature())) {
            return 1.0F;
         }

         delta_temp = temperature - this.getHighestOptimalTemperature();
      }

      return Math.max(1.0F - delta_temp / this.getTemperatureTolerance(), 0.0F);
   }

   public abstract float getHumidityGrowthRateModifier(boolean var1);

   public float getProximityGrowthRateModifier(World world, int x, int y, int z) {
      boolean north = world.getBlock(x, y, z - 1) == this;
      boolean east = world.getBlock(x + 1, y, z) == this;
      boolean south = world.getBlock(x, y, z + 1) == this;
      boolean west = world.getBlock(x - 1, y, z) == this;
      int num_neighbors = 0;
      if (north) {
         ++num_neighbors;
      }

      if (east) {
         ++num_neighbors;
      }

      if (south) {
         ++num_neighbors;
      }

      if (west) {
         ++num_neighbors;
      }

      if (num_neighbors > 1) {
         return 1.0F;
      } else {
         float isolation_penalty_factor = 0.5F;
         if (num_neighbors == 0) {
            return 0.5F;
         } else if (north && (world.getBlock(x, y, z - 2) == this || world.getBlock(x + 1, y, z - 1) == this || world.getBlock(x - 1, y, z - 1) == this)) {
            return 1.0F;
         } else if (!east || world.getBlock(x + 1, y, z - 1) != this && world.getBlock(x + 2, y, z) != this && world.getBlock(x + 1, y, z + 1) != this) {
            if (south && (world.getBlock(x + 1, y, z + 1) == this || world.getBlock(x, y, z + 2) == this || world.getBlock(x - 1, y, z + 1) == this)) {
               return 1.0F;
            } else {
               return !west || world.getBlock(x - 1, y, z - 1) != this && world.getBlock(x - 1, y, z + 1) != this && world.getBlock(x - 2, y, z) != this ? 0.75F : 1.0F;
            }
         } else {
            return 1.0F;
         }
      }
   }

   public int getMinAllowedLightValueForGrowth() {
      return 15;
   }

   public int getMaxAllowedLightValueForGrowth() {
      return 15;
   }

   public boolean isLightLevelSuitableForGrowth(int block_light_value) {
      return block_light_value >= this.getMinAllowedLightValueForGrowth() && block_light_value <= this.getMaxAllowedLightValueForGrowth();
   }

   public abstract float getGrowthRate(World var1, int var2, int var3, int var4);
}
