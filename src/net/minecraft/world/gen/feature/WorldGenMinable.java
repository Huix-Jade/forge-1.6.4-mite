package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenMinable extends WorldGenerator {
   private int minableBlockId;
   private int minable_block_metadata;
   private int numberOfBlocks;
   private int blockToReplace;
   private boolean vein_size_increases_with_depth;
   private int minableBlockMeta = 0;

   public WorldGenMinable(int par1, int par2) {
      this(par1, par2, Block.stone.blockID);
   }

   public WorldGenMinable(int par1, int par2, int par3) {
      this.minableBlockId = par1;
      this.numberOfBlocks = par2;
      this.blockToReplace = par3;
   }

   public WorldGenMinable(int id, int meta, int number, int target)
   {
      this(id, number, target);
      this.minableBlockMeta = meta;
   }

   public WorldGenMinable setMinableBlockMetadata(int metadata) {
      this.minable_block_metadata = metadata;
      return this;
   }

   public int getMinableBlockId() {
      return this.minableBlockId;
   }

   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      return this.generate(par1World, par2Random, par3, par4, par5, false);
   }

   public int growVein(World world, Random rand, int blocks_to_grow, int x, int y, int z, boolean must_be_supported, boolean is_dirt)
   {
      if (blocks_to_grow >= 1 && world.blockExists(x, y, z) && world.getBlockId(x, y, z) == this.blockToReplace)
      {
         if (must_be_supported && (y < 1 || world.isAirOrPassableBlock(x, y - 1, z, true)))
         {
            return 0;
         }
         else
         {
            if (is_dirt && world.canBlockSeeTheSky(x, y + 1, z))
            {
               BiomeGenBase ore_blocks_grown = world.getBiomeGenForCoords(x, z);
               world.setBlock(x, y, z, ore_blocks_grown != BiomeGenBase.desert && ore_blocks_grown != BiomeGenBase.desertHills ? Block.grass.blockID : Block.sand.blockID, 0, 2);
            }
            else
            {
               world.setBlock(x, y, z, this.minableBlockId, this.minable_block_metadata, 2);
            }

            int var15 = 1;

            for (int attempts = 0; attempts < 16; ++attempts)
            {
               int dx = 0;
               int dy = 0;
               int dz = 0;
               int axis = rand.nextInt(3);

               if (axis == 0)
               {
                  dx = rand.nextInt(2) == 0 ? -1 : 1;
               }
               else if (axis == 1)
               {
                  dy = rand.nextInt(2) == 0 ? -1 : 1;
               }
               else
               {
                  dz = rand.nextInt(2) == 0 ? -1 : 1;
               }

               var15 += this.growVein(world, rand, blocks_to_grow - var15, x + dx, y + dy, z + dz, must_be_supported, is_dirt);

               if (var15 == blocks_to_grow)
               {
                  break;
               }
            }

            return var15;
         }
      }
      else
      {
         return 0;
      }
   }

   public boolean generate(World world, Random rand, int x, int y, int z, boolean vein_size_increases_with_depth) {
      if (world.blockExists(x, y, z) && world.getBlockId(x, y, z) == this.blockToReplace) {
         int vein_size = this.numberOfBlocks;

         float scale;
         for(scale = 1.0F; rand.nextInt(2) == 0; scale = (float)((double)scale * ((double)rand.nextFloat() * 0.6 + 0.699999988079071))) {
         }

         scale = Math.min(scale, 4.0F);
         if (vein_size_increases_with_depth) {
            int range = this.getMaxVeinHeight(world) - this.getMinVeinHeight(world);
            if (range > 16) {
               float relative_height = ((float)y - (float)this.getMinVeinHeight(world)) / (float)range;
               scale *= 1.0F - relative_height + 0.5F;
            }
         }

         if ((float)vein_size * scale <= 3.0F && rand.nextInt(2) == 0) {
            if (rand.nextInt(2) != 0) {
               return true;
            }

            scale *= 2.0F;
         }

         vein_size = (int)((float)vein_size * scale);
         if (vein_size < 1) {
            return true;
         } else {
            if (vein_size == 1) {
               if (rand.nextInt(3) != 0) {
                  return true;
               }

               vein_size = 3;
            } else if (vein_size == 2) {
               if (rand.nextInt(3) == 0) {
                  return true;
               }

               vein_size = 3;
            } else if (vein_size > 32) {
               vein_size = 32;
            }

            boolean must_be_supported = world.isUnderworld() && this.getMinableBlockId() == Block.gravel.blockID;
            boolean is_dirt = this.minableBlockId == Block.dirt.blockID;
            this.growVein(world, rand, vein_size, x, y, z, must_be_supported, is_dirt);
            return true;
         }
      } else {
         return false;
      }
   }

   public int getMaxVeinHeight(World world) {
      if (world.isUnderworld()) {
         return 255;
      } else {
         Block block = Block.blocksList[this.minableBlockId];
         if (block == Block.dirt) {
            return 128;
         } else if (block == Block.gravel) {
            return 128;
         } else if (block == Block.oreCoal) {
            return 96;
         } else if (block == Block.oreCopper) {
            return 128;
         } else if (block == Block.oreSilver) {
            return 96;
         } else if (block == Block.oreGold) {
            return 48;
         } else if (block == Block.oreIron) {
            return 64;
         } else if (block == Block.oreMithril) {
            return 32;
         } else if (block != Block.oreAdamantium && block != Block.silverfish) {
            if (block == Block.oreRedstone) {
               return 24;
            } else if (block == Block.oreDiamond) {
               return 32;
            } else if (block == Block.oreLapis) {
               return 40;
            } else {
               Minecraft.setErrorMessage("WorldGenMinable: unknown ore id " + block.blockID);
               return -1;
            }
         } else {
            return 24;
         }
      }
   }

   public int getMinVeinHeight(World world) {
      if (world.isUnderworld()) {
         return 0;
      } else {
         Block block = Block.blocksList[this.minableBlockId];
         if (block == Block.dirt) {
            return 32;
         } else if (block == Block.gravel) {
            return 24;
         } else if (block == Block.oreCoal) {
            return 16;
         } else if (block == Block.oreCopper) {
            return 0;
         } else if (block == Block.oreSilver) {
            return 0;
         } else if (block == Block.oreGold) {
            return 0;
         } else if (block == Block.oreIron) {
            return 0;
         } else if (block == Block.oreMithril) {
            return 0;
         } else if (block != Block.oreAdamantium && block != Block.silverfish) {
            if (block == Block.oreRedstone) {
               return 0;
            } else if (block == Block.oreDiamond) {
               return 0;
            } else if (block == Block.oreLapis) {
               return 8;
            } else {
               Minecraft.setErrorMessage("WorldGenMinable: unknown ore id " + block.blockID);
               return -1;
            }
         } else {
            return 0;
         }
      }
   }

   public int getRandomVeinHeight(World world, Random rand) {
      Block block = Block.blocksList[this.minableBlockId];
      if (world.isUnderworld()) {
         if (world.underworld_y_offset != 0) {
            if (block == Block.oreAdamantium) {
               return rand.nextInt(16 + world.underworld_y_offset);
            }

            if (block instanceof BlockOre && rand.nextFloat() < 0.75F) {
               return rand.nextInt(16 + world.underworld_y_offset);
            }
         }

         return rand.nextInt(256);
      } else {
         float relative_height;
         if (block == Block.dirt) {
            do {
               relative_height = rand.nextFloat();
            } while(!(relative_height > rand.nextFloat()));
         } else if (block == Block.gravel) {
            do {
               relative_height = rand.nextFloat();
            } while(!(relative_height > rand.nextFloat()));
         } else if (block == Block.oreCoal) {
            do {
               relative_height = rand.nextFloat();
            } while(!(relative_height > rand.nextFloat()));
         } else if (block == Block.oreCopper) {
            if (rand.nextInt(2) == 0) {
               relative_height = rand.nextFloat() * 0.6F + 0.4F;
            } else {
               do {
                  relative_height = rand.nextFloat();
               } while(!(relative_height < rand.nextFloat()));
            }
         } else if (block == Block.oreSilver) {
            do {
               relative_height = rand.nextFloat();
            } while(!(relative_height < rand.nextFloat()));
         } else if (block == Block.oreGold) {
            do {
               relative_height = rand.nextFloat();
            } while(!(relative_height < rand.nextFloat()));
         } else if (block == Block.oreIron) {
            do {
               relative_height = rand.nextFloat();
            } while(!(relative_height < rand.nextFloat()));
         } else if (block == Block.oreMithril) {
            do {
               relative_height = rand.nextFloat();
            } while(!(relative_height < rand.nextFloat()));
         } else if (block != Block.oreAdamantium && block != Block.silverfish) {
            if (block == Block.oreRedstone) {
               do {
                  relative_height = rand.nextFloat();
               } while(!(relative_height < rand.nextFloat()));
            } else if (block == Block.oreDiamond) {
               do {
                  relative_height = rand.nextFloat();
               } while(!(relative_height < rand.nextFloat()));
            } else {
               if (block != Block.oreLapis) {
                  Minecraft.setErrorMessage("WorldGenMinable: unknown ore id " + this.minableBlockId);
                  return -1;
               }

               relative_height = (rand.nextFloat() + rand.nextFloat()) / 2.0F;
            }
         } else {
            do {
               relative_height = rand.nextFloat();
            } while(!(relative_height < rand.nextFloat()));
         }

         int min_height = this.getMinVeinHeight(world);
         int height_range = this.getMaxVeinHeight(world) - min_height + 1;
         return min_height + (int)(relative_height * (float)height_range);
      }
   }
}
