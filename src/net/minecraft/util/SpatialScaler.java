package net.minecraft.util;

import net.minecraft.entity.Entity;

public class SpatialScaler {
   private static final int scaling_factor_for_pos_xz = 4095;
   private static final int scaling_factor_for_pos_y = 127;

   public static int getScalingFactorForRotation() {
      return 256;
   }

   public static int getScaledPosX(Entity entity) {
      return entity.myEntitySize.multiplyByNAndRound(entity.posX, 4095);
   }

   public static int getScaledPosX(double pos_x) {
      return MathHelper.floor_double(pos_x * 4095.0);
   }

   public static double getPosX(int scaled_pos_x) {
      return (double)scaled_pos_x / 4095.0;
   }

   public static int getScaledPosY(Entity entity) {
      return MathHelper.floor_double(entity.posY * 127.0);
   }

   public static int getScaledPosY(double pos_y) {
      return MathHelper.floor_double(pos_y * 127.0);
   }

   public static double getPosY(int scaled_pos_y) {
      return (double)scaled_pos_y / 127.0 + 0.015625;
   }

   public static int getScaledPosZ(Entity entity) {
      return entity.myEntitySize.multiplyByNAndRound(entity.posZ, 4095);
   }

   public static int getScaledPosZ(double pos_z) {
      return MathHelper.floor_double(pos_z * 4095.0);
   }

   public static double getPosZ(int scaled_pos_z) {
      return (double)scaled_pos_z / 4095.0;
   }

   public static int getScaledRotation(float rotation) {
      return MathHelper.floor_float(rotation * (float)getScalingFactorForRotation() / 360.0F);
   }

   public static float getRotation(int scaled_rotation) {
      return (float)scaled_rotation * 360.0F / (float)getScalingFactorForRotation();
   }

   public static int getScaledMotion(double motion) {
      double var2 = 3.9;
      double var4 = motion;
      if (var4 < -var2) {
         var4 = -var2;
      } else if (var4 > var2) {
         var4 = var2;
      }

      return (int)(var4 * 8000.0);
   }

   public static double getMotion(int scaled_motion) {
      return (double)scaled_motion / 8000.0;
   }
}
