package net.minecraft.world.gen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenCaves extends MapGenBase {
   private static final byte grass_block_id;
   private static final byte stone_block_id;
   private static final byte dirt_block_id;
   private static final byte water_moving_block_id;
   private static final byte water_still_block_id;
   private static final byte lava_moving_block_id;
   private static final byte sand_block_id;
   private static final byte sand_stone_block_id;
   private HashMap pending_sand_falls = new HashMap();

   protected void generateLargeCaveNode(long par1, int par3, int par4, byte[] par5ArrayOfByte, double par6, double par8, double par10) {
      this.generateCaveNode(par1, par3, par4, par5ArrayOfByte, par6, par8, par10, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
   }

   protected void generateCaveNode(long par1, int par3, int par4, byte[] par5ArrayOfByte, double par6, double par8, double par10, float par12, float par13, float par14, int par15, int par16, double par17) {
      int par3_times_16 = par3 * 16;
      int par4_times_16 = par4 * 16;
      double var19 = (double)(par3_times_16 + 8);
      double var21 = (double)(par4_times_16 + 8);
      float var23 = 0.0F;
      float var24 = 0.0F;
      Random var25 = new Random(par1);
      if (par16 <= 0) {
         int var26 = this.range * 16 - 16;
         par16 = var26 - var25.nextInt(var26 / 4);
      }

      boolean var54 = false;
      if (par15 == -1) {
         par15 = par16 / 2;
         var54 = true;
      }

      int var27 = var25.nextInt(par16 / 2) + par16 / 4;

      for(boolean var28 = var25.nextInt(6) == 0; par15 < par16; ++par15) {
         double var29 = 1.5 + (double)(MathHelper.sin((float)par15 * 3.1415927F / (float)par16) * par12 * 1.0F);
         double var31 = var29 * par17;
         float var33 = MathHelper.cos(par14);
         float var34 = MathHelper.sin(par14);
         par6 += (double)(MathHelper.cos(par13) * var33);
         par8 += (double)var34;
         par10 += (double)(MathHelper.sin(par13) * var33);
         if (var28) {
            par14 *= 0.92F;
         } else {
            par14 *= 0.7F;
         }

         par14 += var24 * 0.1F;
         par13 += var23 * 0.1F;
         var24 *= 0.9F;
         var23 *= 0.75F;
         var24 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 2.0F;
         var23 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 4.0F;
         if (!var54 && par15 == var27 && par12 > 1.0F && par16 > 0) {
            this.generateCaveNode(var25.nextLong(), par3, par4, par5ArrayOfByte, par6, par8, par10, var25.nextFloat() * 0.5F + 0.5F, par13 - 1.5707964F, par14 / 3.0F, par15, par16, 1.0);
            this.generateCaveNode(var25.nextLong(), par3, par4, par5ArrayOfByte, par6, par8, par10, var25.nextFloat() * 0.5F + 0.5F, par13 + 1.5707964F, par14 / 3.0F, par15, par16, 1.0);
            return;
         }

         if (var54 || var25.nextInt(4) != 0) {
            double var35 = par6 - var19;
            double var37 = par10 - var21;
            double var39 = (double)(par16 - par15);
            double var41 = (double)(par12 + 2.0F + 16.0F);
            if (var35 * var35 + var37 * var37 - var39 * var39 > var41 * var41) {
               return;
            }

            if (par6 >= var19 - 16.0 - var29 * 2.0 && par10 >= var21 - 16.0 - var29 * 2.0 && par6 <= var19 + 16.0 + var29 * 2.0 && par10 <= var21 + 16.0 + var29 * 2.0) {
               int var55 = MathHelper.floor_double(par6 - var29) - par3_times_16 - 1;
               int var36 = MathHelper.floor_double(par6 + var29) - par3_times_16 + 1;
               int var57 = MathHelper.floor_double(par8 - var31) - 1;
               int var38 = MathHelper.floor_double(par8 + var31) + 1;
               int var56 = MathHelper.floor_double(par10 - var29) - par4_times_16 - 1;
               int var40 = MathHelper.floor_double(par10 + var29) - par4_times_16 + 1;
               if (var55 < 0) {
                  var55 = 0;
               }

               if (var36 > 16) {
                  var36 = 16;
               }

               if (var57 < 1) {
                  var57 = 1;
               }

               if (var38 > 120) {
                  var38 = 120;
               }

               if (var56 < 0) {
                  var56 = 0;
               }

               if (var40 > 16) {
                  var40 = 16;
               }

               boolean var58 = false;

               int var42;
               int var45;
               int xz_index;
               int y;
               for(var42 = var55; !var58 && var42 < var36; ++var42) {
                  int var42_times_16 = var42 * 16;

                  for(int var43 = var56; !var58 && var43 < var40; ++var43) {
                     xz_index = (var42_times_16 + var43) * 128;

                     for(y = var38 + 1; !var58 && y >= var57 - 1; --y) {
                        var45 = xz_index + y;
                        if (y >= 0 && y < 128) {
                           if (isOceanBlock(par5ArrayOfByte, var45, var42, y, var43, par3, par4)) {
                              var58 = true;
                           }

                           if (y != var57 - 1 && var42 != var55 && var42 != var36 - 1 && var43 != var56 && var43 != var40 - 1) {
                              y = var57;
                           }
                        }
                     }
                  }
               }

               if (!var58) {
                  this.pending_sand_falls.clear();

                  int var48;
                  for(var42 = var55; var42 < var36; ++var42) {
                     double var59 = ((double)(var42 + par3_times_16) + 0.5 - par6) / var29;

                     for(var45 = var56; var45 < var40; ++var45) {
                        byte top_block_id = this.worldObj.getBiomeGenForCoords(var42 + par3_times_16, var45 + par4_times_16).topBlock;
                        double var46 = ((double)(var45 + par4_times_16) + 0.5 - par10) / var29;
                        var48 = (var42 * 16 + var45) * 128 + var38;
                        boolean var49 = false;
                        if (var59 * var59 + var46 * var46 < 1.0) {
                           for(int var50 = var38 - 1; var50 >= var57; --var50) {
                              double var51 = ((double)var50 + 0.5 - par8) / var31;
                              if (var51 > -0.7 && var59 * var59 + var51 * var51 + var46 * var46 < 1.0) {
                                 byte var53 = par5ArrayOfByte[var48];
                                 if (isTopBlock(par5ArrayOfByte, var48, var42, var50, var45, par3, par4)) {
                                    var49 = true;
                                 }

                                 digBlock(par5ArrayOfByte, var48, var42, var50, var45, par3, par4, var49);
                              }

                              --var48;
                           }
                        }
                     }
                  }

                  if (!this.pending_sand_falls.isEmpty()) {
                     Iterator i = this.pending_sand_falls.entrySet().iterator();

                     while(true) {
                        if (!i.hasNext()) {
                           this.pending_sand_falls.clear();
                           break;
                        }

                        Map.Entry entry = (Map.Entry)i.next();
                        xz_index = (Integer)entry.getKey();
                        y = (Integer)entry.getValue();
                        int local_x = xz_index % 16;
                        var48 = xz_index / 16;

                        int index;
                        for(index = (local_x * 16 + var48) * 128 + y; y > 48 && (par5ArrayOfByte[index] != sand_block_id || par5ArrayOfByte[index - 1] != 0); --y) {
                           --index;
                        }

                        if (par5ArrayOfByte[index] == sand_block_id) {
                           if (this.worldObj.pending_sand_falls == null) {
                              this.worldObj.pending_sand_falls = new HashMap();
                           }

                           this.worldObj.pending_sand_falls.put(xz_index, y);
                        }
                     }
                  }

                  if (var54) {
                     break;
                  }
               }
            }
         }
      }

   }

   protected void xrecursiveGenerate(World par1World, int par2, int par3, int par4, int par5, byte[] par6ArrayOfByte) {
      BiomeGenBase biome = par1World.getBiomeGenForCoords(par2 * 16, par3 * 16);
      float frequency;
      if (biome != BiomeGenBase.plains && biome != BiomeGenBase.swampland) {
         if (biome == BiomeGenBase.iceMountains) {
            frequency = 1.25F;
         } else if (biome == BiomeGenBase.extremeHills) {
            frequency = 1.5F;
         } else {
            frequency = 1.0F;
         }
      } else {
         frequency = 0.75F;
      }

      if (this.rand.nextInt((int)(40.0F / frequency)) == 0) {
         int var7 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
         var7 = (int)((float)var7 * frequency);

         for(int var8 = 0; var8 < var7; ++var8) {
            double var9 = (double)(par2 * 16 + this.rand.nextInt(16));
            double var13 = (double)(par3 * 16 + this.rand.nextInt(16));
            int var15 = 1;
            double var11;
            if (biome != BiomeGenBase.extremeHills && this.rand.nextInt(2) != 0) {
               var11 = (double)this.rand.nextInt(this.rand.nextInt(40) + 8);
            } else {
               var11 = (double)this.rand.nextInt(this.rand.nextInt(120) + 8);
            }

            if (this.rand.nextInt(4) == 0) {
               this.generateLargeCaveNode(this.rand.nextLong(), par4, par5, par6ArrayOfByte, var9, var11, var13);
               var15 += this.rand.nextInt(4);
            }

            for(int var16 = 0; var16 < var15; ++var16) {
               float var17 = this.rand.nextFloat() * 3.1415927F * 2.0F;
               float var18 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
               float var19 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
               if (this.rand.nextInt(10) == 0) {
                  var19 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
               }

               this.generateCaveNode(this.rand.nextLong(), par4, par5, par6ArrayOfByte, var9, var11, var13, var19, var17, var18, 0, 0, 1.0);
            }
         }

      }
   }

   protected void recursiveGenerate(World par1World, int par2, int par3, int par4, int par5, byte[] par6ArrayOfByte) {
      BiomeGenBase biome = par1World.getBiomeGenForCoords(par2 * 16, par3 * 16);
      float frequency;
      if (biome != BiomeGenBase.plains && biome != BiomeGenBase.swampland) {
         if (biome == BiomeGenBase.iceMountains) {
            frequency = 1.2F;
         } else if (biome == BiomeGenBase.extremeHills) {
            frequency = 1.4F;
         } else {
            frequency = 1.0F;
         }
      } else {
         frequency = 0.8F;
      }

      if (this.rand.nextInt((int)(15.0F / frequency)) == 0) {
         int var7 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(30) + 1) + 1);
         var7 = (int)((float)var7 * frequency);
         boolean increase_frequency_of_larger_tunnels = par1World.worldInfo.getEarliestMITEReleaseRunIn() >= 166;

         for(int var8 = 0; var8 < var7; ++var8) {
            double var9 = (double)(par2 * 16 + this.rand.nextInt(16));
            double var11 = (double)this.rand.nextInt(this.rand.nextInt(120) + 8);
            double var13 = (double)(par3 * 16 + this.rand.nextInt(16));
            int var15 = 1;
            int rarity_of_large_tunnels;
            if (!increase_frequency_of_larger_tunnels) {
               rarity_of_large_tunnels = 10;
            } else {
               rarity_of_large_tunnels = var11 > 23.0 && var11 < 33.0 ? 2 : 10;
            }

            if (this.rand.nextInt(4) == 0) {
               this.generateLargeCaveNode(this.rand.nextLong(), par4, par5, par6ArrayOfByte, var9, var11, var13);
               var15 += this.rand.nextInt(4);
            }

            for(int var16 = 0; var16 < var15; ++var16) {
               float var17 = this.rand.nextFloat() * 3.1415927F * 2.0F;
               float var18 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
               float var19 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
               if (this.rand.nextInt(rarity_of_large_tunnels) == 0) {
                  var19 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                  if (rarity_of_large_tunnels < 10) {
                     var19 *= 1.0F + this.rand.nextFloat() * 0.5F;
                  }
               } else if (increase_frequency_of_larger_tunnels && var11 < 41.0 && var11 > 15.0 && this.rand.nextInt(2) == 0) {
                  var19 *= this.rand.nextFloat() * this.rand.nextFloat() * 1.5F + 1.0F;
               }

               this.generateCaveNode(this.rand.nextLong(), par4, par5, par6ArrayOfByte, var9, var11, var13, var19, var17, var18, 0, 0, 1.0);
            }
         }

      }
   }

   static {
      grass_block_id = (byte)Block.grass.blockID;
      stone_block_id = (byte)Block.stone.blockID;
      dirt_block_id = (byte)Block.dirt.blockID;
      water_moving_block_id = (byte)Block.waterMoving.blockID;
      water_still_block_id = (byte)Block.waterStill.blockID;
      lava_moving_block_id = (byte)Block.lavaMoving.blockID;
      sand_block_id = (byte)Block.sand.blockID;
      sand_stone_block_id = (byte)Block.sandStone.blockID;
   }

   protected boolean isOceanBlock(byte[] data, int index, int x, int y, int z, int chunkX, int chunkZ)
   {
      return data[index] == Block.waterMoving.blockID || data[index] == Block.waterStill.blockID;
   }

   //Exception biomes to make sure we generate like vanilla
   private boolean isExceptionBiome(BiomeGenBase biome)
   {
      if (biome == BiomeGenBase.beach) return true;
      if (biome == BiomeGenBase.desert) return true;
      return false;
   }

   //Determine if the block at the specified location is the top block for the biome, we take into account
   //Vanilla bugs to make sure that we generate the map the same way vanilla does.
   private boolean isTopBlock(byte[] data, int index, int x, int y, int z, int chunkX, int chunkZ)
   {
      BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
      return (isExceptionBiome(biome) ? data[index] == Block.grass.blockID : data[index] == biome.topBlock);
   }

   /**
    * Digs out the current block, default implementation removes stone, filler, and top block
    * Sets the block to lava if y is less then 10, and air other wise.
    * If setting to air, it also checks to see if we've broken the surface and if so
    * tries to make the floor the biome's top block
    *
    * @param data Block data array
    * @param index Pre-calculated index into block data
    * @param x local X position
    * @param y local Y position
    * @param z local Z position
    * @param chunkX Chunk X position
    * @param chunkZ Chunk Y position
    * @param foundTop True if we've encountered the biome's top block. Ideally if we've broken the surface.
    */
   protected void digBlock(byte[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
   {
      BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
      int top    = (isExceptionBiome(biome) ? Block.grass.blockID : biome.topBlock);
      int filler = (isExceptionBiome(biome) ? Block.dirt.blockID  : biome.fillerBlock);
      int block  = data[index];

      if (block == Block.stone.blockID || block == filler || block == top)
      {
         if (y < 10)
         {
            data[index] = (byte)Block.lavaMoving.blockID;
         }
         else
         {
            data[index] = 0;

            if (foundTop && data[index - 1] == filler)
            {
               data[index - 1] = (byte)top;
            }
         }
      }
   }
}
