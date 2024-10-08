package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityWoodSpider;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public final class PathNavigate {
   private EntityLiving theEntity;
   private World worldObj;
   private PathEntity currentPath;
   private double speed;
   private AttributeInstance pathSearchRange;
   private boolean noSunPathfind;
   private int totalTicks;
   private int ticksAtLastPos;
   private Vec3 lastPosCheck = Vec3.createVectorHelper(0.0, 0.0, 0.0);
   public boolean canPassOpenWoodenDoors = true;
   public boolean canPassClosedWoodenDoors;
   public boolean avoidsWater;
   public boolean canSwim;

   public PathNavigate(EntityLiving par1EntityLiving, World par2World) {
      this.theEntity = par1EntityLiving;
      this.worldObj = par2World;
      this.pathSearchRange = par1EntityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
   }

   public void setAvoidsWater(boolean par1) {
      this.avoidsWater = par1;
   }

   public boolean getAvoidsWater() {
      return this.avoidsWater;
   }

   public void setBreakDoors(boolean par1) {
      this.canPassClosedWoodenDoors = par1;
   }

   public void setEnterDoors(boolean par1) {
      this.canPassOpenWoodenDoors = par1;
   }

   public boolean getCanBreakDoors() {
      return this.canPassClosedWoodenDoors;
   }

   public void setAvoidSun(boolean par1) {
      this.noSunPathfind = par1;
   }

   public void setSpeed(double par1) {
      this.speed = par1;
   }

   public void setCanSwim(boolean par1) {
      this.canSwim = par1;
   }

   private float getPathSearchRange() {
      return Entity.isClass(this.theEntity, EntityWoodSpider.class) && this.theEntity.getBrightness(1.0F) > 0.65F ? (float)this.pathSearchRange.getAttributeValue() * 0.5F : (float)this.pathSearchRange.getAttributeValue();
   }

   public PathEntity getPathToXYZ(double par1, double par3, double par5) {
      return !this.canNavigate() ? null : this.worldObj.getEntityPathToXYZ(this.theEntity, MathHelper.floor_double(par1), (int)par3, MathHelper.floor_double(par5), this.getPathSearchRange(), this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
   }

   public PathEntity getPathToXYZ(int x, int y, int z, int max_range) {
      return !this.canNavigate() ? null : this.worldObj.getEntityPathToXYZ(this.theEntity, x, y, z, (float)max_range, this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
   }

   public boolean tryMoveToXYZ(double par1, double par3, double par5, double par7) {
      PathEntity var9 = this.getPathToXYZ((double)MathHelper.floor_double(par1), (double)((int)par3), (double)MathHelper.floor_double(par5));
      return this.setPath(var9, par7);
   }

   public PathEntity getPathToEntityLiving(Entity par1Entity) {
      return !this.canNavigate() ? null : this.worldObj.getPathEntityToEntity(this.theEntity, par1Entity, this.theEntity.getMaxTargettingRange(), this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
   }

   public PathEntity getPathToEntityLiving(Entity par1Entity, int max_path_length_override) {
      return !this.canNavigate() ? null : this.worldObj.getPathEntityToEntity(this.theEntity, par1Entity, (float)max_path_length_override, this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
   }

   public boolean tryMoveToEntityLiving(Entity par1Entity, double par2) {
      PathEntity var4 = this.getPathToEntityLiving(par1Entity);
      return var4 != null ? this.setPath(var4, par2) : false;
   }

   public boolean setPath(PathEntity par1PathEntity, double par2) {
      if (par1PathEntity == null) {
         this.currentPath = null;
         return false;
      } else {
         if (!par1PathEntity.isSamePath(this.currentPath)) {
            this.currentPath = par1PathEntity;
         }

         if (this.noSunPathfind) {
            this.removeSunnyPath();
         }

         if (this.currentPath.getCurrentPathLength() == 0) {
            return false;
         } else {
            this.speed = par2;
            Vec3 var4 = this.getEntityPosition();
            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck.xCoord = var4.xCoord;
            this.lastPosCheck.yCoord = var4.yCoord;
            this.lastPosCheck.zCoord = var4.zCoord;
            return true;
         }
      }
   }

   public PathEntity getPath() {
      return this.currentPath;
   }

   public void onUpdateNavigation() {
      ++this.totalTicks;
      if (!this.noPath()) {
         if (this.canNavigate()) {
            this.pathFollow();
         }

         if (!this.noPath()) {
            Vec3 var1 = this.currentPath.getPosition(this.theEntity);
            if (var1 != null) {
               this.theEntity.getMoveHelper().setMoveTo(var1.xCoord, var1.yCoord, var1.zCoord, this.speed);
            }
         }
      }

   }

   private void pathFollow() {
      Vec3 var1 = this.getEntityPosition();
      int var2 = this.currentPath.getCurrentPathLength();

      for(int var3 = this.currentPath.getCurrentPathIndex(); var3 < this.currentPath.getCurrentPathLength(); ++var3) {
         if (this.currentPath.getPathPointFromIndex(var3).yCoord != (int)var1.yCoord) {
            var2 = var3;
            break;
         }
      }

      float var8 = this.theEntity.width * this.theEntity.width;

      int var4;
      for(var4 = this.currentPath.getCurrentPathIndex(); var4 < var2; ++var4) {
         if (var1.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, var4)) < (double)var8) {
            this.currentPath.setCurrentPathIndex(var4 + 1);
         }
      }

      var4 = MathHelper.ceiling_float_int(this.theEntity.width);
      int var5 = (int)this.theEntity.height + 1;
      int var6 = var4;

      for(int var7 = var2 - 1; var7 >= this.currentPath.getCurrentPathIndex(); --var7) {
         if (this.isDirectPathBetweenPoints(var1, this.currentPath.getVectorFromIndex(this.theEntity, var7), var4, var5, var6)) {
            this.currentPath.setCurrentPathIndex(var7);
            break;
         }
      }

      if (this.totalTicks - this.ticksAtLastPos > 100) {
         if (var1.squareDistanceTo(this.lastPosCheck) < 2.25) {
            this.clearPathEntity();
         }

         this.ticksAtLastPos = this.totalTicks;
         this.lastPosCheck.xCoord = var1.xCoord;
         this.lastPosCheck.yCoord = var1.yCoord;
         this.lastPosCheck.zCoord = var1.zCoord;
      }

   }

   public boolean noPath() {
      return this.currentPath == null || this.currentPath.isFinished();
   }

   public void clearPathEntity() {
      this.currentPath = null;
   }

   private Vec3 getEntityPosition() {
      return this.worldObj.getWorldVec3Pool().getVecFromPool(this.theEntity.posX, (double)this.getPathableYPos(), this.theEntity.posZ);
   }

   private int getPathableYPos() {
      if (this.theEntity.isInWater() && this.canSwim) {
         int var1 = (int)this.theEntity.boundingBox.minY;
         int var2 = this.worldObj.getBlockId(MathHelper.floor_double(this.theEntity.posX), var1, MathHelper.floor_double(this.theEntity.posZ));
         int var3 = 0;

         do {
            if (var2 != Block.waterMoving.blockID && var2 != Block.waterStill.blockID) {
               return var1;
            }

            ++var1;
            var2 = this.worldObj.getBlockId(MathHelper.floor_double(this.theEntity.posX), var1, MathHelper.floor_double(this.theEntity.posZ));
            ++var3;
         } while(var3 <= 16);

         return (int)this.theEntity.boundingBox.minY;
      } else {
         return (int)(this.theEntity.boundingBox.minY + 0.5);
      }
   }

   private boolean canNavigate() {
      return this.theEntity.onGround || this.canSwim && this.isInFluid();
   }

   private boolean isInFluid() {
      return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
   }

   private void removeSunnyPath() {
      if (!this.theEntity.isWearingHelmet(true)) {
         if (!this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.theEntity.posX), (int)(this.theEntity.boundingBox.minY + 0.5), MathHelper.floor_double(this.theEntity.posZ))) {
            for(int var1 = 0; var1 < this.currentPath.getCurrentPathLength(); ++var1) {
               PathPoint var2 = this.currentPath.getPathPointFromIndex(var1);
               if (!this.worldObj.isInRain(var2.xCoord, var2.yCoord, var2.zCoord) && this.worldObj.canBlockSeeTheSky(var2.xCoord, var2.yCoord, var2.zCoord)) {
                  this.currentPath.setCurrentPathLength(var1 - 1);
                  return;
               }
            }
         }

      }
   }

   private boolean isDirectPathBetweenPoints(Vec3 par1Vec3, Vec3 par2Vec3, int par3, int par4, int par5) {
      int var6 = MathHelper.floor_double(par1Vec3.xCoord);
      int var7 = MathHelper.floor_double(par1Vec3.zCoord);
      double var8 = par2Vec3.xCoord - par1Vec3.xCoord;
      double var10 = par2Vec3.zCoord - par1Vec3.zCoord;
      double var12 = var8 * var8 + var10 * var10;
      if (var12 < 1.0E-8) {
         return false;
      } else {
         double var14 = 1.0 / Math.sqrt(var12);
         var8 *= var14;
         var10 *= var14;
         par3 += 2;
         par5 += 2;
         if (!this.isSafeToStandAt(var6, (int)par1Vec3.yCoord, var7, par3, par4, par5, par1Vec3, var8, var10)) {
            return false;
         } else {
            par3 -= 2;
            par5 -= 2;
            double var16 = 1.0 / Math.abs(var8);
            double var18 = 1.0 / Math.abs(var10);
            double var20 = (double)(var6 * 1) - par1Vec3.xCoord;
            double var22 = (double)(var7 * 1) - par1Vec3.zCoord;
            if (var8 >= 0.0) {
               ++var20;
            }

            if (var10 >= 0.0) {
               ++var22;
            }

            var20 /= var8;
            var22 /= var10;
            int var24 = var8 < 0.0 ? -1 : 1;
            int var25 = var10 < 0.0 ? -1 : 1;
            int var26 = MathHelper.floor_double(par2Vec3.xCoord);
            int var27 = MathHelper.floor_double(par2Vec3.zCoord);
            int var28 = var26 - var6;
            int var29 = var27 - var7;

            do {
               if (var28 * var24 <= 0 && var29 * var25 <= 0) {
                  return true;
               }

               if (var20 < var22) {
                  var20 += var16;
                  var6 += var24;
                  var28 = var26 - var6;
               } else {
                  var22 += var18;
                  var7 += var25;
                  var29 = var27 - var7;
               }
            } while(this.isSafeToStandAt(var6, (int)par1Vec3.yCoord, var7, par3, par4, par5, par1Vec3, var8, var10));

            return false;
         }
      }
   }

   private boolean isSafeToStandAt(int par1, int par2, int par3, int par4, int par5, int par6, Vec3 par7Vec3, double par8, double par10) {
      int var12 = par1 - par4 / 2;
      int var13 = par3 - par6 / 2;
      if (!this.isPositionClear(var12, par2, var13, par4, par5, par6, par7Vec3, par8, par10)) {
         return false;
      } else {
         for(int var14 = var12; var14 < var12 + par4; ++var14) {
            for(int var15 = var13; var15 < var13 + par6; ++var15) {
               double var16 = (double)var14 + 0.5 - par7Vec3.xCoord;
               double var18 = (double)var15 + 0.5 - par7Vec3.zCoord;
               if (var16 * par8 + var18 * par10 >= 0.0) {
                  int var20 = this.worldObj.getBlockId(var14, par2 - 1, var15);
                  if (var20 <= 0) {
                     return false;
                  }

                  Material var21 = Block.blocksList[var20].blockMaterial;
                  if (var21 == Material.water && !this.theEntity.isInWater()) {
                     return false;
                  }

                  if (var21 == Material.lava) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean isPositionClear(int min_x, int min_y, int min_z, int range_x, int range_y, int range_z, Vec3 entity_pos, double dx, double dz) {
      for(int var12 = min_x; var12 < min_x + range_x; ++var12) {
         double var15 = (double)var12 + 0.5 - entity_pos.xCoord;
         double var15_times_par8 = var15 * dx;

         for(int var13 = min_y; var13 < min_y + range_y; ++var13) {
            for(int var14 = min_z; var14 < min_z + range_z; ++var14) {
               double var17 = (double)var14 + 0.5 - entity_pos.zCoord;
               if (var15_times_par8 + var17 * dz >= 0.0) {
                  int var19 = this.worldObj.getBlockId(var12, var13, var14);
                  if (var19 != 0) {
                     Block block = Block.blocksList[var19];
                     if (block.blockMaterial == Material.fire) {
                        if (this.theEntity.isHarmedByFire()) {
                           return false;
                        }
                     } else if (block.blockMaterial == Material.lava && !this.theEntity.isComfortableInLava()) {
                        return false;
                     }

                     if (!block.canBePathedInto(this.worldObj, var12, var13, var14, this.theEntity, false)) {
                        return false;
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
