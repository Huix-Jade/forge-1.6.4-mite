package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenDeadBush extends WorldGenerator {
   private int deadBushID;
   private Block block;

   public WorldGenDeadBush(int par1) {
      this.deadBushID = par1;
      this.block = Block.getBlock(this.deadBushID);
   }

   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5) {
      boolean is_nether_world = par1World.isTheNether();
      int metadata = is_nether_world ? 1 : 0;

      int var11;
      Block block = null;
      do
      {
      block = Block.blocksList[par1World.getBlockId(par3,  par4, par5)];
      if (block != null && !block.isLeaves(par1World, par3, par4, par5))
      {
         break;
      }
      par4--;
   } while (par4 > 0);

      for(int var7 = 0; var7 < 4; ++var7) {
         int var8 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
         int var9 = par4 + par2Random.nextInt(4) - par2Random.nextInt(4);
         int var10 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);
         if (par1World.isAirBlock(var8, var9, var10) && this.block.canOccurAt(par1World, var8, var9, var10, metadata)) {
            par1World.setBlock(var8, var9, var10, this.deadBushID, metadata, 2);
         }
      }

      return true;
   }
}
