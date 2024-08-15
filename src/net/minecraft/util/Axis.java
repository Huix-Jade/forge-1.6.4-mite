package net.minecraft.util;

public enum Axis {
   UP_DOWN,
   NORTH_SOUTH,
   EAST_WEST;

   public boolean isUpDown() {
      return this == UP_DOWN;
   }

   boolean isNorthSouth() {
      return this == NORTH_SOUTH;
   }

   boolean isEastWest() {
      return this == EAST_WEST;
   }
}
