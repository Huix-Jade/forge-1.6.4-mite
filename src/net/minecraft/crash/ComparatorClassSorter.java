package net.minecraft.crash;

import java.util.Comparator;

class ComparatorClassSorter implements Comparator {
   // $FF: synthetic field
   final CallableSuspiciousClasses theSuspiciousClasses;

   ComparatorClassSorter(CallableSuspiciousClasses var1) {
      this.theSuspiciousClasses = var1;
   }

   public int func_85081_a(Class var1, Class var2) {
      String var3 = var1.getPackage() == null ? "" : var1.getPackage().getName();
      String var4 = var2.getPackage() == null ? "" : var2.getPackage().getName();
      return var3.compareTo(var4);
   }

   // $FF: synthetic method
   public int compare(Object var1, Object var2) {
      return this.func_85081_a((Class)var1, (Class)var2);
   }
}
