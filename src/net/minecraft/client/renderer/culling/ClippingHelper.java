package net.minecraft.client.renderer.culling;

public class ClippingHelper {
   public float[][] frustum = new float[16][16];
   public float[] projectionMatrix = new float[16];
   public float[] modelviewMatrix = new float[16];
   public float[] clippingMatrix = new float[16];
   public float frustum_0_0;
   public float frustum_0_1;
   public float frustum_0_2;
   public float frustum_0_3;
   public float frustum_1_0;
   public float frustum_1_1;
   public float frustum_1_2;
   public float frustum_1_3;
   public float frustum_2_0;
   public float frustum_2_1;
   public float frustum_2_2;
   public float frustum_2_3;
   public float frustum_3_0;
   public float frustum_3_1;
   public float frustum_3_2;
   public float frustum_3_3;
   public float frustum_4_0;
   public float frustum_4_1;
   public float frustum_4_2;
   public float frustum_4_3;
   public float frustum_5_0;
   public float frustum_5_1;
   public float frustum_5_2;
   public float frustum_5_3;

   public final boolean isBoxInFrustumMITE(double par1, double par3, double par5, double par7, double par9, double par11) {
      if ((double)this.frustum_0_0 * par1 + (double)this.frustum_0_1 * par3 + (double)this.frustum_0_2 * par5 + (double)this.frustum_0_3 <= 0.0 && (double)this.frustum_0_0 * par7 + (double)this.frustum_0_1 * par3 + (double)this.frustum_0_2 * par5 + (double)this.frustum_0_3 <= 0.0 && (double)this.frustum_0_0 * par1 + (double)this.frustum_0_1 * par9 + (double)this.frustum_0_2 * par5 + (double)this.frustum_0_3 <= 0.0 && (double)this.frustum_0_0 * par7 + (double)this.frustum_0_1 * par9 + (double)this.frustum_0_2 * par5 + (double)this.frustum_0_3 <= 0.0 && (double)this.frustum_0_0 * par1 + (double)this.frustum_0_1 * par3 + (double)this.frustum_0_2 * par11 + (double)this.frustum_0_3 <= 0.0 && (double)this.frustum_0_0 * par7 + (double)this.frustum_0_1 * par3 + (double)this.frustum_0_2 * par11 + (double)this.frustum_0_3 <= 0.0 && (double)this.frustum_0_0 * par1 + (double)this.frustum_0_1 * par9 + (double)this.frustum_0_2 * par11 + (double)this.frustum_0_3 <= 0.0 && (double)this.frustum_0_0 * par7 + (double)this.frustum_0_1 * par9 + (double)this.frustum_0_2 * par11 + (double)this.frustum_0_3 <= 0.0) {
         return false;
      } else if ((double)this.frustum_1_0 * par1 + (double)this.frustum_1_1 * par3 + (double)this.frustum_1_2 * par5 + (double)this.frustum_1_3 <= 0.0 && (double)this.frustum_1_0 * par7 + (double)this.frustum_1_1 * par3 + (double)this.frustum_1_2 * par5 + (double)this.frustum_1_3 <= 0.0 && (double)this.frustum_1_0 * par1 + (double)this.frustum_1_1 * par9 + (double)this.frustum_1_2 * par5 + (double)this.frustum_1_3 <= 0.0 && (double)this.frustum_1_0 * par7 + (double)this.frustum_1_1 * par9 + (double)this.frustum_1_2 * par5 + (double)this.frustum_1_3 <= 0.0 && (double)this.frustum_1_0 * par1 + (double)this.frustum_1_1 * par3 + (double)this.frustum_1_2 * par11 + (double)this.frustum_1_3 <= 0.0 && (double)this.frustum_1_0 * par7 + (double)this.frustum_1_1 * par3 + (double)this.frustum_1_2 * par11 + (double)this.frustum_1_3 <= 0.0 && (double)this.frustum_1_0 * par1 + (double)this.frustum_1_1 * par9 + (double)this.frustum_1_2 * par11 + (double)this.frustum_1_3 <= 0.0 && (double)this.frustum_1_0 * par7 + (double)this.frustum_1_1 * par9 + (double)this.frustum_1_2 * par11 + (double)this.frustum_1_3 <= 0.0) {
         return false;
      } else if ((double)this.frustum_2_0 * par1 + (double)this.frustum_2_1 * par3 + (double)this.frustum_2_2 * par5 + (double)this.frustum_2_3 <= 0.0 && (double)this.frustum_2_0 * par7 + (double)this.frustum_2_1 * par3 + (double)this.frustum_2_2 * par5 + (double)this.frustum_2_3 <= 0.0 && (double)this.frustum_2_0 * par1 + (double)this.frustum_2_1 * par9 + (double)this.frustum_2_2 * par5 + (double)this.frustum_2_3 <= 0.0 && (double)this.frustum_2_0 * par7 + (double)this.frustum_2_1 * par9 + (double)this.frustum_2_2 * par5 + (double)this.frustum_2_3 <= 0.0 && (double)this.frustum_2_0 * par1 + (double)this.frustum_2_1 * par3 + (double)this.frustum_2_2 * par11 + (double)this.frustum_2_3 <= 0.0 && (double)this.frustum_2_0 * par7 + (double)this.frustum_2_1 * par3 + (double)this.frustum_2_2 * par11 + (double)this.frustum_2_3 <= 0.0 && (double)this.frustum_2_0 * par1 + (double)this.frustum_2_1 * par9 + (double)this.frustum_2_2 * par11 + (double)this.frustum_2_3 <= 0.0 && (double)this.frustum_2_0 * par7 + (double)this.frustum_2_1 * par9 + (double)this.frustum_2_2 * par11 + (double)this.frustum_2_3 <= 0.0) {
         return false;
      } else if ((double)this.frustum_3_0 * par1 + (double)this.frustum_3_1 * par3 + (double)this.frustum_3_2 * par5 + (double)this.frustum_3_3 <= 0.0 && (double)this.frustum_3_0 * par7 + (double)this.frustum_3_1 * par3 + (double)this.frustum_3_2 * par5 + (double)this.frustum_3_3 <= 0.0 && (double)this.frustum_3_0 * par1 + (double)this.frustum_3_1 * par9 + (double)this.frustum_3_2 * par5 + (double)this.frustum_3_3 <= 0.0 && (double)this.frustum_3_0 * par7 + (double)this.frustum_3_1 * par9 + (double)this.frustum_3_2 * par5 + (double)this.frustum_3_3 <= 0.0 && (double)this.frustum_3_0 * par1 + (double)this.frustum_3_1 * par3 + (double)this.frustum_3_2 * par11 + (double)this.frustum_3_3 <= 0.0 && (double)this.frustum_3_0 * par7 + (double)this.frustum_3_1 * par3 + (double)this.frustum_3_2 * par11 + (double)this.frustum_3_3 <= 0.0 && (double)this.frustum_3_0 * par1 + (double)this.frustum_3_1 * par9 + (double)this.frustum_3_2 * par11 + (double)this.frustum_3_3 <= 0.0 && (double)this.frustum_3_0 * par7 + (double)this.frustum_3_1 * par9 + (double)this.frustum_3_2 * par11 + (double)this.frustum_3_3 <= 0.0) {
         return false;
      } else if ((double)this.frustum_4_0 * par1 + (double)this.frustum_4_1 * par3 + (double)this.frustum_4_2 * par5 + (double)this.frustum_4_3 <= 0.0 && (double)this.frustum_4_0 * par7 + (double)this.frustum_4_1 * par3 + (double)this.frustum_4_2 * par5 + (double)this.frustum_4_3 <= 0.0 && (double)this.frustum_4_0 * par1 + (double)this.frustum_4_1 * par9 + (double)this.frustum_4_2 * par5 + (double)this.frustum_4_3 <= 0.0 && (double)this.frustum_4_0 * par7 + (double)this.frustum_4_1 * par9 + (double)this.frustum_4_2 * par5 + (double)this.frustum_4_3 <= 0.0 && (double)this.frustum_4_0 * par1 + (double)this.frustum_4_1 * par3 + (double)this.frustum_4_2 * par11 + (double)this.frustum_4_3 <= 0.0 && (double)this.frustum_4_0 * par7 + (double)this.frustum_4_1 * par3 + (double)this.frustum_4_2 * par11 + (double)this.frustum_4_3 <= 0.0 && (double)this.frustum_4_0 * par1 + (double)this.frustum_4_1 * par9 + (double)this.frustum_4_2 * par11 + (double)this.frustum_4_3 <= 0.0 && (double)this.frustum_4_0 * par7 + (double)this.frustum_4_1 * par9 + (double)this.frustum_4_2 * par11 + (double)this.frustum_4_3 <= 0.0) {
         return false;
      } else {
         return !((double)this.frustum_5_0 * par1 + (double)this.frustum_5_1 * par3 + (double)this.frustum_5_2 * par5 + (double)this.frustum_5_3 <= 0.0) || !((double)this.frustum_5_0 * par7 + (double)this.frustum_5_1 * par3 + (double)this.frustum_5_2 * par5 + (double)this.frustum_5_3 <= 0.0) || !((double)this.frustum_5_0 * par1 + (double)this.frustum_5_1 * par9 + (double)this.frustum_5_2 * par5 + (double)this.frustum_5_3 <= 0.0) || !((double)this.frustum_5_0 * par7 + (double)this.frustum_5_1 * par9 + (double)this.frustum_5_2 * par5 + (double)this.frustum_5_3 <= 0.0) || !((double)this.frustum_5_0 * par1 + (double)this.frustum_5_1 * par3 + (double)this.frustum_5_2 * par11 + (double)this.frustum_5_3 <= 0.0) || !((double)this.frustum_5_0 * par7 + (double)this.frustum_5_1 * par3 + (double)this.frustum_5_2 * par11 + (double)this.frustum_5_3 <= 0.0) || !((double)this.frustum_5_0 * par1 + (double)this.frustum_5_1 * par9 + (double)this.frustum_5_2 * par11 + (double)this.frustum_5_3 <= 0.0) || !((double)this.frustum_5_0 * par7 + (double)this.frustum_5_1 * par9 + (double)this.frustum_5_2 * par11 + (double)this.frustum_5_3 <= 0.0);
      }
   }

   public float getFloat(String field) {
      try {
         return this.getClass().getDeclaredField(field).getFloat(this);
      } catch (Exception var3) {
         return 0.0F;
      }
   }
}
