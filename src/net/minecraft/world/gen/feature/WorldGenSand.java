package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class WorldGenSand extends WorldGenerator {
   private int sandID;
   private int radius;

   public WorldGenSand(int par1, int par2) {
      this.sandID = par2;
      this.radius = par1;
   }

   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      if (par1World.getBlockMaterial(par3, par4, par5) != Material.water) {
         return false;
      } else {
         int var6 = par2Random.nextInt(this.radius - 2) + 2;
         byte var7 = 2;

         for(int var8 = par3 - var6; var8 <= par3 + var6; ++var8) {
            for(int var9 = par5 - var6; var9 <= par5 + var6; ++var9) {
               int var10 = var8 - par3;
               int var11 = var9 - par5;
               if (var10 * var10 + var11 * var11 <= var6 * var6) {
                  for(int var12 = par4 - var7; var12 <= par4 + var7; ++var12) {
                     int var13 = par1World.getBlockId(var8, var12, var9);
                     if (var13 == Block.dirt.blockID || var13 == Block.grass.blockID) {
                        par1World.setBlock(var8, var12, var9, this.sandID, 0, 2);
                        Block block_above = par1World.getBlock(var8, var12 + 1, var9);
                        if (block_above != null) {
                           int x = var8;
                           int y = var12;
                           int z = var9;
                           int block_above_metadata = par1World.getBlockMetadata(x, y + 1, z);
                           Block block = par1World.getBlock(x, y, z);
                           int metadata = par1World.getBlockMetadata(x, y, z);
                           if (!block_above.isLegalOn(block_above_metadata, block, metadata)) {
                              par1World.setBlockToAir(x, y + 1, z, 2);
                           }
                        }
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
