package net.minecraft.util;

public class Util {
   public static EnumOS getOSType() {
      String var0 = System.getProperty("os.name").toLowerCase();
      if (var0.contains("win")) {
         return EnumOS.WINDOWS;
      } else if (var0.contains("mac")) {
         return EnumOS.MACOS;
      } else if (var0.contains("solaris")) {
         return EnumOS.SOLARIS;
      } else if (var0.contains("sunos")) {
         return EnumOS.SOLARIS;
      } else if (var0.contains("linux")) {
         return EnumOS.LINUX;
      } else {
         return var0.contains("unix") ? EnumOS.LINUX : EnumOS.UNKNOWN;
      }
   }
}
