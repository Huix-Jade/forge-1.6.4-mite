package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class WorldGenTaiga2 extends WorldGenerator {
   public WorldGenTaiga2(boolean par1) {
      super(par1);
   }

   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      int var6 = par2Random.nextInt(4) + 6;
      int var7 = 1 + par2Random.nextInt(2);
      int var8 = var6 - var7;
      int var9 = 2 + par2Random.nextInt(2);
      boolean var10 = true;
      if (par4 >= 1 && par4 + var6 + 1 <= 256) {
         int l1;
         int i2;
         int var15;
         int var21;
         for(l1 = par4; l1 <= par4 + 1 + var6 && var10; ++l1) {
            boolean var12 = true;
            if (l1 - par4 < var7) {
               var21 = 0;
            } else {
               var21 = var9;
            }

            for(i2 = par3 - var21; i2 <= par3 + var21 && var10; ++i2) {
               for(int l2 = par5 - var21; l2 <= par5 + var21 && var10; ++l2) {
                  if (l1 >= 0 && l1 < 256) {
                     var15 = par1World.getBlockId(i2, l1, l2);
                     Block block = Block.blocksList[var15];

                     if (var15 != 0 && block != null && !block.isLeaves(par1World, i2, l1, l2)) {
                        var10 = false;
                     }
                  } else {
                     var10 = false;
                  }
               }
            }
         }

         if (!var10) {
            return false;
         } else {
            int radius = 0;
            l1 = par1World.getBlockId(par3, par4 - 1, par5);
            Block soil = Block.blocksList[l1];
            boolean isValidSoil = soil != null && soil.canSustainPlant(par1World, par3, par4 - 1, par5, ForgeDirection.UP, (BlockSapling)Block.sapling);

            if (isValidSoil && par4 < 256 - var6 - 1) {
               soil.onPlantGrow(par1World, par3, par4 - 1, par5, par3, par4, par5);
               var21 = par2Random.nextInt(2);
               i2 = 1;
               byte var22 = 0;

               int var17;
               int var16;
               for(var15 = 0; var15 <= var8; ++var15) {
                  var16 = par4 + var6 - var15;

                  for(var17 = par3 - var21; var17 <= par3 + var21; ++var17) {
                     int var18 = var17 - par3;

                     for(int var19 = par5 - var21; var19 <= par5 + var21; ++var19) {
                        int var20 = var19 - par5;
                        Block block = Block.blocksList[par1World.getBlockId(var17, var16, var19)];

                        if ((Math.abs(var18) != var21 || Math.abs(var20) != var21 || var21 <= 0) &&
                                (block == null || block.canBeReplacedByLeaves(par1World, var17, var16, var19))) {
                           this.setBlockAndMetadata(par1World, var17, var16, var19, Block.leaves.blockID, 1);
                           if (var21 > radius) {
                              radius = var21;
                           }
                        }
                     }
                  }

                  if (var21 >= i2) {
                     var21 = var22;
                     var22 = 1;
                     ++i2;
                     if (i2 > var9) {
                        i2 = var9;
                     }
                  } else {
                     ++var21;
                  }
               }

               var15 = par2Random.nextInt(3);

               for(var16 = 0; var16 < var6 - var15; ++var16) {
                  var17 = par1World.getBlockId(par3, par4 + var16, par5);
                  Block block = Block.blocksList[var17];

                  if (var17 == 0 || block == null || block.isLeaves(par1World, par3, par4 + var16, par5)) {
                     this.setBlockAndMetadata(par1World, par3, par4 + var16, par5, Block.wood.blockID, 1);
                  }
               }

               if (par1World.decorating) {
                  par1World.placeNaturallyOccurringSnow(par3 - radius, par5 - radius, par3 + radius, par5 + radius);
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
