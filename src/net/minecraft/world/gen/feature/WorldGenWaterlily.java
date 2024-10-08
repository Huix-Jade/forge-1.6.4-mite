package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenWaterlily extends WorldGenerator {
   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      for(int var6 = 0; var6 < 10; ++var6) {
         int var7 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
         int var8 = par4 + par2Random.nextInt(4) - par2Random.nextInt(4);
         int var9 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);
         if (par1World.isAirBlock(var7, var8, var9) && Block.waterlily.canOccurAt(par1World, var7, var8, var9, 0)) {
            par1World.setBlock(var7, var8, var9, Block.waterlily.blockID, 0, 2);
         }
      }

      return true;
   }
}
