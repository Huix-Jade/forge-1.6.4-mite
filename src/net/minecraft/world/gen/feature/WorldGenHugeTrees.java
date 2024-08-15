package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenHugeTrees extends WorldGenerator {
   private final int baseHeight;
   private final int woodMetadata;
   private final int leavesMetadata;

   public WorldGenHugeTrees(boolean par1, int par2, int par3, int par4) {
      super(par1);
      this.baseHeight = par2;
      this.woodMetadata = par3;
      this.leavesMetadata = par4;
   }

   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      int var6 = par2Random.nextInt(3) + this.baseHeight;
      boolean var7 = true;
      if (par4 >= 1 && par4 + var6 + 1 <= 256) {
         int var8;
         int var10;
         int var11;
         int var12;
         int var14;
         for(var8 = par4; var8 <= par4 + 1 + var6; ++var8) {
            var14 = 2;
            if (var8 == par4) {
               var14 = 1;
            }

            if (var8 >= par4 + 1 + var6 - 2) {
               var14 = 2;
            }

            for(var10 = par3 - var14; var10 <= par3 + var14 && var7; ++var10) {
               for(var11 = par5 - var14; var11 <= par5 + var14 && var7; ++var11) {
                  if (var8 >= 0 && var8 < 256) {
                     var12 = par1World.getBlockId(var10, var8, var11);
                     if (var12 != 0 && var12 != Block.leaves.blockID && var12 != Block.grass.blockID && var12 != Block.dirt.blockID && var12 != Block.wood.blockID && var12 != Block.sapling.blockID) {
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
            var8 = par1World.getBlockId(par3, par4 - 1, par5);
            if ((var8 == Block.grass.blockID || var8 == Block.dirt.blockID) && par4 < 256 - var6 - 1) {
               par1World.setBlock(par3, par4 - 1, par5, Block.dirt.blockID, 0, 2);
               par1World.setBlock(par3 + 1, par4 - 1, par5, Block.dirt.blockID, 0, 2);
               par1World.setBlock(par3, par4 - 1, par5 + 1, Block.dirt.blockID, 0, 2);
               par1World.setBlock(par3 + 1, par4 - 1, par5 + 1, Block.dirt.blockID, 0, 2);
               this.growLeaves(par1World, par3, par5, par4 + var6, 2, par2Random);

               for(var14 = par4 + var6 - 2 - par2Random.nextInt(4); var14 > par4 + var6 / 2; var14 -= 2 + par2Random.nextInt(4)) {
                  float var15 = par2Random.nextFloat() * 3.1415927F * 2.0F;
                  var11 = par3 + (int)(0.5F + MathHelper.cos(var15) * 4.0F);
                  var12 = par5 + (int)(0.5F + MathHelper.sin(var15) * 4.0F);
                  this.growLeaves(par1World, var11, var12, var14, 0, par2Random);

                  for(int var13 = 0; var13 < 5; ++var13) {
                     var11 = par3 + (int)(1.5F + MathHelper.cos(var15) * (float)var13);
                     var12 = par5 + (int)(1.5F + MathHelper.sin(var15) * (float)var13);
                     this.setBlockAndMetadata(par1World, var11, var14 - 3 + var13 / 2, var12, Block.wood.blockID, this.woodMetadata);
                  }
               }

               for(var10 = 0; var10 < var6; ++var10) {
                  var11 = par1World.getBlockId(par3, par4 + var10, par5);
                  if (var11 == 0 || var11 == Block.leaves.blockID) {
                     this.setBlockAndMetadata(par1World, par3, par4 + var10, par5, Block.wood.blockID, this.woodMetadata);
                     if (var10 > 0) {
                        if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3 - 1, par4 + var10, par5)) {
                           this.setBlockAndMetadata(par1World, par3 - 1, par4 + var10, par5, Block.vine.blockID, 8);
                        }

                        if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3, par4 + var10, par5 - 1)) {
                           this.setBlockAndMetadata(par1World, par3, par4 + var10, par5 - 1, Block.vine.blockID, 1);
                        }
                     }
                  }

                  if (var10 < var6 - 1) {
                     var11 = par1World.getBlockId(par3 + 1, par4 + var10, par5);
                     if (var11 == 0 || var11 == Block.leaves.blockID) {
                        this.setBlockAndMetadata(par1World, par3 + 1, par4 + var10, par5, Block.wood.blockID, this.woodMetadata);
                        if (var10 > 0) {
                           if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3 + 2, par4 + var10, par5)) {
                              this.setBlockAndMetadata(par1World, par3 + 2, par4 + var10, par5, Block.vine.blockID, 2);
                           }

                           if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3 + 1, par4 + var10, par5 - 1)) {
                              this.setBlockAndMetadata(par1World, par3 + 1, par4 + var10, par5 - 1, Block.vine.blockID, 1);
                           }
                        }
                     }

                     var11 = par1World.getBlockId(par3 + 1, par4 + var10, par5 + 1);
                     if (var11 == 0 || var11 == Block.leaves.blockID) {
                        this.setBlockAndMetadata(par1World, par3 + 1, par4 + var10, par5 + 1, Block.wood.blockID, this.woodMetadata);
                        if (var10 > 0) {
                           if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3 + 2, par4 + var10, par5 + 1)) {
                              this.setBlockAndMetadata(par1World, par3 + 2, par4 + var10, par5 + 1, Block.vine.blockID, 2);
                           }

                           if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3 + 1, par4 + var10, par5 + 2)) {
                              this.setBlockAndMetadata(par1World, par3 + 1, par4 + var10, par5 + 2, Block.vine.blockID, 4);
                           }
                        }
                     }

                     var11 = par1World.getBlockId(par3, par4 + var10, par5 + 1);
                     if (var11 == 0 || var11 == Block.leaves.blockID) {
                        this.setBlockAndMetadata(par1World, par3, par4 + var10, par5 + 1, Block.wood.blockID, this.woodMetadata);
                        if (var10 > 0) {
                           if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3 - 1, par4 + var10, par5 + 1)) {
                              this.setBlockAndMetadata(par1World, par3 - 1, par4 + var10, par5 + 1, Block.vine.blockID, 8);
                           }

                           if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(par3, par4 + var10, par5 + 2)) {
                              this.setBlockAndMetadata(par1World, par3, par4 + var10, par5 + 2, Block.vine.blockID, 4);
                           }
                        }
                     }
                  }
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

   private void growLeaves(World par1World, int par2, int par3, int par4, int par5, Random par6Random) {
      byte var7 = 2;

      for(int var8 = par4 - var7; var8 <= par4; ++var8) {
         int var9 = var8 - par4;
         int var10 = par5 + 1 - var9;

         for(int var11 = par2 - var10; var11 <= par2 + var10; ++var11) {
            int var12 = var11 - par2;

            for(int var13 = par3 - var10; var13 <= par3 + var10; ++var13) {
               int var14 = var13 - par3;
               if ((var12 >= 0 || var14 >= 0 || var12 * var12 + var14 * var14 <= var10 * var10) && (var12 <= 0 && var14 <= 0 || var12 * var12 + var14 * var14 <= (var10 + 1) * (var10 + 1)) && (par6Random.nextInt(4) != 0 || var12 * var12 + var14 * var14 <= (var10 - 1) * (var10 - 1))) {
                  int var15 = par1World.getBlockId(var11, var8, var13);
                  if (var15 == 0 || var15 == Block.leaves.blockID) {
                     this.setBlockAndMetadata(par1World, var11, var8, var13, Block.leaves.blockID, this.leavesMetadata);
                  }
               }
            }
         }
      }

   }
}
