package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorPerlin extends NoiseGenerator {
   private int[] permutations;
   public double xCoord;
   public double yCoord;
   public double zCoord;

   public NoiseGeneratorPerlin() {
      this(new Random());
   }

   public NoiseGeneratorPerlin(Random par1Random) {
      this.permutations = new int[512];
      this.xCoord = par1Random.nextDouble() * 256.0;
      this.yCoord = par1Random.nextDouble() * 256.0;
      this.zCoord = par1Random.nextDouble() * 256.0;

      int var2;
      for(var2 = 0; var2 < 256; this.permutations[var2] = var2++) {
      }

      for(var2 = 0; var2 < 256; ++var2) {
         int var3 = par1Random.nextInt(256 - var2) + var2;
         int var4 = this.permutations[var2];
         this.permutations[var2] = this.permutations[var3];
         this.permutations[var3] = var4;
         this.permutations[var2 + 256] = this.permutations[var2];
      }

   }

   public final double lerp(double par1, double par3, double par5) {
      return par3 + par1 * (par5 - par3);
   }

   public final double func_76309_a(int par1, double par2, double par4) {
      int var6 = par1 & 15;
      double var7 = (double)(1 - ((var6 & 8) >> 3)) * par2;
      double var9 = var6 < 4 ? 0.0 : (var6 != 12 && var6 != 14 ? par4 : par2);
      return ((var6 & 1) == 0 ? var7 : -var7) + ((var6 & 2) == 0 ? var9 : -var9);
   }

   public final double grad(int par1, double par2, double par4, double par6) {
      int var8 = par1 & 15;
      double var9 = var8 < 8 ? par2 : par4;
      double var11 = var8 < 4 ? par4 : (var8 != 12 && var8 != 14 ? par6 : par2);
      return ((var8 & 1) == 0 ? var9 : -var9) + ((var8 & 2) == 0 ? var11 : -var11);
   }

   public void populateNoiseArray(double[] par1ArrayOfDouble, double par2, double par4, double par6, int par8, int par9, int par10, double par11, double par13, double par15, double par17) {
      int var19;
      int var22;
      double var31;
      double var35;
      double var38;
      int var37;
      double var42;
      int var40;
      int var41;
      int var10001;
      int var77;
      boolean var21;
      boolean var67;
      double var29;
      int var34;
      if (par9 == 1) {
         boolean var66 = false;
         boolean var65 = false;
         var21 = false;
         var67 = false;
         double var72 = 0.0;
         double var71 = 0.0;
         var77 = 0;
         var29 = 1.0 / par17;

         for(int var30 = 0; var30 < par8; ++var30) {
            var31 = par2 + (double)var30 * par11 + this.xCoord;
            int var78 = (int)var31;
            if (var31 < (double)var78) {
               --var78;
            }

            var34 = var78 & 255;
            var31 -= (double)var78;
            var35 = var31 * var31 * var31 * (var31 * (var31 * 6.0 - 15.0) + 10.0);

            for(var37 = 0; var37 < par10; ++var37) {
               var38 = par6 + (double)var37 * par15 + this.zCoord;
               var40 = (int)var38;
               if (var38 < (double)var40) {
                  --var40;
               }

               var41 = var40 & 255;
               var38 -= (double)var40;
               var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0 - 15.0) + 10.0);
               var19 = this.permutations[var34] + 0;
               int var64 = this.permutations[var19] + var41;
               int var69 = this.permutations[var34 + 1] + 0;
               var22 = this.permutations[var69] + var41;
               var72 = this.lerp(var35, this.func_76309_a(this.permutations[var64], var31, var38), this.grad(this.permutations[var22], var31 - 1.0, 0.0, var38));
               var71 = this.lerp(var35, this.grad(this.permutations[var64 + 1], var31, 0.0, var38 - 1.0), this.grad(this.permutations[var22 + 1], var31 - 1.0, 0.0, var38 - 1.0));
               double var79 = this.lerp(var42, var72, var71);
               var10001 = var77++;
               par1ArrayOfDouble[var10001] += var79 * var29;
            }
         }
      } else {
         var19 = 0;
         double var20 = 1.0 / par17;
         var22 = -1;
         var21 = false;
         var67 = false;
         boolean var25 = false;
         boolean var26 = false;
         boolean var27 = false;
         boolean var28 = false;
         var29 = 0.0;
         var31 = 0.0;
         double var33 = 0.0;
         var35 = 0.0;

         for(var37 = 0; var37 < par8; ++var37) {
            var38 = par2 + (double)var37 * par11 + this.xCoord;
            var40 = (int)var38;
            if (var38 < (double)var40) {
               --var40;
            }

            var41 = var40 & 255;
            var38 -= (double)var40;
            var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0 - 15.0) + 10.0);

            for(var34 = 0; var34 < par10; ++var34) {
               double var45 = par6 + (double)var34 * par15 + this.zCoord;
               int var47 = (int)var45;
               if (var45 < (double)var47) {
                  --var47;
               }

               int var48 = var47 & 255;
               var45 -= (double)var47;
               double var49 = var45 * var45 * var45 * (var45 * (var45 * 6.0 - 15.0) + 10.0);

               for(int var51 = 0; var51 < par9; ++var51) {
                  double var52 = par4 + (double)var51 * par13 + this.yCoord;
                  int var54 = (int)var52;
                  if (var52 < (double)var54) {
                     --var54;
                  }

                  int var55 = var54 & 255;
                  var52 -= (double)var54;
                  double var56 = var52 * var52 * var52 * (var52 * (var52 * 6.0 - 15.0) + 10.0);
                  if (var51 == 0 || var55 != var22) {
                     var22 = var55;
                     int var68 = this.permutations[var41] + var55;
                     int var73 = this.permutations[var68] + var48;
                     int var70 = this.permutations[var68 + 1] + var48;
                     int var76 = this.permutations[var41 + 1] + var55;
                     var77 = this.permutations[var76] + var48;
                     int var75 = this.permutations[var76 + 1] + var48;
                     var29 = this.lerp(var42, this.grad(this.permutations[var73], var38, var52, var45), this.grad(this.permutations[var77], var38 - 1.0, var52, var45));
                     var31 = this.lerp(var42, this.grad(this.permutations[var70], var38, var52 - 1.0, var45), this.grad(this.permutations[var75], var38 - 1.0, var52 - 1.0, var45));
                     var33 = this.lerp(var42, this.grad(this.permutations[var73 + 1], var38, var52, var45 - 1.0), this.grad(this.permutations[var77 + 1], var38 - 1.0, var52, var45 - 1.0));
                     var35 = this.lerp(var42, this.grad(this.permutations[var70 + 1], var38, var52 - 1.0, var45 - 1.0), this.grad(this.permutations[var75 + 1], var38 - 1.0, var52 - 1.0, var45 - 1.0));
                  }

                  double var58 = this.lerp(var56, var29, var31);
                  double var60 = this.lerp(var56, var33, var35);
                  double var62 = this.lerp(var49, var58, var60);
                  var10001 = var19++;
                  par1ArrayOfDouble[var10001] += var62 * var20;
               }
            }
         }
      }

   }

   public void xpopulateNoiseArray(double[] par1ArrayOfDouble, double par2, double par4, double par6, int par8, int par9, int par10, double par11, double par13, double par15, double par17) {
      int var10001;
      int var19;
      int var22;
      double var31;
      double var35;
      double var38;
      int var37;
      double var42;
      int var40;
      int var41;
      int var77;
      double var20;
      double var29;
      double var33;
      int var34;
      if (par9 == 1) {
         var20 = 0.0;
         var29 = 0.0;
         var77 = 0;
         var33 = 1.0 / par17;

         for(int var30 = 0; var30 < par8; ++var30) {
            var31 = par2 + (double)var30 * par11 + this.xCoord;
            int var78 = (int)var31;
            if (var31 < (double)var78) {
               --var78;
            }

            var34 = var78 & 255;
            var31 -= (double)var78;
            var35 = var31 * var31 * var31 * (var31 * (var31 * 6.0 - 15.0) + 10.0);

            for(var37 = 0; var37 < par10; ++var37) {
               var38 = par6 + (double)var37 * par15 + this.zCoord;
               var40 = (int)var38;
               if (var38 < (double)var40) {
                  --var40;
               }

               var41 = var40 & 255;
               var38 -= (double)var40;
               var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0 - 15.0) + 10.0);
               var19 = this.permutations[var34] + 0;
               int var64 = this.permutations[var19] + var41;
               int var69 = this.permutations[var34 + 1] + 0;
               var22 = this.permutations[var69] + var41;
               var20 = this.lerp(var35, this.func_76309_a(this.permutations[var64], var31, var38), this.grad(this.permutations[var22], var31 - 1.0, 0.0, var38));
               var29 = this.lerp(var35, this.grad(this.permutations[var64 + 1], var31, 0.0, var38 - 1.0), this.grad(this.permutations[var22 + 1], var31 - 1.0, 0.0, var38 - 1.0));
               var10001 = var77++;
               par1ArrayOfDouble[var10001] += this.lerp(var42, var20, var29) * var33;
            }
         }
      } else {
         var19 = 0;
         var20 = 1.0 / par17;
         var22 = -1;
         var29 = 0.0;
         var31 = 0.0;
         var33 = 0.0;
         var35 = 0.0;

         for(var37 = 0; var37 < par8; ++var37) {
            var38 = par2 + (double)var37 * par11 + this.xCoord;
            var40 = (int)var38;
            if (var38 < (double)var40) {
               --var40;
            }

            var41 = var40 & 255;
            var38 -= (double)var40;
            var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0 - 15.0) + 10.0);
            double var38_minus_1 = var38 - 1.0;

            for(var34 = 0; var34 < par10; ++var34) {
               double var45 = par6 + (double)var34 * par15 + this.zCoord;
               int var47 = (int)var45;
               if (var45 < (double)var47) {
                  --var47;
               }

               int var48 = var47 & 255;
               var45 -= (double)var47;
               double var49 = var45 * var45 * var45 * (var45 * (var45 * 6.0 - 15.0) + 10.0);
               double var45_minus_1 = var45 - 1.0;

               for(int var51 = 0; var51 < par9; ++var51) {
                  double var52 = par4 + (double)var51 * par13 + this.yCoord;
                  int var54 = (int)var52;
                  if (var52 < (double)var54) {
                     --var54;
                  }

                  int var55 = var54 & 255;
                  var52 -= (double)var54;
                  double var56 = var52 * var52 * var52 * (var52 * (var52 * 6.0 - 15.0) + 10.0);
                  double var52_minus_1 = var52 - 1.0;
                  if (var51 == 0 || var55 != var22) {
                     var22 = var55;
                     int var68 = this.permutations[var41] + var55;
                     int var73 = this.permutations[var68] + var48;
                     int var70 = this.permutations[var68 + 1] + var48;
                     int var76 = this.permutations[var41 + 1] + var55;
                     var77 = this.permutations[var76] + var48;
                     int var75 = this.permutations[var76 + 1] + var48;
                     var29 = this.lerp(var42, this.grad(this.permutations[var73], var38, var52, var45), this.grad(this.permutations[var77], var38_minus_1, var52, var45));
                     var31 = this.lerp(var42, this.grad(this.permutations[var70], var38, var52_minus_1, var45), this.grad(this.permutations[var75], var38_minus_1, var52_minus_1, var45));
                     var33 = this.lerp(var42, this.grad(this.permutations[var73 + 1], var38, var52, var45_minus_1), this.grad(this.permutations[var77 + 1], var38_minus_1, var52, var45_minus_1));
                     var35 = this.lerp(var42, this.grad(this.permutations[var70 + 1], var38, var52_minus_1, var45_minus_1), this.grad(this.permutations[var75 + 1], var38_minus_1, var52_minus_1, var45_minus_1));
                  }

                  var10001 = var19++;
                  par1ArrayOfDouble[var10001] += this.lerp(var49, this.lerp(var56, var29, var31), this.lerp(var56, var33, var35)) * var20;
               }
            }
         }
      }

   }
}
