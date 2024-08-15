package net.minecraft.util;

public enum EnumItemInUseAction {
   EAT,
   DRINK,
   BLOCK,
   BOW;

   public boolean isIngestion() {
      return this == EAT || this == DRINK;
   }
}
