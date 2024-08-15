package net.minecraft.util;

public enum EnumSide {
   BOTTOM,
   TOP,
   NORTH,
   SOUTH,
   WEST,
   EAST;

   public static boolean isValidOrdinal(int ordinal) {
      return ordinal >= 0 && ordinal < values().length;
   }

   public static EnumSide get(int ordinal) {
      return values()[ordinal];
   }

   public boolean isBottom() {
      return this == BOTTOM;
   }

   public boolean isTop() {
      return this == TOP;
   }

   public boolean isNorth() {
      return this == NORTH;
   }

   public boolean isSouth() {
      return this == SOUTH;
   }

   public boolean isWest() {
      return this == WEST;
   }

   public boolean isEast() {
      return this == EAST;
   }

   public boolean isTopOrBottom() {
      return this == TOP || this == BOTTOM;
   }

   public boolean isNorthOrSouth() {
      return this == NORTH || this == SOUTH;
   }

   public boolean isEastOrWest() {
      return this == EAST || this == WEST;
   }

   public int getNeighborX(int x) {
      return this == WEST ? x - 1 : (this == EAST ? x + 1 : x);
   }

   public int getNeighborY(int y) {
      return this == BOTTOM ? y - 1 : (this == TOP ? y + 1 : y);
   }

   public int getNeighborZ(int z) {
      return this == NORTH ? z - 1 : (this == SOUTH ? z + 1 : z);
   }
}
