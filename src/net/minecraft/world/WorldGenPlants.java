package net.minecraft.world;

import java.util.Random;
import net.minecraft.block.BlockPlant;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenPlants extends WorldGenerator {
   private BlockPlant block;
   private int metadata;

   public WorldGenPlants(BlockPlant block) {
      this.block = block;
   }

   public boolean generate(World world, Random random, int origin_x, int origin_y, int origin_z) {
      int attempts = this.block.getPatchSize(world.getBiomeGenForCoords(origin_x, origin_z));
      int placements = 0;

      for(int attempt = 0; attempt < attempts; ++attempt) {
         int x = origin_x + random.nextInt(8) - random.nextInt(8);
         int y = origin_y + random.nextInt(4) - random.nextInt(4);
         int z = origin_z + random.nextInt(8) - random.nextInt(8);
         if (world.isAirBlock(x, y, z) && (!world.provider.hasNoSky || y < 127) && this.block.canOccurAt(world, x, y, z, this.metadata)) {
            world.setBlock(x, y, z, this.block.blockID, this.metadata, 2);
            ++placements;
            if (placements > 3 && random.nextInt(3) == 0) {
               break;
            }
         }
      }

      return true;
   }

   public void setMetadata(int metadata) {
      this.metadata = metadata;
   }
}
