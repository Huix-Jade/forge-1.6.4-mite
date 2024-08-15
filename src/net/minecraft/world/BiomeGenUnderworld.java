package net.minecraft.world;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroomCap;
import net.minecraft.entity.EntityAncientBoneLord;
import net.minecraft.entity.EntityBoneLord;
import net.minecraft.entity.EntityGhoul;
import net.minecraft.entity.EntityLongdead;
import net.minecraft.entity.EntityRevenant;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.chunk.ChunkPost;
import net.minecraft.world.chunk.ChunkPostField;

public class BiomeGenUnderworld extends BiomeGenBase {
   public BiomeGenUnderworld(int id) {
      super(id);
      this.removeEntityFromSpawnableLists(EntitySkeleton.class);
      this.removeEntityFromSpawnableLists(EntityZombie.class);
      this.removeEntityFromSpawnableLists(EntityGhoul.class);
      this.removeEntityFromSpawnableLists(EntityRevenant.class);
      this.removeEntityFromSpawnableLists(EntityBoneLord.class);
      this.spawnableMonsterList.add(new SpawnListEntry(EntityCaveSpider.class, 40, 1, 2));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityLongdead.class, 40, 1, 2));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityAncientBoneLord.class, 5, 1, 1));
   }

   public void decorate(World world, Random random, int chunk_origin_x, int chunk_origin_z) {
      this.placeMycelium(world, chunk_origin_x, chunk_origin_z);
      super.decorate(world, random, chunk_origin_x, chunk_origin_z);
   }

   private void placeMycelium(World world, int chunk_origin_x, int chunk_origin_z) {
      ChunkPostField mycelium_posts = world.getMyceliumPostField();
      Random random = new Random();

      for(int x = chunk_origin_x; x < chunk_origin_x + 16; ++x) {
         for(int z = chunk_origin_z; z < chunk_origin_z + 16; ++z) {
            List posts = mycelium_posts.getNearbyPostsForBlockCoords(x, z);

            for(int i = 0; i < posts.size(); ++i) {
               ChunkPost post = (ChunkPost)posts.get(i);
               if (!(post.getDistanceSqFromBlockCoords(x, z) > (double)(mycelium_posts.getPostMaxRadiusOfEffectSq() + 4))) {
                  random.setSeed(post.getSeed());
                  random.nextInt();
                  int y = random.nextInt(random.nextBoolean() ? 16 : 72) + 24;
                  y += world.underworld_y_offset;
                  int height = random.nextInt(5) + 1;

                  for(int dy = 0; dy < height; ++dy) {
                     if (world.isAirBlock(x, y + 1, z)) {
                        Block block = world.getBlock(x, y, z);
                        if (block != null && block.isAlwaysSolidOpaqueStandardFormCube() && !(block instanceof BlockMushroomCap)) {
                           block = world.getBlock(x, y - 1, z);
                           if (block != null && block.isAlwaysSolidOpaqueStandardFormCube() && world.setBlock(x, y, z, Block.mycelium.blockID, 0, 2)) {
                              world.getChunkFromBlockCoords(x, z).setHadNaturallyOccurringMycelium();
                              random.setSeed(post.getSeed() + (long)MathHelper.getIntPairHash(x, z));
                              random.nextInt();
                              if (random.nextInt(16) == 0 && !this.theBiomeDecorator.bigMushroomGen.generate(world, random, x, y + 1, z)) {
                                 world.setBlock(x, y + 1, z, Block.mushroomBrown.blockID, 0, 2);
                              }
                           }
                        }
                        break;
                     }

                     ++y;
                  }
               }
            }
         }
      }

   }
}
