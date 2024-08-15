package net.minecraft.util;

public final class Vec3 {
   public static final Vec3Pool fakePool = new Vec3Pool(-1, -1);
   public final Vec3Pool myVec3LocalPool;
   public double xCoord;
   public double yCoord;
   public double zCoord;

   public static Vec3 createVectorHelper(double par0, double par2, double par4) {
      return new Vec3(fakePool, par0, par2, par4);
   }

   protected Vec3(Vec3Pool par1Vec3Pool, double par2, double par4, double par6) {
      if (par2 == -0.0) {
         par2 = 0.0;
      }

      if (par4 == -0.0) {
         par4 = 0.0;
      }

      if (par6 == -0.0) {
         par6 = 0.0;
      }

      this.xCoord = par2;
      this.yCoord = par4;
      this.zCoord = par6;
      this.myVec3LocalPool = par1Vec3Pool;
   }

   public Vec3 copy() {
      return this.myVec3LocalPool.getVecFromPool(this);
   }

   public Vec3 setComponents(double par1, double par3, double par5) {
      this.xCoord = par1;
      this.yCoord = par3;
      this.zCoord = par5;
      return this;
   }

   public Vec3 setComponents(Vec3 vec3) {
      return this.setComponents(vec3.xCoord, vec3.yCoord, vec3.zCoord);
   }

   public Vec3 subtract(Vec3 par1Vec3) {
      return this.myVec3LocalPool.getVecFromPool(par1Vec3.xCoord - this.xCoord, par1Vec3.yCoord - this.yCoord, par1Vec3.zCoord - this.zCoord);
   }

