package net.minecraft.util;

public class Tuple {
   private Object first;
   private Object second;

   public Tuple(Object var1, Object var2) {
      this.first = var1;
      this.second = var2;
   }

   public Object getFirst() {
      return this.first;
   }

   public Object getSecond() {
      return this.second;
   }
}
