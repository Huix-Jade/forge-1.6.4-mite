package net.minecraft.util;

final class AABBLocalPool extends ThreadLocal {
   protected AABBPool createNewDefaultPool() {
      return new AABBPool(300, 2000);
   }

   // $FF: synthetic method
   protected Object initialValue() {
      return this.createNewDefaultPool();
   }
}
