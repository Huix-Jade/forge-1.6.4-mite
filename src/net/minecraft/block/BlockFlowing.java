package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.world.World;

public final class BlockFlowing extends BlockFluid {
   int numAdjacentSources;
   boolean[] isOptimalFlowDirection = new boolean[4];
   int[] flowCost = new int[4];

   protected BlockFlowing(int par1, Material par2Material) {
      super(par1, par2Material);
   }

   private void updateFlow(World par1World, int par2, int par3, int par4) {
      int var5 = par1World.getBlockMetadata(par2, par3, par4);
      par1World.setBlock(par2, par3, par4, this.blockID + 1, var5, 2);
   }

   private boolean hasStationaryPeerNeighbor(World world, int x, int y, int z) {
      if (world.getBlockId(x - 1, y, z) == this.blockID + 1) {
         return true;
      } else if (world.getBlockId(x + 1, y, z) == this.blockID + 1) {
         return true;
      } else if (world.getBlockId(x, y, z - 1) == this.blockID + 1) {
         return true;
      } else {
         return world.getBlockId(x, y, z + 1) == this.blockID + 1;
      }
   }

   private boolean handleFreefall(World world, int x, int y, int z) {
      if (world.getDistanceSqToNearestPlayer(x, z) < 256.0 && world.isAirBlock(x, y - 1, z) && world.getBlockMaterial(x, y + 1, z) != this.blockMaterial && !this.hasStationaryPeerNeighbor(world, x, y, z)) {
         world.setBlock(x, y, z, 0, 0, 2);
         --y;
         world.setBlock(x, y, z, this.blockID);
         if (this.blockMaterial == Material.water) {
            world.scheduleBlockChange(x, y, z, this.blockID + 1, Block.waterMoving.blockID, 1, 8);
         } else if (this.blockMaterial == Material.lava) {
            world.scheduleBlockChange(x, y, z, this.blockID, Block.lavaMoving.blockID, 1, world.doesLavaFlowQuicklyInThisWorld() ? 16 : 48);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (this.handleFreefall(par1World, par2, par3, par4)) {
         return true;
      } else {
         boolean changed_state = false;
         int var6 = this.getFlowDecay(par1World, par2, par3, par4);
         byte var7 = 1;
         if (this.blockMaterial == Material.lava && !par1World.provider.isHellWorld) {
            var7 = 2;
         }

         boolean var8 = true;
         int var9 = this.tickRate(par1World);
         int var11;
         if (var6 > 0) {
            byte var10 = -100;
            this.numAdjacentSources = 0;
            int var13 = this.getSmallestFlowDecay(par1World, par2 - 1, par3, par4, var10);
            var13 = this.getSmallestFlowDecay(par1World, par2 + 1, par3, par4, var13);
            var13 = this.getSmallestFlowDecay(par1World, par2, par3, par4 - 1, var13);
            var13 = this.getSmallestFlowDecay(par1World, par2, par3, par4 + 1, var13);
            var11 = var13 + var7;
            if (var11 >= 8 || var13 < 0) {
               var11 = -1;
            }

            if (this.getFlowDecay(par1World, par2, par3 + 1, par4) >= 0) {
               int var12 = this.getFlowDecay(par1World, par2, par3 + 1, par4);
               if (var12 >= 8) {
                  var11 = var12;
               } else {
                  var11 = var12 + 8;
               }
            }

            if (this.numAdjacentSources >= 2 && this.blockMaterial == Material.water) {
               if (par1World.getBlockMaterial(par2, par3 - 1, par4).isSolid()) {
                  var11 = 0;
               } else if (par1World.getBlockMaterial(par2, par3 - 1, par4) == this.blockMaterial && par1World.getBlockMetadata(par2, par3 - 1, par4) == 0) {
                  var11 = 0;
               }
            }

            if (this.blockMaterial == Material.lava && var6 < 8 && var11 < 8 && var11 > var6 && par5Random.nextInt(4) != 0) {
               var9 *= 4;
            }

            if (var11 == var6) {
               if (var8) {
                  this.updateFlow(par1World, par2, par3, par4);
                  changed_state = true;
               }
            } else {
               var6 = var11;
               if (var11 < 0) {
                  par1World.setBlockToAir(par2, par3, par4);
               } else {
                  par1World.setBlockMetadataWithNotify(par2, par3, par4, var11, 2);
                  par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, var9);
                  par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
               }

               changed_state = true;
            }
         } else {
            this.updateFlow(par1World, par2, par3, par4);
            changed_state = true;
         }

         if (this.liquidCanDisplaceBlock(par1World, par2, par3 - 1, par4)) {
            if (this.blockMaterial == Material.lava && par1World.getBlockMaterial(par2, par3 - 1, par4) == Material.water) {
               if (!par1World.isRemote) {
                  par1World.tryConvertWaterToCobblestone(par2, par3 - 1, par4);
               }

               return changed_state;
            }

            if (var6 >= 8) {
               this.flowIntoBlock(par1World, par2, par3 - 1, par4, var6);
            } else {
               this.flowIntoBlock(par1World, par2, par3 - 1, par4, var6 + 8);
            }
         } else if (var6 >= 0 && (var6 == 0 || par1World.doesBlockBlockFluids(par2, par3 - 1, par4))) {
            boolean[] var14 = this.getOptimalFlowDirections(par1World, par2, par3, par4);
            var11 = var6 + var7;
            if (var6 >= 8) {
               var11 = 1;
            }

            if (var11 >= 8) {
               return changed_state;
            }

            if (var14[0]) {
               this.flowIntoBlock(par1World, par2 - 1, par3, par4, var11);
            }

            if (var14[1]) {
               this.flowIntoBlock(par1World, par2 + 1, par3, par4, var11);
            }

            if (var14[2]) {
               this.flowIntoBlock(par1World, par2, par3, par4 - 1, var11);
            }

            if (var14[3]) {
               this.flowIntoBlock(par1World, par2, par3, par4 + 1, var11);
            }
         }

         return changed_state;
      }
   }

   private void flowIntoBlock(World par1World, int par2, int par3, int par4, int par5) {
      if (this.liquidCanDisplaceBlock(par1World, par2, par3, par4)) {
         int var6 = par1World.getBlockId(par2, par3, par4);
         if (var6 > 0) {
            if (this.blockMaterial == Material.lava) {
               par1World.blockFX(EnumBlockFX.lava_mixing_with_water, par2, par3, par4);
            } else {
               Block block = Block.blocksList[var6];
               block.dropBlockAsEntityItem((new BlockBreakInfo(par1World, par2, par3, par4)).setFlooded(this));
            }
         }

         if (this.blockMaterial == Material.water && par1World.getBlock(par2, par3 - 1, par4) == mantleOrCore) {
            par1World.blockFX(EnumBlockFX.steam, par2, par3, par4);
            return;
         }

         par1World.setBlock(par2, par3, par4, this.blockID, par5, 3);
      }

   }

   private int calculateFlowCost(World par1World, int par2, int par3, int par4, int par5, int par6) {
      int var7 = 1000;

      for(int var8 = 0; var8 < 4; ++var8) {
         if ((var8 != 0 || par6 != 1) && (var8 != 1 || par6 != 0) && (var8 != 2 || par6 != 3) && (var8 != 3 || par6 != 2)) {
            int var9 = par2;
            int var11 = par4;
            if (var8 == 0) {
               var9 = par2 - 1;
            }

            if (var8 == 1) {
               ++var9;
            }

            if (var8 == 2) {
               var11 = par4 - 1;
            }

            if (var8 == 3) {
               ++var11;
            }

            if (!par1World.doesBlockBlockFluids(var9, par3, var11) && (par1World.getBlockMaterial(var9, par3, var11) != this.blockMaterial || par1World.getBlockMetadata(var9, par3, var11) != 0)) {
               if (!par1World.doesBlockBlockFluids(var9, par3 - 1, var11)) {
                  return par5;
               }

               if (par5 < 4) {
                  int var12 = this.calculateFlowCost(par1World, var9, par3, var11, par5 + 1, var8);
                  if (var12 < var7) {
                     var7 = var12;
                  }
               }
            }
         }
      }

      return var7;
   }

   private boolean[] getOptimalFlowDirections(World par1World, int par2, int par3, int par4) {
      int var5;
      int var6;
      for(var5 = 0; var5 < 4; ++var5) {
         this.flowCost[var5] = 1000;
         var6 = par2;
         int var8 = par4;
         if (var5 == 0) {
            var6 = par2 - 1;
         }

         if (var5 == 1) {
            ++var6;
         }

         if (var5 == 2) {
            var8 = par4 - 1;
         }

         if (var5 == 3) {
            ++var8;
         }

         if (!par1World.doesBlockBlockFluids(var6, par3, var8) && (par1World.getBlockMaterial(var6, par3, var8) != this.blockMaterial || par1World.getBlockMetadata(var6, par3, var8) != 0)) {
            if (par1World.doesBlockBlockFluids(var6, par3 - 1, var8)) {
               this.flowCost[var5] = this.calculateFlowCost(par1World, var6, par3, var8, 1, var5);
            } else {
               this.flowCost[var5] = 0;
            }
         }
      }

      var5 = this.flowCost[0];

      for(var6 = 1; var6 < 4; ++var6) {
         if (this.flowCost[var6] < var5) {
            var5 = this.flowCost[var6];
         }
      }

      for(var6 = 0; var6 < 4; ++var6) {
         this.isOptimalFlowDirection[var6] = this.flowCost[var6] == var5;
      }

      return this.isOptimalFlowDirection;
   }

   protected int getSmallestFlowDecay(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = this.getFlowDecay(par1World, par2, par3, par4);
      if (var6 < 0) {
         return par5;
      } else {
         if (var6 == 0) {
            ++this.numAdjacentSources;
         }

         if (var6 >= 8) {
            var6 = 0;
         }

         return par5 >= 0 && var6 >= par5 ? par5 : var6;
      }
   }

   private boolean liquidCanDisplaceBlock(World par1World, int par2, int par3, int par4) {
      Material var5 = par1World.getBlockMaterial(par2, par3, par4);
      return var5 == this.blockMaterial ? false : (var5 == Material.lava ? false : !par1World.doesBlockBlockFluids(par2, par3, par4));
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      super.onBlockAdded(par1World, par2, par3, par4);
      if (par1World.getBlockId(par2, par3, par4) == this.blockID) {
         par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
      }

   }

   public boolean func_82506_l() {
      return true;
   }
}
