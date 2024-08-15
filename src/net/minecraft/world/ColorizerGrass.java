package net.minecraft.world;

public class ColorizerGrass {
   private static int[] grassBuffer = new int[65536];

   public static void setGrassBiomeColorizer(int[] var0) {
      grassBuffer = var0;
   }

   public static int getGrassColor(double var0, double var2) {
      var2 *= var0;
      int var4 = (int)((1.0 - var0) * 255.0);
      int var5 = (int)((1.0 - var2) * 255.0);
      return grassBuffer[var5 << 8 | var4];
   }
}
