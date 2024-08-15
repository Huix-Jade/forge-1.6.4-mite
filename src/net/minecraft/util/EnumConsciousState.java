package net.minecraft.util;

public enum EnumConsciousState {
   fully_awake,
   falling_asleep,
   sleeping,
   waking_up;

   public void print() {
      if (this == fully_awake) {
         System.out.println("fully awake");
      } else if (this == falling_asleep) {
         System.out.println("falling asleep");
      } else if (this == sleeping) {
         System.out.println("sleeping");
      } else if (this == waking_up) {
         System.out.println("waking up");
      }

   }
}
