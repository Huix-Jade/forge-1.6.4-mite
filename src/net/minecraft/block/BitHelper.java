package net.minecraft.block;

public final class BitHelper {
   public static int clearBit(int data, int bit_value) {
      return data & ~bit_value;
   }

   public static int flipBit(int data, int bit_value) {
      return data ^ bit_value;
   }

   public static boolean isBitSet(int data, int bit_value) {
      return (data & bit_value) == bit_value;
   }

   public static boolean isAnyBitSet(int data, int bit_values) {
      return (data & bit_values) != 0;
   }

   public static int getBitValue(int bit_position) {
      return 1 << bit_position;
   }
}
