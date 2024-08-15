package net.minecraft.util;

import net.minecraft.world.World;

public final class AxisAlignedBB {
   private static final ThreadLocal theAABBLocalPool = new AABBLocalPool();
   public double minX;
   public double minY;
   public double minZ;
   public double maxX;
   public double maxY;
   public double maxZ;

   public AxisAlignedBB() {
   }

   public static AxisAlignedBB getBoundingBox(double par0, double par2, double par4, double par6, double par8, double par10) {
      return new AxisAlignedBB(par0, par2, par4, par6, par8, par10);
   }

   public static AxisAlignedBB getBoundingBoxFromPool(double min_x, double min_y, double min_z, double max_x, double max_y, double max_z) {
      return getAABBPool().getAABB(min_x, min_y, min_z, max_x, max_y, max_z);
   }

   public static AxisAlignedBB getBoundingBoxFromPool(int x, int y, int z, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return getAABBPool().getAABB((double)x + minX, (double)y + minY, (double)z + minZ, (double)x + maxX, (double)y + maxY, (double)z + maxZ);
   }

   public static AABBPool getAABBPool() {
      return (AABBPool)theAABBLocalPool.get();
   }

   public AxisAlignedBB(double par1, double par3, double par5, double par7, double par9, double par11) {
      this.minX = par1;
      this.minY = par3;
      this.minZ = par5;
      this.maxX = par7;
      this.maxY = par9;
      this.maxZ = par11;
   }

   public AxisAlignedBB(AxisAlignedBB bb) {
      this.minX = bb.minX;
      this.minY = bb.minY;
      this.minZ = bb.minZ;
      this.maxX = bb.maxX;
      this.maxY = bb.maxY;
      this.maxZ = bb.maxZ;
   }

   public AxisAlignedBB setBounds(double par1, double par3, double par5, double par7, double par9, double par11) {
      this.minX = par1;
      this.minY = par3;
      this.minZ = par5;
      this.maxX = par7;
      this.maxY = par9;
      this.maxZ = par11;
      return this;
   }

   public AxisAlignedBB setBounds(int x, int y, int z, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this.minX = (double)x + minX;
      this.minY = (double)y + minY;
      this.minZ = (double)z + minZ;
      this.maxX = (double)x + maxX;
      this.maxY = (double)y + maxY;
      this.maxZ = (double)z + maxZ;
      return this;
   }

   public AxisAlignedBB addCoord(double par1, double par3, double par5) {
      double var7 = this.minX;
      double var9 = this.minY;
      double var11 = this.minZ;
      double var13 = this.maxX;
      double var15 = this.maxY;
      double var17 = this.maxZ;
      if (par1 < 0.0) {
         var7 += par1;
      }

      if (par1 > 0.0) {
         var13 += par1;
      }

      if (par3 < 0.0) {
         var9 += par3;
      }

      if (par3 > 0.0) {
         var15 += par3;
      }

      if (par5 < 0.0) {
         var11 += par5;
      }

      if (par5 > 0.0) {
         var17 += par5;
      }

      return getAABBPool().getAABB(var7, var9, var11, var13, var15, var17);
   }

   public AxisAlignedBB expand(double par1, double par3, double par5) {
      double var7 = this.minX - par1;
      double var9 = this.minY - par3;
      double var11 = this.minZ - par5;
      double var13 = this.maxX + par1;
      double var15 = this.maxY + par3;
      double var17 = this.maxZ + par5;
      return getAABBPool().getAABB(var7, var9, var11, var13, var15, var17);
   }

   public AxisAlignedBB func_111270_a(AxisAlignedBB par1AxisAlignedBB) {
      double var2 = Math.min(this.minX, par1AxisAlignedBB.minX);
      double var4 = Math.min(this.minY, par1AxisAlignedBB.minY);
      double var6 = Math.min(this.minZ, par1AxisAlignedBB.minZ);
      double var8 = Math.max(this.maxX, par1AxisAlignedBB.maxX);
      double var10 = Math.max(this.maxY, par1AxisAlignedBB.maxY);
      double var12 = Math.max(this.maxZ, par1AxisAlignedBB.maxZ);
      return getAABBPool().getAABB(var2, var4, var6, var8, var10, var12);
   }

