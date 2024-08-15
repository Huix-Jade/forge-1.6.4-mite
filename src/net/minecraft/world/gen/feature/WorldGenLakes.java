package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenLakes extends WorldGenerator
{
   private int blockIndex;

   public WorldGenLakes(int par1)
   {
      this.blockIndex = par1;
   }

   public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
   {
      par3 -= 8;

      for (par5 -= 8; par4 > 5 && par1World.isAirBlock(par3, par4, par5); --par4)
      {
         ;
      }

      if (par4 <= 4)
      {
         return false;
      }
      else
      {
         par4 -= 4;
         boolean[] var6 = new boolean[2048];
         int var7 = par2Random.nextInt(4) + 4;
         int var8;

         for (var8 = 0; var8 < var7; ++var8)
         {
            double var9 = par2Random.nextDouble() * 6.0D + 3.0D;
            double var11 = par2Random.nextDouble() * 4.0D + 2.0D;
            double var13 = par2Random.nextDouble() * 6.0D + 3.0D;
            double var15 = par2Random.nextDouble() * (16.0D - var9 - 2.0D) + 1.0D + var9 / 2.0D;
            double var17 = par2Random.nextDouble() * (8.0D - var11 - 4.0D) + 2.0D + var11 / 2.0D;
            double var19 = par2Random.nextDouble() * (16.0D - var13 - 2.0D) + 1.0D + var13 / 2.0D;

            for (int var21 = 1; var21 < 15; ++var21)
            {
               for (int var22 = 1; var22 < 15; ++var22)
               {
                  for (int var23 = 1; var23 < 7; ++var23)
                  {
                     double var24 = ((double)var21 - var15) / (var9 / 2.0D);
                     double var26 = ((double)var23 - var17) / (var11 / 2.0D);
                     double var28 = ((double)var22 - var19) / (var13 / 2.0D);
                     double var30 = var24 * var24 + var26 * var26 + var28 * var28;

                     if (var30 < 1.0D)
                     {
                        var6[(var21 * 16 + var22) * 8 + var23] = true;
                     }
                  }
               }
            }
         }

         int var10;
         int var32;
         boolean var33;

         for (var8 = 0; var8 < 16; ++var8)
         {
            for (var10 = 0; var10 < 16; ++var10)
            {
               for (var32 = 0; var32 < 8; ++var32)
               {
                  var33 = !var6[(var8 * 16 + var10) * 8 + var32] && (var8 < 15 && var6[((var8 + 1) * 16 + var10) * 8 + var32] || var8 > 0 && var6[((var8 - 1) * 16 + var10) * 8 + var32] || var10 < 15 && var6[(var8 * 16 + var10 + 1) * 8 + var32] || var10 > 0 && var6[(var8 * 16 + (var10 - 1)) * 8 + var32] || var32 < 7 && var6[(var8 * 16 + var10) * 8 + var32 + 1] || var32 > 0 && var6[(var8 * 16 + var10) * 8 + (var32 - 1)]);

                  if (var33)
                  {
                     Material var12 = par1World.getBlockMaterial(par3 + var8, par4 + var32, par5 + var10);

                     if (var32 >= 4 && var12.isLiquid())
                     {
                        return false;
                     }

                     if (var32 < 4 && !var12.isSolid() && par1World.getBlockId(par3 + var8, par4 + var32, par5 + var10) != this.blockIndex)
                     {
                        return false;
                     }
                  }
               }
            }
         }

         int var36;

         for (var8 = 0; var8 < 16; ++var8)
         {
            int var34 = var8 * 16;

            for (var10 = 0; var10 < 16; ++var10)
            {
               var36 = (var34 + var10) * 8;

               for (var32 = 0; var32 < 8; ++var32)
               {
                  if (var6[var36 + var32])
                  {
                     Block var14;

                     if (var32 < 3)
                     {
                        var14 = par1World.getBlock(par3 + var8, par4 + var32 + 1, par5 + var10);

                        if (var14 == Block.wood)
                        {
                           return false;
                        }
                     }
                     else if (var32 >= 4)
                     {
                        var14 = par1World.getBlock(par3 + var8, par4 + var32, par5 + var10);

                        if (var14 == Block.wood || var14 == Block.leaves || var14 == Block.planks)
                        {
                           var6[(var8 * 16 + var10) * 8 + var32] = false;
                        }
                     }
                     else
                     {
                        var14 = par1World.getBlock(par3 + var8, par4 + var32 + 1, par5 + var10);

                        if (var14 == Block.wood)
                        {
                           var6[(var8 * 16 + var10) * 8 + var32] = false;
                        }
                     }
                  }
               }
            }
         }

         Block var35 = Block.getBlock(this.blockIndex);
         int var16;
         int var18;
         int var38;
         int var39;
         Block var41;

         for (var8 = 0; var8 < 16; ++var8)
         {
            for (var10 = 0; var10 < 16; ++var10)
            {
               for (var32 = 0; var32 < 8; ++var32)
               {
                  if (var6[(var8 * 16 + var10) * 8 + var32])
                  {
                     var36 = par3 + var8;
                     var38 = par4 + var32;
                     var39 = par5 + var10;

                     if (var32 >= 4)
                     {
                        var16 = 0;
                     }
                     else
                     {
                        var16 = this.blockIndex;
                     }

                     par1World.setBlock(var36, var38, var39, var16, 0, 2);
                     var41 = par1World.getBlock(par3 + var8, par4 + var32 + 1, par5 + var10);

                     if (var41 != null && !var41.isLegalOn(par1World.getBlockMetadata(par3 + var8, par4 + var32 + 1, par5 + var10), var35, 0))
                     {
                        par1World.setBlock(par3 + var8, par4 + var32 + 1, par5 + var10, 0, 0, 2);
                        var18 = par4 + var32 + 1;

                        while (true)
                        {
                           ++var18;

                           if (var18 >= 256)
                           {
                              break;
                           }

                           var41 = par1World.getBlock(var36, var18, var39);

                           if (var41 == null || var41.isLegalOn(par1World.getBlockMetadata(var36, var18, var39), (Block)null, 0))
                           {
                              break;
                           }

                           par1World.setBlock(var36, var18, var39, 0, 0, 2);
                        }
                     }
                  }
               }
            }
         }

         for (var8 = 0; var8 < 16; ++var8)
         {
            for (var10 = 0; var10 < 16; ++var10)
            {
               for (var32 = 4; var32 < 8; ++var32)
               {
                  if (var6[(var8 * 16 + var10) * 8 + var32] && par1World.getBlockId(par3 + var8, par4 + var32 - 1, par5 + var10) == Block.dirt.blockID && par1World.getSavedLightValue(EnumSkyBlock.Sky, par3 + var8, par4 + var32, par5 + var10) > 0)
                  {
                     BiomeGenBase var37 = par1World.getBiomeGenForCoords(par3 + var8, par5 + var10);

                     if (var37.topBlock == Block.mycelium.blockID)
                     {
                        par1World.setBlock(par3 + var8, par4 + var32 - 1, par5 + var10, Block.mycelium.blockID, 0, 2);
                     }
                     else
                     {
                        par1World.setBlock(par3 + var8, par4 + var32 - 1, par5 + var10, Block.grass.blockID, 0, 2);
                     }
                  }
               }
            }
         }

         if (Block.blocksList[this.blockIndex].blockMaterial == Material.lava)
         {
            for (var8 = 0; var8 < 16; ++var8)
            {
               for (var10 = 0; var10 < 16; ++var10)
               {
                  for (var32 = 0; var32 < 8; ++var32)
                  {
                     var33 = !var6[(var8 * 16 + var10) * 8 + var32] && (var8 < 15 && var6[((var8 + 1) * 16 + var10) * 8 + var32] || var8 > 0 && var6[((var8 - 1) * 16 + var10) * 8 + var32] || var10 < 15 && var6[(var8 * 16 + var10 + 1) * 8 + var32] || var10 > 0 && var6[(var8 * 16 + (var10 - 1)) * 8 + var32] || var32 < 7 && var6[(var8 * 16 + var10) * 8 + var32 + 1] || var32 > 0 && var6[(var8 * 16 + var10) * 8 + (var32 - 1)]);

                     if (var33 && (var32 < 4 || par2Random.nextInt(2) != 0) && par1World.getBlockMaterial(par3 + var8, par4 + var32, par5 + var10).isSolid())
                     {
                        par1World.setBlock(par3 + var8, par4 + var32, par5 + var10, Block.stone.blockID, 0, 2);
                     }
                  }
               }
            }
         }

         for (var8 = 0; var8 < 16; ++var8)
         {
            for (var10 = 0; var10 < 16; ++var10)
            {
               var36 = par3 + var8;
               var38 = par5 + var10;
               Block var40;

               if (par1World.getBiomeGenForCoords(var36, var38).isFreezing())
               {
                  var39 = par1World.getPrecipitationHeight(var36, var38) - 1;
                  var40 = par1World.getBlock(var36, var39, var38);

                  if (var40 == Block.waterStill)
                  {
                     par1World.setBlock(var36, var39, var38, Block.ice.blockID, 0, 2);
                  }
                  else if (var40 != Block.ice && par1World.isBlockTopFlatAndSolid(var36, var39, var38) && par1World.isAirBlock(var36, var39 + 1, var38))
                  {
                     par1World.setBlock(var36, var39 + 1, var38, Block.snow.blockID, 0, 2);
                  }

                  for (int var42 = 7; var42 >= 0; --var42)
                  {
                     var39 = par4 + var42;

                     if (par1World.getBlock(var36, var39, var38) == Block.waterStill && par1World.isAirBlock(var36, var39 + 1, var38))
                     {
                        par1World.setBlock(var36, var39, var38, Block.ice.blockID, 0, 2);
                     }
                  }
               }
               else
               {
                  var39 = par1World.getPrecipitationHeight(var36, var38) - 1;
                  var40 = par1World.getBlock(var36, var39, var38);

                  if (var40 == Block.dirt)
                  {
                     par1World.setBlock(var36, var39, var38, Block.grass.blockID, 0, 2);
                  }
               }

               for (var39 = 7; var39 >= 0; --var39)
               {
                  var16 = par4 + var39;

                  if (par1World.isAirOrPassableBlock(var36, var16, var38, true))
                  {
                     var41 = par1World.getBlock(var36, var16 + 1, var38);

                     if (var41 == Block.wood)
                     {
                        var18 = par1World.getBlockMetadata(var36, var16 + 1, var38);
                        BlockLog var43 = (BlockLog)var41;

                        if (var43.getAxis(var18).isUpDown())
                        {
                           par1World.setBlock(var36, var16, var38, Block.wood.blockID, var18, 2);
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
