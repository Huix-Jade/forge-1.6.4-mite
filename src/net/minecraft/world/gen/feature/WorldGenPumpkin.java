package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenPumpkin extends WorldGenerator {
   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      for(int var6 = 0; var6 < 64; ++var6) {
         int var7 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
         int var8 = par4 + par2Random.nextInt(4) - par2Random.nextInt(4);
         int var9 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);
         int metadata = par2Random.nextInt(4);
         if (par1World.isAirBlock(var7, var8, var9) && par1World.getBlockId(var7, var8 - 1, var9) == Block.grass.blockID && Block.pumpkin.canOccurAt(par1World, var7, var8, var9, metadata)) {
            par1World.setBlock(var7, var8, var9, Block.pumpkin.blockID, metadata, 2);
         }
      }

      return true;
   }
}
