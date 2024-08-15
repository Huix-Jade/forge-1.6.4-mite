package net.minecraft.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class WeightedRandom {
   public static int getTotalWeight(Collection var0) {
      int var1 = 0;

      WeightedRandomItem var3;
      for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 += var3.itemWeight) {
         var3 = (WeightedRandomItem)var2.next();
      }

      return var1;
   }

   public static WeightedRandomItem getRandomItem(Random var0, Collection var1, int var2) {
      if (var2 <= 0) {
         throw new IllegalArgumentException();
      } else {
         int var3 = var0.nextInt(var2);
         Iterator var4 = var1.iterator();

         WeightedRandomItem var5;
         do {
            if (!var4.hasNext()) {
               return null;
            }

            var5 = (WeightedRandomItem)var4.next();
            var3 -= var5.itemWeight;
         } while(var3 >= 0);

         return var5;
      }
   }

   public static WeightedRandomItem getRandomItem(Random var0, Collection var1) {
      return getRandomItem(var0, var1, getTotalWeight(var1));
   }

   public static int getTotalWeight(WeightedRandomItem[] var0) {
      int var1 = 0;
      WeightedRandomItem[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WeightedRandomItem var5 = var2[var4];
         var1 += var5.itemWeight;
      }

      return var1;
   }

   public static WeightedRandomItem getRandomItem(Random var0, WeightedRandomItem[] var1, int var2) {
      if (var2 <= 0) {
         throw new IllegalArgumentException();
      } else {
         int var3 = var0.nextInt(var2);
         WeightedRandomItem[] var4 = var1;
         int var5 = var1.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            WeightedRandomItem var7 = var4[var6];
            var3 -= var7.itemWeight;
            if (var3 < 0) {
               return var7;
            }
         }

         return null;
      }
   }

   public static WeightedRandomItem getRandomItem(Random var0, WeightedRandomItem[] var1) {
      return getRandomItem(var0, var1, getTotalWeight(var1));
   }
}
