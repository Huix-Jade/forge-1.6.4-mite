package net.minecraft.util;

import java.util.Random;

public final class MathHelper {
   private static float[] SIN_TABLE = new float[65536];

   public static final float sin(float par0) {
      return SIN_TABLE[(int)(par0 * 10430.378F) & '\uffff'];
   }

   public static final float cos(float par0) {
      return SIN_TABLE[(int)(par0 * 10430.378F + 16384.0F) & '\uffff'];
   }

   public static final float sqrt_float(float par0) {
      return (float)Math.sqrt((double)par0);
   }

   public static final float sqrt_double(double par0) {
      return (float)Math.sqrt(par0);
   }

   public static int floor_float(float par0) {
      int var1 = (int)par0;
      return par0 < (float)var1 ? var1 - 1 : var1;
   }

   public static int truncateDoubleToInt(double par0) {
      return (int)(par0 + 1024.0) - 1024;
   }

   public static int floor_double(double par0) {
      int var2 = (int)par0;
      return par0 < (double)var2 ? var2 - 1 : var2;
   }

   public static long floor_double_long(double par0) {
      long var2 = (long)par0;
      return par0 < (double)var2 ? var2 - 1L : var2;
   }

   public static float abs(float par0) {
      return par0 >= 0.0F ? par0 : -par0;
   }

   public static int abs_int(int par0) {
      return par0 >= 0 ? par0 : -par0;
   }

   public static int ceiling_float_int(float par0) {
      int var1 = (int)par0;
      return par0 > (float)var1 ? var1 + 1 : var1;
   }

   public static int ceiling_double_int(double par0) {
      int var2 = (int)par0;
      return par0 > (double)var2 ? var2 + 1 : var2;
   }

   public static int clamp_int(int par0, int par1, int par2) {
      return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
   }

   public static float clamp_float(float par0, float par1, float par2) {
      return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
   }

   public static double clamp_double(double value, double min, double max) {
      return value < min ? min : (value > max ? max : value);
   }

   public static double abs_max(double par0, double par2) {
      if (par0 < 0.0) {
         par0 = -par0;
      }

      if (par2 < 0.0) {
         par2 = -par2;
      }

      return par0 > par2 ? par0 : par2;
   }

   public static int bucketInt(int par0, int par1) {
      return par0 < 0 ? -((-par0 - 1) / par1) - 1 : par0 / par1;
   }

   public static boolean stringNullOrLengthZero(String par0Str) {
      return par0Str == null || par0Str.length() == 0;
   }

   public static int getRandomIntegerInRange(Random par0Random, int par1, int par2) {
      return par1 >= par2 ? par1 : par0Random.nextInt(par2 - par1 + 1) + par1;
   }

   public static double getRandomDoubleInRange(Random par0Random, double par1, double par3) {
      return par1 >= par3 ? par1 : par0Random.nextDouble() * (par3 - par1) + par1;
   }

   public static double average(long[] par0ArrayOfLong) {
      long var1 = 0L;
      long[] var3 = par0ArrayOfLong;
      int var4 = par0ArrayOfLong.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         long var6 = var3[var5];
         var1 += var6;
      }

