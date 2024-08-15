package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityFireElemental;
import net.minecraft.world.World;

public final class BlockStationary extends BlockFluid {
   protected BlockStationary(int par1, Material par2Material) {
      super(par1, par2Material);
      this.setTickRandomly(false);
      if (par2Material == Material.lava) {
         this.setTickRandomly(true);
      }

   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (super.onNeighborBlockChange(world, x, y, z, neighbor_block_id)) {
         return true;
      } else if (world.getBlockId(x, y, z) == this.blockID) {
         this.setNotStationary(world, x, y, z);
         return true;
      } else {
         return false;
      }
   }

   private void setNotStationary(World par1World, int par2, int par3, int par4) {
      int var5 = par1World.getBlockMetadata(par2, par3, par4);
      par1World.setBlock(par2, par3, par4, this.blockID - 1, var5, 2);
      par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID - 1, this.tickRate(par1World));
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (this.blockMaterial == Material.lava) {
         int rarity_of_spawning_fire_elemental = par1World.hasSkylight() ? 16 + par3 : 16;
         if (par1World.rand.nextInt(rarity_of_spawning_fire_elemental) == 0 && par1World.getEntitiesWithinAABB(EntityFireElemental.class, par1World.getBoundingBoxFromPool(par2, par3, par4).expand(16.0, 16.0, 16.0)).size() < 2) {
            boolean spawn_fire_elemental = false;
            if (par1World.isTheNether() && par1World.getBlockMaterial(par2, par3 - 1, par4) != Material.lava && par1World.getBlockMaterial(par2, par3, par4) == Material.lava && par1World.isAirBlock(par2, par3 + 1, par4) && par1World.rand.nextInt(4) == 0) {
               if (!BlockFluid.isFullLavaBlock(par1World, par2, par3, par4, false)) {
                  spawn_fire_elemental = par1World.isPlayerNearby((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), 64.0);
               } else if (par1World.rand.nextInt(4) == 0) {
                  spawn_fire_elemental = par1World.isPlayerNearby((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), 16.0);
               }
            }

            if (!spawn_fire_elemental && par1World.rand.nextInt(16) == 0) {
               spawn_fire_elemental = BlockFluid.isFullLavaBlock(par1World, par2, par3 + 1, par4, true) && par1World.isAirBlock(par2, par3 + 2, par4) && par1World.isAirBlock(par2, par3 + 3, par4) && par1World.isPlayerNearby((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), 16.0);
            }

            if (spawn_fire_elemental) {
               EntityFireElemental fire_elemental = new EntityFireElemental(par1World);
               fire_elemental.setLocationAndAngles((double)((float)par2 + 0.5F), (double)((float)par3 + 0.1F), (double)((float)par4 + 0.5F), 0.0F, 0.0F);
               par1World.spawnEntityInWorld(fire_elemental);
            }
         }

         int var6 = par5Random.nextInt(3);
         int var7 = 0;

         while(true) {
            int var8;
            if (var7 >= var6) {
               if (var6 == 0) {
                  var7 = par2;
                  var8 = par4;

                  for(int var9 = 0; var9 < 3; ++var9) {
                     par2 = var7 + par5Random.nextInt(3) - 1;
                     par4 = var8 + par5Random.nextInt(3) - 1;
                     if (par1World.isAirBlock(par2, par3 + 1, par4) && isFlammable(par1World, par2, par3, par4)) {
                        par1World.setBlock(par2, par3 + 1, par4, Block.fire.blockID);
                     }
                  }
               }
               break;
            }

            par2 += par5Random.nextInt(3) - 1;
            ++par3;
            par4 += par5Random.nextInt(3) - 1;
            var8 = par1World.getBlockId(par2, par3, par4);
            if (var8 == 0) {
               if (isFlammable(par1World, par2 - 1, par3, par4) || isFlammable(par1World, par2 + 1, par3, par4) || isFlammable(par1World, par2, par3, par4 - 1) || isFlammable(par1World, par2, par3, par4 + 1) || isFlammable(par1World, par2, par3 - 1, par4) || isFlammable(par1World, par2, par3 + 1, par4)) {
                  par1World.setBlock(par2, par3, par4, Block.fire.blockID);
                  return false;
               }
            } else if (getBlock(var8).isSolid(par1World, par2, par3, par4)) {
               return false;
            }

            ++var7;
         }
      }

      return false;
   }

   public static boolean isFlammable(World par1World, int par2, int par3, int par4) {
      Block block = par1World.getBlock(par2, par3, par4);
      return block != null && block.blockMaterial != Material.netherrack ? block.blockMaterial.canCatchFire() : false;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return "still";
   }
}
