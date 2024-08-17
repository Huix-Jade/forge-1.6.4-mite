package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

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
                     Block block = Block.blocksList[var12];

                     if (block != null &&
                             !block.isAirBlock(par1World, var10, var8, var11) &&
                             !block.isLeaves(par1World, var10, var8, var11) &&
                             !block.isWood(par1World, var10, var8, var11) &&
                             block != Block.grass &&
                             block != Block.dirt &&
                             block != Block.sapling) {
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
            Block soil = Block.blocksList[var8];
            boolean isValidSoil = soil != null && soil.canSustainPlant(par1World, par3, par4 - 1, par5, ForgeDirection.UP, (BlockSapling)Block.sapling);

            if (isValidSoil && par4 < 256 - var6 - 1)
            {
               onPlantGrow(par1World, par3,     par4 - 1, par5,     par3, par4, par5);
               onPlantGrow(par1World, par3 + 1, par4 - 1, par5,     par3, par4, par5);
               onPlantGrow(par1World, par3,     par4 - 1, par5 + 1, par3, par4, par5);
               onPlantGrow(par1World, par3 + 1, par4 - 1, par5 + 1, par3, par4, par5);
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
                  if (isReplaceable(par1World, par3, par4 + var10, par5)) {
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
                     if (isReplaceable(par1World, par3 + 1, par4 + var10, par5)) {
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
                     if (isReplaceable(par1World, par3 + 1, par4 + var10, par5 + 1)) {
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
                     if (isReplaceable(par1World, par3, par4 + var10, par5 + 1)) {
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
                  Block block = Block.blocksList[var15];

                  if (block == null || block.canBeReplacedByLeaves(par1World, var11, var8, var13)) {
                     this.setBlockAndMetadata(par1World, var11, var8, var13, Block.leaves.blockID, this.leavesMetadata);
                  }
               }
            }
         }
      }

   }

   private void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
   {
      Block block = Block.blocksList[world.getBlockId(x, y, z)];
      if (block != null)
      {
         block.onPlantGrow(world, x, y, z, sourceX, sourceY, sourceZ);
      }
   }

   private boolean isReplaceable(World world, int x, int y, int z)
   {
      Block block = Block.blocksList[world.getBlockId(x, y, z)];
      return (block == null || block.isAirBlock(world, x, y, z) || block.isLeaves(world, x, y, z));
   }
}
