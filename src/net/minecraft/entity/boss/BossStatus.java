package net.minecraft.entity.boss;

public final class BossStatus {
   public static float healthScale;
   public static int statusBarLength;
   public static String bossName;
   public static boolean field_82825_d;

   public static void setBossStatus(IBossDisplayData var0, boolean var1) {
      healthScale = var0.getHealth() / var0.getMaxHealth();
      statusBarLength = 100;
      bossName = var0.getEntityName();
      field_82825_d = var1;
   }
}