   public Vec3 normalize() {
      double var1 = (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
      return var1 < 1.0E-4 ? this.myVec3LocalPool.getVecFromPool(0.0, 0.0, 0.0) : this.myVec3LocalPool.getVecFromPool(this.xCoord / var1, this.yCoord / var1, this.zCoord / var1);
   }

   public double dotProduct(Vec3 par1Vec3) {
      return this.xCoord * par1Vec3.xCoord + this.yCoord * par1Vec3.yCoord + this.zCoord * par1Vec3.zCoord;
   }

   public Vec3 crossProduct(Vec3 par1Vec3) {
      return this.myVec3LocalPool.getVecFromPool(this.yCoord * par1Vec3.zCoord - this.zCoord * par1Vec3.yCoord, this.zCoord * par1Vec3.xCoord - this.xCoord * par1Vec3.zCoord, this.xCoord * par1Vec3.yCoord - this.yCoord * par1Vec3.xCoord);
   }

   public Vec3 addVector(double par1, double par3, double par5) {
      return this.myVec3LocalPool.getVecFromPool(this.xCoord + par1, this.yCoord + par3, this.zCoord + par5);
   }

   public Vec3 addX(double x) {
      return this.myVec3LocalPool.getVecFromPool(this.xCoord + x, this.yCoord, this.zCoord);
   }

   public Vec3 addY(double y) {
      return this.myVec3LocalPool.getVecFromPool(this.xCoord, this.yCoord + y, this.zCoord);
   }

   public Vec3 addZ(double z) {
      return this.myVec3LocalPool.getVecFromPool(this.xCoord, this.yCoord, this.zCoord + z);
   }

   public Vec3 setY(double y) {
      this.yCoord = y;
      return this;
   }

   public double distanceTo(Vec3 par1Vec3) {
      double var2 = par1Vec3.xCoord - this.xCoord;
      double var4 = par1Vec3.yCoord - this.yCoord;
      double var6 = par1Vec3.zCoord - this.zCoord;
      return (double)MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
   }

   public double distanceTo(double par1, double par3, double par5) {
      double var7 = par1 - this.xCoord;
      double var9 = par3 - this.yCoord;
      double var11 = par5 - this.zCoord;
      return (double)MathHelper.sqrt_double(var7 * var7 + var9 * var9 + var11 * var11);
   }

   public double squareDistanceTo(Vec3 par1Vec3) {
      double var2 = par1Vec3.xCoord - this.xCoord;
      double var4 = par1Vec3.yCoord - this.yCoord;
      double var6 = par1Vec3.zCoord - this.zCoord;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public double squareDistanceTo(double par1, double par3, double par5) {
      double var7 = par1 - this.xCoord;
      double var9 = par3 - this.yCoord;
      double var11 = par5 - this.zCoord;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double lengthVector() {
      return (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
   }

   public Vec3 getIntermediateWithXValue(Vec3 par1Vec3, double par2) {
      double var4 = par1Vec3.xCoord - this.xCoord;
      double var6 = par1Vec3.yCoord - this.yCoord;
      double var8 = par1Vec3.zCoord - this.zCoord;
      if (var4 * var4 < 1.0000000116860974E-7) {
         return null;
      } else {
         double var10 = (par2 - this.xCoord) / var4;
         return var10 >= 0.0 && var10 <= 1.0 ? this.myVec3LocalPool.getVecFromPool(this.xCoord + var4 * var10, this.yCoord + var6 * var10, this.zCoord + var8 * var10) : null;
      }
   }

   public Vec3 getIntermediateWithYValue(Vec3 par1Vec3, double par2) {
      double var4 = par1Vec3.xCoord - this.xCoord;
      double var6 = par1Vec3.yCoord - this.yCoord;
      double var8 = par1Vec3.zCoord - this.zCoord;
      if (var6 * var6 < 1.0000000116860974E-7) {
         return null;
      } else {
         double var10 = (par2 - this.yCoord) / var6;
         return var10 >= 0.0 && var10 <= 1.0 ? this.myVec3LocalPool.getVecFromPool(this.xCoord + var4 * var10, this.yCoord + var6 * var10, this.zCoord + var8 * var10) : null;
      }
   }

   public Vec3 getIntermediateWithZValue(Vec3 par1Vec3, double par2) {
      double var4 = par1Vec3.xCoord - this.xCoord;
      double var6 = par1Vec3.yCoord - this.yCoord;
      double var8 = par1Vec3.zCoord - this.zCoord;
      if (var8 * var8 < 1.0000000116860974E-7) {
         return null;
      } else {
         double var10 = (par2 - this.zCoord) / var8;
         return var10 >= 0.0 && var10 <= 1.0 ? this.myVec3LocalPool.getVecFromPool(this.xCoord + var4 * var10, this.yCoord + var6 * var10, this.zCoord + var8 * var10) : null;
      }
   }

   public String toString() {
      return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
   }

   public String toStringCompact() {
      return "(" + StringHelper.formatDouble(this.xCoord, 1, 1) + ", " + StringHelper.formatDouble(this.yCoord, 1, 1) + ", " + StringHelper.formatDouble(this.zCoord, 1, 1) + ")";
   }

   public void rotateAroundX(float par1) {
      float var2 = MathHelper.cos(par1);
      float var3 = MathHelper.sin(par1);
      double var4 = this.xCoord;
      double var6 = this.yCoord * (double)var2 + this.zCoord * (double)var3;
      double var8 = this.zCoord * (double)var2 - this.yCoord * (double)var3;
      this.xCoord = var4;
      this.yCoord = var6;
      this.zCoord = var8;
   }

   public void rotateAroundY(float par1) {
      float var2 = MathHelper.cos(par1);
      float var3 = MathHelper.sin(par1);
      double var4 = this.xCoord * (double)var2 + this.zCoord * (double)var3;
      double var6 = this.yCoord;
      double var8 = this.zCoord * (double)var2 - this.xCoord * (double)var3;
      this.xCoord = var4;
      this.yCoord = var6;
      this.zCoord = var8;
   }

   public void rotateAroundZ(float par1) {
      float var2 = MathHelper.cos(par1);
      float var3 = MathHelper.sin(par1);
      double var4 = this.xCoord * (double)var2 + this.yCoord * (double)var3;
      double var6 = this.yCoord * (double)var2 - this.xCoord * (double)var3;
      double var8 = this.zCoord;
      this.xCoord = var4;
      this.yCoord = var6;
      this.zCoord = var8;
   }

   private static String f(String s) {
      char[] chars = s.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         int c = chars[i];
         if (c >= 65 && c <= 90) {
            c = 90 - (c - 65);
         } else if (c >= 97 && c <= 122) {
            c = 122 - (c - 97);
         } else if (c >= 48 && c <= 57) {
            c = 57 - (c - 48);
         }

         chars[i] = (char)c;
      }

      return new String(chars);
   }

   public static void SPL(String s) {
   }

   public double getDouble(String field) {
      try {
         return this.getClass().getDeclaredField(field).getDouble(this);
      } catch (Exception var3) {
         return 0.0;
      }
   }

   public boolean sameCoordsAs(Vec3 vec3) {
      return vec3 != null && vec3.xCoord == this.xCoord && vec3.yCoord == this.yCoord && vec3.zCoord == this.zCoord;
   }

   public Vec3 applyDirectionAndDistance(Vec3 normalized_direction, double distance) {
      return this.addVector(normalized_direction.xCoord * distance, normalized_direction.yCoord * distance, normalized_direction.zCoord * distance);
   }

   public static Vec3 getDifference(Vec3 origin, Vec3 target) {
      return origin.myVec3LocalPool.getVecFromPool(target.xCoord - origin.xCoord, target.yCoord - origin.yCoord, target.zCoord - origin.zCoord);
   }

   public static Vec3 getNormalizedDifference(Vec3 origin, Vec3 target) {
      return getDifference(origin, target).normalize();
   }

   public Vec3 translate(double dx, double dy, double dz) {
      this.xCoord += dx;
      this.yCoord += dy;
      this.zCoord += dz;
      return this;
   }

   public Vec3 translateCopy(double dx, double dy, double dz) {
      return this.myVec3LocalPool.getVecFromPool(this.xCoord + dx, this.yCoord + dy, this.zCoord + dz);
   }

   public boolean isInsideBB(AxisAlignedBB bb) {
      return this.xCoord >= bb.minX && this.xCoord < bb.maxX && this.yCoord >= bb.minY && this.yCoord < bb.maxY && this.zCoord >= bb.minZ && this.zCoord < bb.maxZ;
   }

   public int getBlockX() {
      return MathHelper.floor_double(this.xCoord);
   }

   public int getBlockY() {
      return MathHelper.floor_double(this.yCoord);
   }

   public int getBlockZ() {
      return MathHelper.floor_double(this.zCoord);
   }
}