   public AxisAlignedBB include(AxisAlignedBB bb) {
      if (bb.minX < this.minX) {
         this.minX = bb.minX;
      }

      if (bb.maxX > this.maxX) {
         this.maxX = bb.maxX;
      }

      if (bb.minY < this.minY) {
         this.minY = bb.minY;
      }

      if (bb.maxY > this.maxY) {
         this.maxY = bb.maxY;
      }

      if (bb.minZ < this.minZ) {
         this.minZ = bb.minZ;
      }

      if (bb.maxZ > this.maxZ) {
         this.maxZ = bb.maxZ;
      }

      return this;
   }

   public AxisAlignedBB getOffsetBoundingBox(double par1, double par3, double par5) {
      return getAABBPool().getAABB(this.minX + par1, this.minY + par3, this.minZ + par5, this.maxX + par1, this.maxY + par3, this.maxZ + par5);
   }

   public double calculateXOffset(AxisAlignedBB par1AxisAlignedBB, double par2, double[] limits) {
      if (par1AxisAlignedBB.maxY > this.minY && par1AxisAlignedBB.minY < this.maxY) {
         if (par1AxisAlignedBB.maxZ > this.minZ && par1AxisAlignedBB.minZ < this.maxZ) {
            double var4;
            if (par2 > 0.0 && par1AxisAlignedBB.maxX <= this.minX) {
               if (Double.isNaN(limits[0]) || limits[0] > this.minX) {
                  limits[0] = this.minX;
               }

               var4 = this.minX - par1AxisAlignedBB.maxX;
               if (var4 < par2) {
                  par2 = var4;
               }
            }

            if (par2 < 0.0 && par1AxisAlignedBB.minX >= this.maxX) {
               if (Double.isNaN(limits[1]) || limits[1] < this.maxX) {
                  limits[1] = this.maxX;
               }

               var4 = this.maxX - par1AxisAlignedBB.minX;
               if (var4 > par2) {
                  par2 = var4;
               }
            }

            return par2;
         } else {
            return par2;
         }
      } else {
         return par2;
      }
   }

   public double calculateYOffset(AxisAlignedBB par1AxisAlignedBB, double par2, double[] limits) {
      if (par1AxisAlignedBB.maxX > this.minX && par1AxisAlignedBB.minX < this.maxX) {
         if (par1AxisAlignedBB.maxZ > this.minZ && par1AxisAlignedBB.minZ < this.maxZ) {
            double var4;
            if (par2 > 0.0 && par1AxisAlignedBB.maxY <= this.minY) {
               if (Double.isNaN(limits[0]) || limits[0] > this.minY) {
                  limits[0] = this.minY;
               }

               var4 = this.minY - par1AxisAlignedBB.maxY;
               if (var4 < par2) {
                  par2 = var4;
               }
            }

            if (par2 < 0.0 && par1AxisAlignedBB.minY >= this.maxY) {
               if (Double.isNaN(limits[1]) || limits[1] < this.maxY) {
                  limits[1] = this.maxY;
               }

               var4 = this.maxY - par1AxisAlignedBB.minY;
               if (var4 > par2) {
                  par2 = var4;
               }
            }

            return par2;
         } else {
            return par2;
         }
      } else {
         return par2;
      }
   }

   public double calculateZOffset(AxisAlignedBB par1AxisAlignedBB, double par2, double[] limits) {
      if (par1AxisAlignedBB.maxX > this.minX && par1AxisAlignedBB.minX < this.maxX) {
         if (par1AxisAlignedBB.maxY > this.minY && par1AxisAlignedBB.minY < this.maxY) {
            double var4;
            if (par2 > 0.0 && par1AxisAlignedBB.maxZ <= this.minZ) {
               if (Double.isNaN(limits[0]) || limits[0] > this.minZ) {
                  limits[0] = this.minZ;
               }

               var4 = this.minZ - par1AxisAlignedBB.maxZ;
               if (var4 < par2) {
                  par2 = var4;
               }
            }

            if (par2 < 0.0 && par1AxisAlignedBB.minZ >= this.maxZ) {
               if (Double.isNaN(limits[1]) || limits[1] < this.maxZ) {
                  limits[1] = this.maxZ;
               }

               var4 = this.maxZ - par1AxisAlignedBB.minZ;
               if (var4 > par2) {
                  par2 = var4;
               }
            }

            return par2;
         } else {
            return par2;
         }
      } else {
         return par2;
      }
   }

