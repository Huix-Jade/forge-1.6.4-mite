package net.minecraft.util;

public class StatCollector {
   private static StringTranslate localizedName = StringTranslate.getInstance();

   public static String translateToLocal(String var0) {
      return localizedName.translateKey(var0);
   }

   public static String translateToLocalFormatted(String var0, Object... var1) {
      return localizedName.translateKeyFormat(var0, var1);
   }

   public static boolean func_94522_b(String var0) {
      return localizedName.containsTranslateKey(var0);
   }
}
