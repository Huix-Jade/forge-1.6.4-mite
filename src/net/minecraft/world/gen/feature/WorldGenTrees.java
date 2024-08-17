package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class WorldGenTrees extends WorldGenerator {
   private final int minTreeHeight;
   private final boolean vinesGrow;
   private final int metaWood;
   private final int metaLeaves;

   public WorldGenTrees(boolean par1) {
      this(par1, 4, 0, 0, false);
   }

   public WorldGenTrees(boolean par1, int par2, int par3, int par4, boolean par5) {
      super(par1);
      this.minTreeHeight = par2;
      this.metaWood = par3;
      this.metaLeaves = par4;
      this.vinesGrow = par5;
   }

   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      int var6 = par2Random.nextInt(3) + this.minTreeHeight;
      boolean var7 = true;
      if (par4 >= 1 && par4 + var6 + 1 <= 256) {
         int i1;
         byte var9;
         int j1;
         int var12;
         int l1;
         for(i1 = par4; i1 <= par4 + 1 + var6; ++i1) {
            var9 = 1;
            if (i1 == par4) {
               var9 = 0;
            }

            if (i1 >= par4 + 1 + var6 - 2) {
               var9 = 2;
            }

            for(l1 = par3 - var9; l1 <= par3 + var9 && var7; ++l1) {
               for(j1 = par5 - var9; j1 <= par5 + var9 && var7; ++j1) {
                  if (i1 >= 0 && i1 < 256) {
                     var12 = par1World.getBlockId(l1, i1, j1);
                     Block block = Block.blocksList[var12];

                     if (!par1World.isAirBlock(l1, i1, j1) &&
                             !block.isLeaves(par1World, l1, i1, j1) &&
                             var12 != Block.grass.blockID &&
                             var12 != Block.dirt.blockID &&
                             !block.isWood(par1World, l1, i1, j1)) {
                        var7 = false;
                     }
                  } else {
                     var7 = false;
                  }
               }
            }
         }

         if (!var7) {
            return false;
         } else {
            l1 = 0;
            i1 = par1World.getBlockId(par3, par4 - 1, par5);
            Block soil = Block.blocksList[i1];
            boolean isSoil = (soil != null && soil.canSustainPlant(par1World, par3, par4 - 1, par5, ForgeDirection.UP, (BlockSapling)Block.sapling));

            if (isSoil && par4 < 256 - var6 - 1) {
               soil.onPlantGrow(par1World, par3, par4 - 1, par5, par3, par4, par5);
               var9 = 3;
               byte var19 = 0;

               int var13;
               int var14;
               int var15;
               for(j1 = par4 - var9 + var6; j1 <= par4 + var6; ++j1) {
                  var12 = j1 - (par4 + var6);
                  var13 = var19 + 1 - var12 / 2;
                  if (var13 > l1) {
                     l1 = var13;
                  }

                  for(var14 = par3 - var13; var14 <= par3 + var13; ++var14) {
                     var15 = var14 - par3;

                     for(int var16 = par5 - var13; var16 <= par5 + var13; ++var16) {
                        int var17 = var16 - par5;
                        if (var15 != var13 || -var15 != var13 || var17 != var13 || -var17 != var13 || par2Random.nextInt(2) != 0 && var12 != 0) {
                           int var18 = par1World.getBlockId(var14, j1, var16);
                           Block block = Block.blocksList[var18];

                           if (block == null || block.canBeReplacedByLeaves(par1World, var14, j1, var16)) {
                              this.setBlockAndMetadata(par1World, var14, j1, var16, Block.leaves.blockID, this.metaLeaves);
                           }
                        }
                     }
                  }
               }

               for(j1 = 0; j1 < var6; ++j1) {
                  var12 = par1World.getBlockId(par3, par4 + j1, par5);
                  Block block = Block.blocksList[var12];

                  if (block == null || block.isAirBlock(par1World, par3, par4 + j1, par5) || block.isLeaves(par1World, par3, par4 + j1, par5)) {
                     this.setBlockAndMetadata(par1World, par3, par4 + j1, par5, Block.wood.blockID, this.metaWood);
                     if (this.vinesGrow && j1 > 0) {
                        if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3 - 1, par4 + j1, par5)) {
                           this.setBlockAndMetadata(par1World, par3 - 1, par4 + j1, par5, Block.vine.blockID, 8);
                        }

                        if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3 + 1, par4 + j1, par5)) {
                           this.setBlockAndMetadata(par1World, par3 + 1, par4 + j1, par5, Block.vine.blockID, 2);
                        }

                        if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3, par4 + j1, par5 - 1)) {
                           this.setBlockAndMetadata(par1World, par3, par4 + j1, par5 - 1, Block.vine.blockID, 1);
                        }

                        if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3, par4 + j1, par5 + 1)) {
                           this.setBlockAndMetadata(par1World, par3, par4 + j1, par5 + 1, Block.vine.blockID, 4);
                        }
                     }
                  }
               }

               if (this.vinesGrow) {
                  for(j1 = par4 - 3 + var6; j1 <= par4 + var6; ++j1) {
                     var12 = j1 - (par4 + var6);
                     var13 = 2 - var12 / 2;

                     for(var14 = par3 - var13; var14 <= par3 + var13; ++var14) {
                        for(var15 = par5 - var13; var15 <= par5 + var13; ++var15) {
                           Block block = Block.blocksList[par1World.getBlockId(var14, j1, var15)];
                           if (block != null && block.isLeaves(par1World, var14, j1, var15)) {
                              if (par2Random.nextInt(4) == 0 && par1World.isAirBlock(var14 - 1, j1, var15)) {
                                 this.growVines(par1World, var14 - 1, j1, var15, 8);
                              }

                              if (par2Random.nextInt(4) == 0 && par1World.isAirBlock(var14 + 1, j1, var15)) {
                                 this.growVines(par1World, var14 + 1, j1, var15, 2);
                              }

                              if (par2Random.nextInt(4) == 0 && par1World.isAirBlock(var14, j1, var15 - 1)) {
                                 this.growVines(par1World, var14, j1, var15 - 1, 1);
                              }

                              if (par2Random.nextInt(4) == 0 && par1World.isAirBlock(var14, j1, var15 + 1)) {
                                 this.growVines(par1World, var14, j1, var15 + 1, 4);
                              }
                           }
                        }
                     }
                  }

                  if (par2Random.nextInt(5) == 0 && var6 > 5) {
                     for(j1 = 0; j1 < 2; ++j1) {
                        for(var12 = 0; var12 < 4; ++var12) {
                           if (par2Random.nextInt(4 - j1) == 0) {
                              var13 = par2Random.nextInt(3);
                              this.setBlockAndMetadata(par1World, par3 + Direction.offsetX[Direction.rotateOpposite[var12]], par4 + var6 - 5 + j1, par5 + Direction.offsetZ[Direction.rotateOpposite[var12]], Block.cocoaPlant.blockID, var13 << 2 | var12);
                           }
                        }
                     }
                  }
               }

               if (par1World.decorating) {
                  par1World.placeNaturallyOccurringSnow(par3 - l1, par5 - l1, par3 + l1, par5 + l1);
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

   private void growVines(World par1World, int par2, int par3, int par4, int par5) {
      this.setBlockAndMetadata(par1World, par2, par3, par4, Block.vine.blockID, par5);
      int var6 = 4;

      while(true) {
         --par3;
         if (!par1World.isAirBlock(par2, par3, par4)|| var6 <= 0) {
            return;
         }

         this.setBlockAndMetadata(par1World, par2, par3, par4, Block.vine.blockID, par5);
         --var6;
      }
   }
}
