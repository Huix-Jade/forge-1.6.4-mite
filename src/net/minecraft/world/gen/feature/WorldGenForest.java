package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenerator {
   public WorldGenForest(boolean par1) {
      super(par1);
   }

   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      int var6 = par2Random.nextInt(3) + 5;
      boolean var7 = true;
      if (par4 >= 1 && par4 + var6 + 1 <= 256) {
         int var8;
         int var10;
         int var11;
         int var12;
         int var17;
         for(var8 = par4; var8 <= par4 + 1 + var6; ++var8) {
            var17 = 1;
            if (var8 == par4) {
               var17 = 0;
            }

            if (var8 >= par4 + 1 + var6 - 2) {
               var17 = 2;
            }

            for(var10 = par3 - var17; var10 <= par3 + var17 && var7; ++var10) {
               for(var11 = par5 - var17; var11 <= par5 + var17 && var7; ++var11) {
                  if (var8 >= 0 && var8 < 256) {
                     var12 = par1World.getBlockId(var10, var8, var11);
                     if (var12 != 0 && var12 != Block.leaves.blockID) {
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
               this.setBlock(par1World, par3, par4 - 1, par5, Block.dirt.blockID);

               for(var17 = par4 - 3 + var6; var17 <= par4 + var6; ++var17) {
                  var10 = var17 - (par4 + var6);
                  var11 = 1 - var10 / 2;

                  for(var12 = par3 - var11; var12 <= par3 + var11; ++var12) {
                     int var13 = var12 - par3;

                     for(int var14 = par5 - var11; var14 <= par5 + var11; ++var14) {
                        int var15 = var14 - par5;
                        if (Math.abs(var13) != var11 || Math.abs(var15) != var11 || par2Random.nextInt(2) != 0 && var10 != 0) {
                           int var16 = par1World.getBlockId(var12, var17, var14);
                           if (var16 == 0 || var16 == Block.leaves.blockID) {
                              this.setBlockAndMetadata(par1World, var12, var17, var14, Block.leaves.blockID, 2);
                           }
                        }
                     }
                  }
               }

               for(var17 = 0; var17 < var6; ++var17) {
                  var10 = par1World.getBlockId(par3, par4 + var17, par5);
                  if (var10 == 0 || var10 == Block.leaves.blockID) {
                     this.setBlockAndMetadata(par1World, par3, par4 + var17, par5, Block.wood.blockID, 2);
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
}
