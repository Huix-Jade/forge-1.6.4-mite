package net.minecraft.util;

public enum EnumFace {
   BOTTOM,
   TOP,
   NORTH,
   SOUTH,
   WEST,
   EAST;

   public static boolean isValidOrdinal(int ordinal) {
      return ordinal >= 0 && ordinal < values().length;
   }

   public static EnumFace get(int ordinal) {
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

   public boolean isSide() {
      return this == NORTH || this == SOUTH || this == WEST || this == EAST;
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

   public EnumFace getOpposite() {
      if (this == BOTTOM) {
         return TOP;
      } else if (this == TOP) {
         return BOTTOM;
      } else if (this == NORTH) {
         return SOUTH;
      } else if (this == SOUTH) {
         return NORTH;
      } else {
         return this == WEST ? EAST : WEST;
      }
   }

   public EnumDirection getNormal() {
      if (this == BOTTOM) {
         return EnumDirection.DOWN;
      } else if (this == TOP) {
         return EnumDirection.UP;
      } else if (this == NORTH) {
         return EnumDirection.NORTH;
      } else if (this == SOUTH) {
         return EnumDirection.SOUTH;
      } else {
         return this == WEST ? EnumDirection.WEST : EnumDirection.EAST;
      }
   }

   public EnumDirection getAntiNormal() {
      if (this == BOTTOM) {
         return EnumDirection.UP;
      } else if (this == TOP) {
         return EnumDirection.DOWN;
      } else if (this == NORTH) {
         return EnumDirection.SOUTH;
      } else if (this == SOUTH) {
         return EnumDirection.NORTH;
      } else {
         return this == WEST ? EnumDirection.EAST : EnumDirection.WEST;
      }
   }

   public String getDescriptor(boolean capitalized) {
      return capitalized ? StringHelper.capitalize(this.name().toLowerCase()) : this.name().toLowerCase();
   }
}