      return (double)var1 / (double)par0ArrayOfLong.length;
   }

   public static float wrapAngleTo180_float(float par0) {
      par0 %= 360.0F;
      if (par0 >= 180.0F) {
         par0 -= 360.0F;
      }

      if (par0 < -180.0F) {
         par0 += 360.0F;
      }

      return par0;
   }

   public static double wrapAngleTo180_double(double par0) {
      par0 %= 360.0;
      if (par0 >= 180.0) {
         par0 -= 360.0;
      }

      if (par0 < -180.0) {
         par0 += 360.0;
      }

      return par0;
   }

   public static int parseIntWithDefault(String par0Str, int par1) {
      int var2 = par1;

      try {
         var2 = Integer.parseInt(par0Str);
      } catch (Throwable var4) {
      }

      return var2;
   }

   public static int parseIntWithDefaultAndMax(String par0Str, int par1, int par2) {
      int var3 = par1;

      try {
         var3 = Integer.parseInt(par0Str);
      } catch (Throwable var5) {
      }

      if (var3 < par2) {
         var3 = par2;
      }

      return var3;
   }

   public static double parseDoubleWithDefault(String par0Str, double par1) {
      double var3 = par1;

      try {
         var3 = Double.parseDouble(par0Str);
      } catch (Throwable var6) {
      }

      return var3;
   }

   public static double func_82713_a(String par0Str, double par1, double par3) {
      double var5 = par1;

      try {
         var5 = Double.parseDouble(par0Str);
      } catch (Throwable var8) {
      }

      if (var5 < par3) {
         var5 = par3;
      }

      return var5;
   }

   private static float getNearestFloat(float value, float[] values) {
      float nearest_float = values[0];
      float smallest_difference = nearest_float - value;
      if (smallest_difference < 0.0F) {
         smallest_difference = -smallest_difference;
      }

      for(int i = 1; i < values.length; ++i) {
         float difference = values[i] - value;
         if (difference < 0.0F) {
            difference = -difference;
         }

         if (difference < smallest_difference) {
            nearest_float = values[i];
            smallest_difference = difference;
         }
      }

      return nearest_float;
   }

   public static float tryFitToFloats(float value, float[] values, float tolerance) {
      float nearest_float = getNearestFloat(value, values);
      if (value < nearest_float) {
         if (nearest_float - value <= tolerance) {
            return nearest_float;
         }
      } else if (value - nearest_float <= tolerance) {
         return nearest_float;
      }

      return value;
   }

   public static float tryFitToNearestInteger(float value, float tolerance) {
      float floor = value < 0.0F ? (float)((int)value - 1) : (float)((int)value);
      float fraction = value - floor;
      if (fraction < 0.5F) {
         return value - floor <= tolerance ? floor : value;
      } else {
         return floor + 1.0F - value <= tolerance ? floor + 1.0F : value;
      }
   }

   public static double getYawInDegrees(double origin_pos_x, double origin_pos_z, double target_pos_x, double target_pos_z) {
      double delta_pos_x = target_pos_x - origin_pos_x;
      double delta_pos_z = target_pos_z - origin_pos_z;
      double yaw = Math.atan2(delta_pos_z, delta_pos_x) * 180.0 / Math.PI - 90.0;
      if (yaw < 0.0) {
         yaw += 360.0;
      }

      return yaw;
   }

   public static double getYawInDegrees(Vec3 origin, Vec3 target) {
      return getYawInDegrees(origin.xCoord, origin.zCoord, target.xCoord, target.zCoord);
   }

   public static double getHorizontalDistance(double origin_pos_x, double origin_pos_z, double target_pos_x, double target_pos_z) {
      double delta_pos_x = target_pos_x - origin_pos_x;
      double delta_pos_z = target_pos_z - origin_pos_z;
      return (double)sqrt_double(delta_pos_x * delta_pos_x + delta_pos_z * delta_pos_z);
   }

   public static double getPitchInDegrees(double origin_pos_x, double origin_pos_y, double origin_pos_z, double target_pos_x, double target_pos_y, double target_pos_z) {
      return getPitchInDegrees(getHorizontalDistance(origin_pos_x, origin_pos_z, target_pos_x, target_pos_z), origin_pos_y, target_pos_y);
   }

   public static double getPitchInDegrees(double horizontal_distance, double origin_pos_y, double target_pos_y) {
      double rise = target_pos_y - origin_pos_y;
      return -Math.atan2(rise, horizontal_distance) * 180.0 / Math.PI;
   }

   public static double getPitchInDegrees(Vec3 origin, Vec3 target) {
      return getPitchInDegrees(origin.xCoord, origin.yCoord, origin.zCoord, target.xCoord, target.yCoord, target.zCoord);
   }

   public static Vec3 getNormalizedVector(float yaw, float pitch, Vec3Pool vec3_pool) {
      float a = cos(-yaw * 0.017453292F - 3.1415927F);
      float b = sin(-yaw * 0.017453292F - 3.1415927F);
      float c = -cos(-pitch * 0.017453292F);
      float d = sin(-pitch * 0.017453292F);
      return vec3_pool.getVecFromPool((double)(b * c), (double)d, (double)(a * c));
   }

   public static Vec3 getNormalizedVector_DP(float yaw, float pitch, Vec3Pool vec3_pool) {
      double PI_divided_by_180 = 0.017453292519943295;
      double a = Math.cos((double)(-yaw) * PI_divided_by_180 - Math.PI);
      double b = Math.sin((double)(-yaw) * PI_divided_by_180 - Math.PI);
      double c = -Math.cos((double)(-pitch) * PI_divided_by_180);
      double d = Math.sin((double)(-pitch) * PI_divided_by_180);
      return vec3_pool.getVecFromPool(b * c, d, a * c);
   }

   public static int wrapToRange(int n, int min, int max) {
      int offset = min;
      n -= offset;
      min -= offset;
      max -= offset;
      int range = max + 1;
      if (n < min) {
         n -= (n - range + 1) / range * range;
      } else if (n > max) {
         n -= n / range * range;
      }

      return n + offset;
   }

   public static int getWrappedIndex(int n, int array_length) {
      return wrapToRange(n, 0, array_length - 1);
   }

   public static boolean isInRange(int n, int min, int max) {
      return n >= min && n <= max;
   }

   public static double getInterpolatedValue(double value_last_tick, double value_this_tick, float partial_tick) {
      return value_last_tick + (value_this_tick - value_last_tick) * (double)partial_tick;
   }

   public static int getIntPairHash(int a, int b) {
      int hash = 17;
      hash = hash * 31 + a;
      hash = hash * 31 + b;
      return hash;
   }

   static {
      for(int var0 = 0; var0 < 65536; ++var0) {
         SIN_TABLE[var0] = (float)Math.sin((double)var0 * Math.PI * 2.0 / 65536.0);
      }

   }
}
