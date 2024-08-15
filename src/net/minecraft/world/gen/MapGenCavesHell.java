package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MapGenCavesHell extends MapGenBase {
   protected void generateLargeCaveNode(long var1, int var3, int var4, byte[] var5, double var6, double var8, double var10) {
      this.generateCaveNode(var1, var3, var4, var5, var6, var8, var10, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
   }

   protected void generateCaveNode(long var1, int var3, int var4, byte[] var5, double var6, double var8, double var10, float var12, float var13, float var14, int var15, int var16, double var17) {
      double var19 = (double)(var3 * 16 + 8);
      double var21 = (double)(var4 * 16 + 8);
      float var23 = 0.0F;
      float var24 = 0.0F;
      Random var25 = new Random(var1);
      if (var16 <= 0) {
         int var26 = this.range * 16 - 16;
         var16 = var26 - var25.nextInt(var26 / 4);
      }

      boolean var53 = false;
      if (var15 == -1) {
         var15 = var16 / 2;
         var53 = true;
      }

      int var27 = var25.nextInt(var16 / 2) + var16 / 4;

      for(boolean var28 = var25.nextInt(6) == 0; var15 < var16; ++var15) {
         double var29 = 1.5 + (double)(MathHelper.sin((float)var15 * 3.1415927F / (float)var16) * var12 * 1.0F);
         double var31 = var29 * var17;
         float var33 = MathHelper.cos(var14);
         float var34 = MathHelper.sin(var14);
         var6 += (double)(MathHelper.cos(var13) * var33);
         var8 += (double)var34;
         var10 += (double)(MathHelper.sin(var13) * var33);
         if (var28) {
            var14 *= 0.92F;
         } else {
            var14 *= 0.7F;
         }

         var14 += var24 * 0.1F;
         var13 += var23 * 0.1F;
         var24 *= 0.9F;
         var23 *= 0.75F;
         var24 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 2.0F;
         var23 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 4.0F;
         if (!var53 && var15 == var27 && var12 > 1.0F) {
            this.generateCaveNode(var25.nextLong(), var3, var4, var5, var6, var8, var10, var25.nextFloat() * 0.5F + 0.5F, var13 - 1.5707964F, var14 / 3.0F, var15, var16, 1.0);
            this.generateCaveNode(var25.nextLong(), var3, var4, var5, var6, var8, var10, var25.nextFloat() * 0.5F + 0.5F, var13 + 1.5707964F, var14 / 3.0F, var15, var16, 1.0);
            return;
         }

         if (var53 || var25.nextInt(4) != 0) {
            double var35 = var6 - var19;
            double var37 = var10 - var21;
            double var39 = (double)(var16 - var15);
            double var41 = (double)(var12 + 2.0F + 16.0F);
            if (var35 * var35 + var37 * var37 - var39 * var39 > var41 * var41) {
               return;
            }

            if (!(var6 < var19 - 16.0 - var29 * 2.0) && !(var10 < var21 - 16.0 - var29 * 2.0) && !(var6 > var19 + 16.0 + var29 * 2.0) && !(var10 > var21 + 16.0 + var29 * 2.0)) {
               int var54 = MathHelper.floor_double(var6 - var29) - var3 * 16 - 1;
               int var36 = MathHelper.floor_double(var6 + var29) - var3 * 16 + 1;
               int var55 = MathHelper.floor_double(var8 - var31) - 1;
               int var38 = MathHelper.floor_double(var8 + var31) + 1;
               int var56 = MathHelper.floor_double(var10 - var29) - var4 * 16 - 1;
               int var40 = MathHelper.floor_double(var10 + var29) - var4 * 16 + 1;
               if (var54 < 0) {
                  var54 = 0;
               }

               if (var36 > 16) {
                  var36 = 16;
               }

               if (var55 < 1) {
                  var55 = 1;
               }

               if (var38 > 120) {
                  var38 = 120;
               }

               if (var56 < 0) {
                  var56 = 0;
               }

               if (var40 > 16) {
                  var40 = 16;
               }

               boolean var57 = false;

               int var42;
               int var45;
               for(var42 = var54; !var57 && var42 < var36; ++var42) {
                  for(int var43 = var56; !var57 && var43 < var40; ++var43) {
                     for(int var44 = var38 + 1; !var57 && var44 >= var55 - 1; --var44) {
                        var45 = (var42 * 16 + var43) * 128 + var44;
                        if (var44 >= 0 && var44 < 128) {
                           if (var5[var45] == Block.lavaMoving.blockID || var5[var45] == Block.lavaStill.blockID) {
                              var57 = true;
                           }

                           if (var44 != var55 - 1 && var42 != var54 && var42 != var36 - 1 && var43 != var56 && var43 != var40 - 1) {
                              var44 = var55;
                           }
                        }
                     }
                  }
               }

               if (!var57) {
                  for(var42 = var54; var42 < var36; ++var42) {
                     double var58 = ((double)(var42 + var3 * 16) + 0.5 - var6) / var29;

                     for(var45 = var56; var45 < var40; ++var45) {
                        double var46 = ((double)(var45 + var4 * 16) + 0.5 - var10) / var29;
                        int var48 = (var42 * 16 + var45) * 128 + var38;

                        for(int var49 = var38 - 1; var49 >= var55; --var49) {
                           double var50 = ((double)var49 + 0.5 - var8) / var31;
                           if (var50 > -0.7 && var58 * var58 + var50 * var50 + var46 * var46 < 1.0) {
                              byte var52 = var5[var48];
                              if (var52 == Block.netherrack.blockID || var52 == Block.dirt.blockID || var52 == Block.grass.blockID) {
                                 var5[var48] = 0;
                              }
                           }

                           --var48;
                        }
                     }
                  }

                  if (var53) {
                     break;
                  }
               }
            }
         }
      }

   }

   protected void recursiveGenerate(World var1, int var2, int var3, int var4, int var5, byte[] var6) {
      int var7 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(10) + 1) + 1);
      if (this.rand.nextInt(5) != 0) {
         var7 = 0;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         double var9 = (double)(var2 * 16 + this.rand.nextInt(16));
         double var11 = (double)this.rand.nextInt(128);
         double var13 = (double)(var3 * 16 + this.rand.nextInt(16));
         int var15 = 1;
         if (this.rand.nextInt(4) == 0) {
            this.generateLargeCaveNode(this.rand.nextLong(), var4, var5, var6, var9, var11, var13);
            var15 += this.rand.nextInt(4);
         }

         for(int var16 = 0; var16 < var15; ++var16) {
            float var17 = this.rand.nextFloat() * 3.1415927F * 2.0F;
            float var18 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float var19 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
            this.generateCaveNode(this.rand.nextLong(), var4, var5, var6, var9, var11, var13, var19 * 2.0F, var17, var18, 0, 0, 0.5);
         }
      }

   }
}
