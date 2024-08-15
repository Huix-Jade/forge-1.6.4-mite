package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class WorldGenClay extends WorldGenerator {
   private int clayBlockId;
   private int numberOfBlocks;

   public WorldGenClay(int var1) {
      this.clayBlockId = Block.blockClay.blockID;
      this.numberOfBlocks = var1;
   }

   public boolean generate(World var1, Random var2, int var3, int var4, int var5) {
      if (var1.getBlockMaterial(var3, var4, var5) != Material.water) {
         return false;
      } else {
         int var6 = var2.nextInt(this.numberOfBlocks - 2) + 2;
         byte var7 = 1;

         for(int var8 = var3 - var6; var8 <= var3 + var6; ++var8) {
            for(int var9 = var5 - var6; var9 <= var5 + var6; ++var9) {
               int var10 = var8 - var3;
               int var11 = var9 - var5;
               if (var10 * var10 + var11 * var11 <= var6 * var6) {
                  for(int var12 = var4 - var7; var12 <= var4 + var7; ++var12) {
                     int var13 = var1.getBlockId(var8, var12, var9);
                     if (var13 == Block.dirt.blockID || var13 == Block.blockClay.blockID) {
                        var1.setBlock(var8, var12, var9, this.clayBlockId, 0, 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
