package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenTaiga1 extends WorldGenerator {
   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      int var6 = par2Random.nextInt(5) + 7;
      int var7 = var6 - par2Random.nextInt(2) - 3;
      int var8 = var6 - var7;
      int var9 = 1 + par2Random.nextInt(var8 + 1);
      boolean var10 = true;
      if (par4 >= 1 && par4 + var6 + 1 <= 128) {
         int l1;
         int i2;
         int j2;
         int k2;
         int var18;
         for(l1 = par4; l1 <= par4 + 1 + var6 && var10; ++l1) {
            boolean var12 = true;
            if (l1 - par4 < var7) {
               var18 = 0;
            } else {
               var18 = var9;
            }

            for(i2 = par3 - var18; i2 <= par3 + var18 && var10; ++i2) {
               for(j2 = par5 - var18; j2 <= par5 + var18 && var10; ++j2) {
                  if (l1 >= 0 && l1 < 128) {
                     k2 = par1World.getBlockId(i2, l1, j2);
                     Block block = Block.blocksList[k2];

                     if (k2 != 0 && (block == null || !block.isLeaves(par1World, i2, l1, j2))) {
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
            l1 = par1World.getBlockId(par3, par4 - 1, par5);
            if ((l1 == Block.grass.blockID || l1 == Block.dirt.blockID) && par4 < 128 - var6 - 1) {
               this.setBlock(par1World, par3, par4 - 1, par5, Block.dirt.blockID);
               var18 = 0;

               for(i2 = par4 + var6; i2 >= par4 + var7; --i2) {
                  for(j2 = par3 - var18; j2 <= par3 + var18; ++j2) {
                     k2 = j2 - par3;

                     for(int l2 = par5 - var18; l2 <= par5 + var18; ++l2) {
                        int var17 = l2 - par5;
                        Block block = Block.blocksList[par1World.getBlockId(j2, i2, l2)];

                        if ((Math.abs(k2) != l2 || Math.abs(var17) != l2 || l2 <= 0) &&
                                (block == null || block.canBeReplacedByLeaves(par1World, j2, i2, l2))) {
                           this.setBlockAndMetadata(par1World, j2, i2, l2, Block.leaves.blockID, 1);
                        }
                     }
                  }

                  if (var18 >= 1 && i2 == par4 + var7 + 1) {
                     --var18;
                  } else if (var18 < var9) {
                     ++var18;
                  }
               }

               for(i2 = 0; i2 < var6 - 1; ++i2) {
                  j2 = par1World.getBlockId(par3, par4 + i2, par5);
                  Block block = Block.blocksList[j2];

                  if (j2 == 0 || block == null || block.isLeaves(par1World, par3, par4 + i2, par5)) {
                     this.setBlockAndMetadata(par1World, par3, par4 + i2, par5, Block.wood.blockID, 1);
                  }
               }

               if (par1World.decorating) {
                  par1World.placeNaturallyOccurringSnow(par3 - var18, par5 - var18, par3 + var18, par5 + var18);
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
