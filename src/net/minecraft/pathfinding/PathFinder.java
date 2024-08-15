package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockInfo;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFireElemental;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIControlledByPlayer;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PathFinder {
   private IBlockAccess worldMap;
   private Path path = new Path();
   private IntHashMap pointMap = new IntHashMap();
   private PathPoint[] pathOptions = new PathPoint[32];
   private boolean allow_open_wooden_doors;
   private boolean isMovementBlockAllowed;
   private boolean avoid_water;
   private boolean canEntityDrown;

   public PathFinder(IBlockAccess par1IBlockAccess, boolean can_pass_open_wooden_doors, boolean can_path_through_closed_wooden_doors, boolean avoid_water, boolean can_entity_swim) {
      this.worldMap = par1IBlockAccess;
      this.allow_open_wooden_doors = can_pass_open_wooden_doors;
      this.isMovementBlockAllowed = can_path_through_closed_wooden_doors;
      this.avoid_water = avoid_water;
      this.canEntityDrown = can_entity_swim;
   }

   public PathEntity createEntityPathTo(Entity par1Entity, Entity par2Entity, float max_path_length) {
      return this.createEntityPathTo(par1Entity, par2Entity.posX, par2Entity.boundingBox.minY, par2Entity.posZ, max_path_length);
   }

   public PathEntity createEntityPathTo(Entity par1Entity, int par2, int par3, int par4, float par5) {
      return this.createEntityPathTo(par1Entity, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), par5);
   }

   private PathEntity createEntityPathTo(Entity par1Entity, double par2, double par4, double par6, float max_path_length) {
      if (max_path_length > 32.0F) {
         max_path_length = 32.0F;
      }

      this.path.clearPath();
      this.pointMap.clearMap();
      boolean var9 = this.avoid_water;
      int var10 = MathHelper.floor_double(par1Entity.boundingBox.minY + 0.5);
      if (this.canEntityDrown && par1Entity.isInWater()) {
         var10 = (int)par1Entity.boundingBox.minY;

         for(int var11 = this.worldMap.getBlockId(MathHelper.floor_double(par1Entity.posX), var10, MathHelper.floor_double(par1Entity.posZ)); var11 == Block.waterMoving.blockID || var11 == Block.waterStill.blockID; var11 = this.worldMap.getBlockId(MathHelper.floor_double(par1Entity.posX), var10, MathHelper.floor_double(par1Entity.posZ))) {
            ++var10;
         }

         var9 = this.avoid_water;
         this.avoid_water = false;
      } else {
         var10 = MathHelper.floor_double(par1Entity.boundingBox.minY + 0.5);
      }

      PathPoint var15 = this.openPoint(MathHelper.floor_double(par1Entity.boundingBox.minX), var10, MathHelper.floor_double(par1Entity.boundingBox.minZ));
      PathPoint var12 = this.openPoint(MathHelper.floor_double(par2 - (double)(par1Entity.width / 2.0F)), MathHelper.floor_double(par4), MathHelper.floor_double(par6 - (double)(par1Entity.width / 2.0F)));
      PathPoint var13 = new PathPoint(MathHelper.floor_float(par1Entity.width + 1.0F), MathHelper.floor_float(par1Entity.height + 1.0F), MathHelper.floor_float(par1Entity.width + 1.0F));
      PathEntity var14 = this.addToPath(par1Entity, var15, var12, var13, max_path_length);
      this.avoid_water = var9;
      return var14;
   }

   private PathEntity addToPath(Entity par1Entity, PathPoint par2PathPoint, PathPoint par3PathPoint, PathPoint par4PathPoint, float max_path_length) {
      par2PathPoint.totalPathDistance = 0.0F;
      par2PathPoint.distanceToNext = par2PathPoint.func_75832_b(par3PathPoint);
      par2PathPoint.distanceToTarget = par2PathPoint.distanceToNext;
      this.path.clearPath();
      this.path.addPoint(par2PathPoint);
      PathPoint var6 = par2PathPoint;

      while(!this.path.isPathEmpty()) {
         PathPoint var7 = this.path.dequeue();
         if (var7.equals(par3PathPoint)) {
            return this.createEntityPath(par2PathPoint, par3PathPoint);
         }

         if (var7.func_75832_b(par3PathPoint) < var6.func_75832_b(par3PathPoint)) {
            var6 = var7;
         }

         var7.isFirst = true;
         int var8 = this.findPathOptions(par1Entity, var7, par4PathPoint, par3PathPoint, max_path_length);

         for(int var9 = 0; var9 < var8; ++var9) {
            PathPoint var10 = this.pathOptions[var9];
            float var11 = var7.totalPathDistance + var7.func_75832_b(var10);
            if (!var10.isAssigned() || var11 < var10.totalPathDistance) {
               var10.previous = var7;
               var10.totalPathDistance = var11;
               var10.distanceToNext = var10.func_75832_b(par3PathPoint);
               if (var10.isAssigned()) {
                  this.path.changeDistance(var10, var10.totalPathDistance + var10.distanceToNext);
               } else {
                  var10.distanceToTarget = var10.totalPathDistance + var10.distanceToNext;
                  this.path.addPoint(var10);
               }
            }
         }
      }

      if (var6 == par2PathPoint) {
         return null;
      } else {
         return this.createEntityPath(par2PathPoint, var6);
      }
   }

   private int findPathOptions(Entity par1Entity, PathPoint par2PathPoint, PathPoint par3PathPoint, PathPoint par4PathPoint, float max_path_length) {
      int var6 = 0;
      byte var7 = 0;
      if (this.getVerticalOffset(par1Entity, par2PathPoint.xCoord, par2PathPoint.yCoord + 1, par2PathPoint.zCoord, par3PathPoint) == 1) {
         var7 = 1;
      }

      PathPoint var8 = this.getSafePoint(par1Entity, par2PathPoint.xCoord, par2PathPoint.yCoord, par2PathPoint.zCoord + 1, par3PathPoint, var7);
      PathPoint var9 = this.getSafePoint(par1Entity, par2PathPoint.xCoord - 1, par2PathPoint.yCoord, par2PathPoint.zCoord, par3PathPoint, var7);
      PathPoint var10 = this.getSafePoint(par1Entity, par2PathPoint.xCoord + 1, par2PathPoint.yCoord, par2PathPoint.zCoord, par3PathPoint, var7);
      PathPoint var11 = this.getSafePoint(par1Entity, par2PathPoint.xCoord, par2PathPoint.yCoord, par2PathPoint.zCoord - 1, par3PathPoint, var7);
      float par5_sq = max_path_length * max_path_length;
      if (var8 != null && !var8.isFirst && var8.distanceToSq(par4PathPoint) < par5_sq) {
         this.pathOptions[var6++] = var8;
      }

      if (var9 != null && !var9.isFirst && var9.distanceToSq(par4PathPoint) < par5_sq) {
         this.pathOptions[var6++] = var9;
      }

      if (var10 != null && !var10.isFirst && var10.distanceToSq(par4PathPoint) < par5_sq) {
         this.pathOptions[var6++] = var10;
      }

      if (var11 != null && !var11.isFirst && var11.distanceToSq(par4PathPoint) < par5_sq) {
         this.pathOptions[var6++] = var11;
      }

      return var6;
   }

   private PathPoint getSafePoint(Entity par1Entity, int par2, int par3, int par4, PathPoint par5PathPoint, int par6) {
      PathPoint var7 = null;
      int var8 = this.getVerticalOffset(par1Entity, par2, par3, par4, par5PathPoint);
      if (var8 == 2) {
         return this.openPoint(par2, par3, par4);
      } else {
         if (var8 == 1) {
            var7 = this.openPoint(par2, par3, par4);
         }

         if (var7 == null && par6 > 0 && var8 != -3 && var8 != -4 && this.getVerticalOffset(par1Entity, par2, par3 + par6, par4, par5PathPoint) == 1) {
            var7 = this.openPoint(par2, par3 + par6, par4);
            par3 += par6;
         }

         if (var7 != null) {
            int var9 = 0;
            int var10 = 0;

            while(par3 > 0) {
               var10 = this.getVerticalOffset(par1Entity, par2, par3 - 1, par4, par5PathPoint);
               if (this.avoid_water && var10 == -1) {
                  return null;
               }

               if (var10 == 3) {
                  return null;
               }

               if (var10 != 1) {
                  break;
               }

               if (var9++ >= par1Entity.getMaxSafePointTries()) {
                  return null;
               }

               --par3;
               if (par3 > 0) {
                  var7 = this.openPoint(par2, par3, par4);
               }
            }

            if (var10 == -2 && par1Entity.isHarmedByLava()) {
               return null;
            }
         }

         return var7;
      }
   }

   private final PathPoint openPoint(int par1, int par2, int par3) {
      int var4 = PathPoint.makeHash(par1, par2, par3);
      PathPoint var5 = (PathPoint)this.pointMap.lookup(var4);
      if (var5 == null) {
         var5 = new PathPoint(par1, par2, par3);
         this.pointMap.addKey(var4, var5);
      }

      return var5;
   }

   public int getVerticalOffset(Entity par1Entity, int par2, int par3, int par4, PathPoint par5PathPoint) {
      return func_82565_a(par1Entity, par2, par3, par4, par5PathPoint, this.avoid_water, this.isMovementBlockAllowed, this.allow_open_wooden_doors);
   }

   public static boolean isWoodenPortal(int block_id) {
      return block_id == Block.doorWood.blockID || block_id == Block.trapdoor.blockID || block_id == Block.fenceGate.blockID;
   }

   public static boolean isAClosedWoodenPortal(World world, int x, int y, int z) {
      Block block = world.getBlock(x, y, z);
      if (block == Block.doorWood) {
         int metadata = world.getBlockMetadata(x, y, z);
         if (BlockDoor.isTopHalf(metadata)) {
            return isAClosedWoodenPortal(world, x, y - 1, z);
         } else {
            return !BlockDoor.isOpen(metadata);
         }
      } else if (block == Block.trapdoor) {
         return !BlockTrapDoor.isTrapdoorOpen(world.getBlockMetadata(x, y, z));
      } else if (block == Block.fenceGate) {
         return !BlockFenceGate.isFenceGateOpen(world.getBlockMetadata(x, y, z));
      } else {
         return false;
      }
   }

   public static int func_82565_a(Entity par0Entity, int par1, int par2, int par3, PathPoint par4PathPoint, boolean par5, boolean par6, boolean par7) {
      boolean var8 = false;

      for(int var9 = par1; var9 < par1 + par4PathPoint.xCoord; ++var9) {
         for(int var10 = par2; var10 < par2 + par4PathPoint.yCoord; ++var10) {
            for(int var11 = par3; var11 < par3 + par4PathPoint.zCoord; ++var11) {
               int var12 = par0Entity.worldObj.getBlockId(var9, var10, var11);
               if (var12 > 0) {
                  if (var12 == Block.trapdoor.blockID) {
                     var8 = true;
                  } else if (var12 != Block.waterMoving.blockID && var12 != Block.waterStill.blockID) {
                     if (!par7 && isWoodenPortal(var12)) {
                        return 0;
                     }
                  } else {
                     if (par5) {
                        return -1;
                     }

                     var8 = true;
                  }

                  Block var13 = Block.blocksList[var12];
                  int var14 = var13.getRenderType();
                  if (var13 == Block.cactus && par0Entity.isEntityLiving()) {
                     EntityLiving entity_living = par0Entity.getAsEntityLiving();
                     if (entity_living.canBeDamagedByCacti() && (entity_living.tasks == null || !entity_living.tasks.isTaskExecuting(EntityAIControlledByPlayer.class))) {
                        BlockInfo info = entity_living.getBlockRestingOn3();
                        if (info == null || info.block != Block.cactus) {
                           return 3;
                        }
                     }
                  }

                  if (par0Entity.worldObj.blockGetRenderType(var9, var10, var11) == 9) {
                     int var18 = MathHelper.floor_double(par0Entity.posX);
                     int var16 = MathHelper.floor_double(par0Entity.posY);
                     int var17 = MathHelper.floor_double(par0Entity.posZ);
                     if (par0Entity.worldObj.blockGetRenderType(var18, var16, var17) != 9 && par0Entity.worldObj.blockGetRenderType(var18, var16 - 1, var17) != 9) {
                        return -3;
                     }
                  } else {
                     boolean can_block_be_pathed_into = var13.canBePathedInto(par0Entity.worldObj, var9, var10, var11, par0Entity, par6);
                     if (var13.blockMaterial == Material.lava || !can_block_be_pathed_into) {
                        if (var14 == 11 || var12 == Block.fenceGate.blockID || var14 == 32) {
                           return -3;
                        }

                        if (var12 == Block.trapdoor.blockID) {
                           return -4;
                        }

                        Material var15 = var13.blockMaterial;
                        if (var15 != Material.lava || par0Entity instanceof EntityFireElemental || par0Entity.handleLavaMovement()) {
                           return 0;
                        }

                        if (!par0Entity.handleLavaMovement()) {
                           return -2;
                        }
                     }
                  }
               }
            }
         }
      }

      return var8 ? 2 : 1;
   }

   private PathEntity createEntityPath(PathPoint par1PathPoint, PathPoint par2PathPoint) {
      int var3 = 1;

      PathPoint var4;
      for(var4 = par2PathPoint; var4.previous != null; var4 = var4.previous) {
         ++var3;
      }

      PathPoint[] var5 = new PathPoint[var3];
      var4 = par2PathPoint;
      --var3;

      for(var5[var3] = par2PathPoint; var4.previous != null; var5[var3] = var4) {
         var4 = var4.previous;
         --var3;
      }

      return new PathEntity(var5);
   }
}