   public boolean intersectsWith(AxisAlignedBB par1AxisAlignedBB) {
      return par1AxisAlignedBB.maxX > this.minX && par1AxisAlignedBB.minX < this.maxX ? (par1AxisAlignedBB.maxY > this.minY && par1AxisAlignedBB.minY < this.maxY ? par1AxisAlignedBB.maxZ > this.minZ && par1AxisAlignedBB.minZ < this.maxZ : false) : false;
   }

   public AxisAlignedBB offset(double par1, double par3, double par5) {
      this.minX += par1;
      this.minY += par3;
      this.minZ += par5;
      this.maxX += par1;
      this.maxY += par3;
      this.maxZ += par5;
      return this;
   }

   public void offsetX(double dx) {
      this.minX += dx;
      this.maxX += dx;
   }

   public void offsetY(double dy) {
      this.minY += dy;
      this.maxY += dy;
   }

   public void offsetZ(double dz) {
      this.minZ += dz;
      this.maxZ += dz;
   }

   public boolean isVecInside(Vec3 par1Vec3) {
      return par1Vec3.xCoord > this.minX && par1Vec3.xCoord < this.maxX ? (par1Vec3.yCoord > this.minY && par1Vec3.yCoord < this.maxY ? par1Vec3.zCoord > this.minZ && par1Vec3.zCoord < this.maxZ : false) : false;
   }

   public double getAverageEdgeLength() {
      double var1 = this.maxX - this.minX;
      double var3 = this.maxY - this.minY;
      double var5 = this.maxZ - this.minZ;
      return (var1 + var3 + var5) / 3.0;
   }

   public AxisAlignedBB contract(double par1, double par3, double par5) {
      double var7 = this.minX + par1;
      double var9 = this.minY + par3;
      double var11 = this.minZ + par5;
      double var13 = this.maxX - par1;
      double var15 = this.maxY - par3;
      double var17 = this.maxZ - par5;
      return getAABBPool().getAABB(var7, var9, var11, var13, var15, var17);
   }

   public AxisAlignedBB scale(double amount) {
      double center_x = (this.minX + this.maxX) * 0.5;
      double center_y = (this.minY + this.maxY) * 0.5;
      double center_z = (this.minZ + this.maxZ) * 0.5;
      this.minX = center_x + (this.minX - center_x) * amount;
      this.minY = center_y + (this.minY - center_y) * amount;
      this.minZ = center_z + (this.minZ - center_z) * amount;
      this.maxX = center_x + (this.maxX - center_x) * amount;
      this.maxY = center_y + (this.maxY - center_y) * amount;
      this.maxZ = center_z + (this.maxZ - center_z) * amount;
      return this;
   }

   public AxisAlignedBB scaleXZ(double amount) {
      double center_x = (this.minX + this.maxX) * 0.5;
      double center_z = (this.minZ + this.maxZ) * 0.5;
      this.minX = center_x + (this.minX - center_x) * amount;
      this.minZ = center_z + (this.minZ - center_z) * amount;
      this.maxX = center_x + (this.maxX - center_x) * amount;
      this.maxZ = center_z + (this.maxZ - center_z) * amount;
      return this;
   }

   public AxisAlignedBB translate(double x, double y, double z) {
      this.minX += x;
      this.minY += y;
      this.minZ += z;
      this.maxX += x;
      this.maxY += y;
      this.maxZ += z;
      return this;
   }

   public AxisAlignedBB translateCopy(double dx, double dy, double dz) {
      return this.copy().translate(dx, dy, dz);
   }

