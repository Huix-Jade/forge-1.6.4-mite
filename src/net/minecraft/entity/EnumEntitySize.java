package net.minecraft.entity;

import net.minecraft.util.MathHelper;

public enum EnumEntitySize {
   SIZE_1,
   SIZE_2,
   SIZE_3,
   SIZE_4,
   SIZE_5,
   SIZE_6;

   public int multiplyByNAndRound(double par1, int n)
   {
      double var3 = par1 - ((double)MathHelper.floor_double(par1) + 0.5D);

      switch (EnumEntitySizeHelper.field_96565_a[this.ordinal()])
      {
         case 1:
            if (var3 < 0.0D)
            {
               if (var3 < -0.3125D)
               {
                  return MathHelper.ceiling_double_int(par1 * (double)n);
               }
            }
            else if (var3 < 0.3125D)
            {
               return MathHelper.ceiling_double_int(par1 * (double)n);
            }

            return MathHelper.floor_double(par1 * (double)n);

         case 2:
            if (var3 < 0.0D)
            {
               if (var3 < -0.3125D)
               {
                  return MathHelper.floor_double(par1 * (double)n);
               }
            }
            else if (var3 < 0.3125D)
            {
               return MathHelper.floor_double(par1 * (double)n);
            }

            return MathHelper.ceiling_double_int(par1 * (double)n);

         case 3:
            if (var3 > 0.0D)
            {
               return MathHelper.floor_double(par1 * (double)n);
            }

            return MathHelper.ceiling_double_int(par1 * (double)n);

         case 4:
            if (var3 < 0.0D)
            {
               if (var3 < -0.1875D)
               {
                  return MathHelper.ceiling_double_int(par1 * (double)n);
               }
            }
            else if (var3 < 0.1875D)
            {
               return MathHelper.ceiling_double_int(par1 * (double)n);
            }

            return MathHelper.floor_double(par1 * (double)n);

         case 5:
            if (var3 < 0.0D)
            {
               if (var3 < -0.1875D)
               {
                  return MathHelper.floor_double(par1 * (double)n);
               }
            }
            else if (var3 < 0.1875D)
            {
               return MathHelper.floor_double(par1 * (double)n);
            }

            return MathHelper.ceiling_double_int(par1 * (double)n);

         case 6:
         default:
            return var3 > 0.0D ? MathHelper.ceiling_double_int(par1 * (double)n) : MathHelper.floor_double(par1 * (double)n);
      }
   }
}