   public AxisAlignedBB copy() {
      return getAABBPool().getAABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public static AxisAlignedBB[] copyArray(AxisAlignedBB[] array) {
      AxisAlignedBB[] copied_array = new AxisAlignedBB[array.length];

      for(int i = 0; i < array.length; ++i) {
         if (array[i] != null) {
            copied_array[i] = array[i].copy();
         }
      }

      return copied_array;
   }

   public static AxisAlignedBB[] translateCopy(AxisAlignedBB[] array, double dx, double dy, double dz) {
      AxisAlignedBB[] copied_array = new AxisAlignedBB[array.length];

      for(int i = 0; i < array.length; ++i) {
         if (array[i] != null) {
            copied_array[i] = array[i].translateCopy(dx, dy, dz);
         }
      }

      return copied_array;
   }

   public AABBIntercept calculateIntercept(World world, Vec3 par1Vec3, Vec3 par2Vec3) {
      double dx = par1Vec3.xCoord - par2Vec3.xCoord;
      double dy = par1Vec3.yCoord - par2Vec3.yCoord;
      double dz = par1Vec3.zCoord - par2Vec3.zCoord;
      Vec3 var7;
      Vec3 var8;
      double unknown;
      if (dx * dx < 1.0000000116860974E-7) {
         var7 = null;
         var8 = null;
      } else {
         unknown = (this.minX - par2Vec3.xCoord) / dx;
         var7 = unknown >= 0.0 && unknown <= 1.0 ? par2Vec3.myVec3LocalPool.getVecFromPool(par2Vec3.xCoord + dx * unknown, par2Vec3.yCoord + dy * unknown, par2Vec3.zCoord + dz * unknown) : null;
         unknown = (this.maxX - par2Vec3.xCoord) / dx;
         var8 = unknown >= 0.0 && unknown <= 1.0 ? par2Vec3.myVec3LocalPool.getVecFromPool(par2Vec3.xCoord + dx * unknown, par2Vec3.yCoord + dy * unknown, par2Vec3.zCoord + dz * unknown) : null;
      }

      Vec3 var9;
      Vec3 var10;
      if (dy * dy < 1.0000000116860974E-7) {
         var9 = null;
         var10 = null;
      } else {
         unknown = (this.minY - par2Vec3.yCoord) / dy;
         var9 = unknown >= 0.0 && unknown <= 1.0 ? par2Vec3.myVec3LocalPool.getVecFromPool(par2Vec3.xCoord + dx * unknown, par2Vec3.yCoord + dy * unknown, par2Vec3.zCoord + dz * unknown) : null;
         unknown = (this.maxY - par2Vec3.yCoord) / dy;
         var10 = unknown >= 0.0 && unknown <= 1.0 ? par2Vec3.myVec3LocalPool.getVecFromPool(par2Vec3.xCoord + dx * unknown, par2Vec3.yCoord + dy * unknown, par2Vec3.zCoord + dz * unknown) : null;
      }

      Vec3 var11;
      Vec3 var12;
      if (dz * dz < 1.0000000116860974E-7) {
         var11 = null;
         var12 = null;
      } else {
         unknown = (this.minZ - par2Vec3.zCoord) / dz;
         var11 = unknown >= 0.0 && unknown <= 1.0 ? par2Vec3.myVec3LocalPool.getVecFromPool(par2Vec3.xCoord + dx * unknown, par2Vec3.yCoord + dy * unknown, par2Vec3.zCoord + dz * unknown) : null;
         unknown = (this.maxZ - par2Vec3.zCoord) / dz;
         var12 = unknown >= 0.0 && unknown <= 1.0 ? par2Vec3.myVec3LocalPool.getVecFromPool(par2Vec3.xCoord + dx * unknown, par2Vec3.yCoord + dy * unknown, par2Vec3.zCoord + dz * unknown) : null;
      }

      if (var7 != null && (var7.yCoord < this.minY || var7.yCoord > this.maxY || var7.zCoord < this.minZ || var7.zCoord > this.maxZ)) {
         var7 = null;
      }

      if (var8 != null && (var8.yCoord < this.minY || var8.yCoord > this.maxY || var8.zCoord < this.minZ || var8.zCoord > this.maxZ)) {
         var8 = null;
      }

      if (var9 != null && (var9.xCoord < this.minX || var9.xCoord > this.maxX || var9.zCoord < this.minZ || var9.zCoord > this.maxZ)) {
         var9 = null;
      }

      if (var10 != null && (var10.xCoord < this.minX || var10.xCoord > this.maxX || var10.zCoord < this.minZ || var10.zCoord > this.maxZ)) {
         var10 = null;
      }

      if (var11 != null && (var11.xCoord < this.minX || var11.xCoord > this.maxX || var11.yCoord < this.minY || var11.yCoord > this.maxY)) {
         var11 = null;
      }

      if (var12 != null && (var12.xCoord < this.minX || var12.xCoord > this.maxX || var12.yCoord < this.minY || var12.yCoord > this.maxY)) {
         var12 = null;
      }

      Vec3 var13 = null;
      if (var7 != null) {
         var13 = var7;
      }

      double distance_squared_left;
      double distance_squared_right;
      if (var8 != null) {
         if (var13 == null) {
            var13 = var8;
         } else {
            dx = var8.xCoord - par1Vec3.xCoord;
            dy = var8.yCoord - par1Vec3.yCoord;
            dz = var8.zCoord - par1Vec3.zCoord;
            distance_squared_left = dx * dx + dy * dy + dz * dz;
            dx = var13.xCoord - par1Vec3.xCoord;
            dy = var13.yCoord - par1Vec3.yCoord;
            dz = var13.zCoord - par1Vec3.zCoord;
            distance_squared_right = dx * dx + dy * dy + dz * dz;
            if (distance_squared_left < distance_squared_right) {
               var13 = var8;
            }
         }
      }

      if (var9 != null) {
         if (var13 == null) {
            var13 = var9;
         } else {
            dx = var9.xCoord - par1Vec3.xCoord;
            dy = var9.yCoord - par1Vec3.yCoord;
            dz = var9.zCoord - par1Vec3.zCoord;
            distance_squared_left = dx * dx + dy * dy + dz * dz;
            dx = var13.xCoord - par1Vec3.xCoord;
            dy = var13.yCoord - par1Vec3.yCoord;
            dz = var13.zCoord - par1Vec3.zCoord;
            distance_squared_right = dx * dx + dy * dy + dz * dz;
            if (distance_squared_left < distance_squared_right) {
               var13 = var9;
            }
         }
      }

      if (var10 != null) {
         if (var13 == null) {
            var13 = var10;
         } else {
            dx = var10.xCoord - par1Vec3.xCoord;
            dy = var10.yCoord - par1Vec3.yCoord;
            dz = var10.zCoord - par1Vec3.zCoord;
            distance_squared_left = dx * dx + dy * dy + dz * dz;
            dx = var13.xCoord - par1Vec3.xCoord;
            dy = var13.yCoord - par1Vec3.yCoord;
            dz = var13.zCoord - par1Vec3.zCoord;
            distance_squared_right = dx * dx + dy * dy + dz * dz;
            if (distance_squared_left < distance_squared_right) {
               var13 = var10;
            }
         }
      }

      if (var11 != null) {
         if (var13 == null) {
            var13 = var11;
         } else {
            dx = var11.xCoord - par1Vec3.xCoord;
            dy = var11.yCoord - par1Vec3.yCoord;
            dz = var11.zCoord - par1Vec3.zCoord;
            distance_squared_left = dx * dx + dy * dy + dz * dz;
            dx = var13.xCoord - par1Vec3.xCoord;
            dy = var13.yCoord - par1Vec3.yCoord;
            dz = var13.zCoord - par1Vec3.zCoord;
            distance_squared_right = dx * dx + dy * dy + dz * dz;
            if (distance_squared_left < distance_squared_right) {
               var13 = var11;
            }
         }
      }

      if (var12 != null) {
         if (var13 == null) {
            var13 = var12;
         } else {
            dx = var12.xCoord - par1Vec3.xCoord;
            dy = var12.yCoord - par1Vec3.yCoord;
            dz = var12.zCoord - par1Vec3.zCoord;
            distance_squared_left = dx * dx + dy * dy + dz * dz;
            dx = var13.xCoord - par1Vec3.xCoord;
            dy = var13.yCoord - par1Vec3.yCoord;
            dz = var13.zCoord - par1Vec3.zCoord;
            distance_squared_right = dx * dx + dy * dy + dz * dz;
            if (distance_squared_left < distance_squared_right) {
               var13 = var12;
            }
         }
      }

      if (var13 == null) {
         return null;
      } else {
         EnumFace face_hit;
         if (var13 == var12) {
            face_hit = EnumFace.SOUTH;
         } else if (var13 == var11) {
            face_hit = EnumFace.NORTH;
         } else if (var13 == var10) {
            face_hit = EnumFace.TOP;
         } else if (var13 == var9) {
            face_hit = EnumFace.BOTTOM;
         } else if (var13 == var8) {
            face_hit = EnumFace.EAST;
         } else if (var13 == var7) {
            face_hit = EnumFace.WEST;
         } else {
            face_hit = null;
         }

         return new AABBIntercept(var13, face_hit);
      }
   }

   private boolean isVecInYZ(Vec3 par1Vec3) {
      return par1Vec3 == null ? false : par1Vec3.yCoord >= this.minY && par1Vec3.yCoord <= this.maxY && par1Vec3.zCoord >= this.minZ && par1Vec3.zCoord <= this.maxZ;
   }

   private boolean isVecInXZ(Vec3 par1Vec3) {
      return par1Vec3 == null ? false : par1Vec3.xCoord >= this.minX && par1Vec3.xCoord <= this.maxX && par1Vec3.zCoord >= this.minZ && par1Vec3.zCoord <= this.maxZ;
   }

   private boolean isVecInXY(Vec3 par1Vec3) {
      return par1Vec3 == null ? false : par1Vec3.xCoord >= this.minX && par1Vec3.xCoord <= this.maxX && par1Vec3.yCoord >= this.minY && par1Vec3.yCoord <= this.maxY;
   }

   public void setBB(AxisAlignedBB par1AxisAlignedBB) {
      this.minX = par1AxisAlignedBB.minX;
      this.minY = par1AxisAlignedBB.minY;
      this.minZ = par1AxisAlignedBB.minZ;
      this.maxX = par1AxisAlignedBB.maxX;
      this.maxY = par1AxisAlignedBB.maxY;
      this.maxZ = par1AxisAlignedBB.maxZ;
   }

   public boolean isEquivalentTo(AxisAlignedBB bb) {
      return this.minX == bb.minX && this.minY == bb.minY && this.minZ == bb.minZ && this.maxX == bb.maxX && this.maxY == bb.maxY && this.maxZ == bb.maxZ;
   }

   private static final int getBlockCoordForMin(double min) {
      return MathHelper.floor_double(min);
   }

   public final int getBlockCoordForMinX() {
      return getBlockCoordForMin(this.minX);
   }

   public final int getBlockCoordForMinY() {
      return getBlockCoordForMin(this.minY);
   }

   public final int getBlockCoordForMinZ() {
      return getBlockCoordForMin(this.minZ);
   }

   private static final int getBlockCoordForMax(double max) {
      return MathHelper.ceiling_double_int(max) - 1;
   }

   public final int getBlockCoordForMaxX() {
      return getBlockCoordForMax(this.maxX);
   }

   public final int getBlockCoordForMaxY() {
      return getBlockCoordForMax(this.maxY);
   }

   public final int getBlockCoordForMaxZ() {
      return getBlockCoordForMax(this.maxZ);
   }

   public AxisAlignedBB setMinY(double min_y) {
      this.minY = min_y;
      return this;
   }

   public AxisAlignedBB setMaxY(double max_y) {
      this.maxY = max_y;
      return this;
   }

   public AxisAlignedBB addToMaxY(double amount) {
      this.maxY += amount;
      return this;
   }

   public String toString() {
      return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }
}
