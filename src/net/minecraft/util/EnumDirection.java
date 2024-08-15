package net.minecraft.util;

import net.minecraft.client.Minecraft;

public enum EnumDirection {
   UP(0, 1, 0),
   DOWN(0, -1, 0),
   SOUTH(0, 0, 1),
   NORTH(0, 0, -1),
   EAST(1, 0, 0),
   WEST(-1, 0, 0);

   public final int dx;
   public final int dy;
   public final int dz;

   private EnumDirection(int dx, int dy, int dz) {
      this.dx = dx;
      this.dy = dy;
      this.dz = dz;
   }

   public static EnumDirection get(int ordinal) {
      return values()[ordinal];
   }

   public static EnumDirection getDirectionFromYaw(float yaw) {
      int direction = MathHelper.floor_double((double)(yaw * 4.0F / 360.0F) + 0.5) & 3;
      if (direction == 0) {
         return SOUTH;
      } else if (direction == 1) {
         return WEST;
      } else if (direction == 2) {
         return NORTH;
      } else if (direction == 3) {
         return EAST;
      } else {
         Minecraft.setErrorMessage("getDirectionFromYaw: invalid result");
         return null;
      }
   }

   static float getNormalizedYaw(float yaw) {
      while(yaw < 0.0F) {
         yaw += 360.0F;
      }

      while(yaw >= 360.0F) {
         yaw -= 360.0F;
      }

      return yaw;
   }

   public static boolean doesYawHaveNorthComponent(float yaw) {
      yaw = getNormalizedYaw(yaw);
      return yaw > 90.0F && yaw < 270.0F;
   }

   public static boolean doesYawHaveSouthComponent(float yaw) {
      yaw = getNormalizedYaw(yaw);
      return yaw > 270.0F || yaw < 90.0F;
   }

   public static boolean doesYawHaveWestComponent(float yaw) {
      yaw = getNormalizedYaw(yaw);
      return yaw > 0.0F && yaw < 180.0F;
   }

   public static boolean doesYawHaveEastComponent(float yaw) {
      yaw = getNormalizedYaw(yaw);
      return yaw > 180.0F && yaw < 360.0F;
   }

   public static EnumDirection getDirectionFromPitch(float pitch) {
      return pitch < 0.0F ? UP : DOWN;
   }

   public final boolean isUp() {
      return this == UP;
   }

   public final boolean isDown() {
      return this == DOWN;
   }

   public final boolean isSouth() {
      return this == SOUTH;
   }

   public final boolean isNorth() {
      return this == NORTH;
   }

   public final boolean isEast() {
      return this == EAST;
   }

   public final boolean isWest() {
      return this == WEST;
   }

   public final boolean isUpOrDown() {
      return this == UP || this == DOWN;
   }

   public final boolean isHorizontal() {
      return this == SOUTH || this == NORTH || this == EAST || this == WEST;
   }

   public final boolean isNorthOrSouth() {
      return this == NORTH || this == SOUTH;
   }

   public final boolean isEastOrWest() {
      return this == EAST || this == WEST;
   }

   public final EnumDirection getOpposite() {
      if (this == UP) {
         return DOWN;
      } else if (this == DOWN) {
         return UP;
      } else if (this == SOUTH) {
         return NORTH;
      } else if (this == NORTH) {
         return SOUTH;
      } else {
         return this == EAST ? WEST : EAST;
      }
   }

   public final EnumFace getFace() {
      if (this == UP) {
         return EnumFace.TOP;
      } else if (this == DOWN) {
         return EnumFace.BOTTOM;
      } else if (this == SOUTH) {
         return EnumFace.SOUTH;
      } else if (this == NORTH) {
         return EnumFace.NORTH;
      } else {
         return this == EAST ? EnumFace.EAST : EnumFace.WEST;
      }
   }

   public final EnumFace getOppositeFace() {
      if (this == UP) {
         return EnumFace.BOTTOM;
      } else if (this == DOWN) {
         return EnumFace.TOP;
      } else if (this == SOUTH) {
         return EnumFace.NORTH;
      } else if (this == NORTH) {
         return EnumFace.SOUTH;
      } else {
         return this == EAST ? EnumFace.WEST : EnumFace.EAST;
      }
   }

   public final int getNeighborX(int x) {
      return this == WEST ? x - 1 : (this == EAST ? x + 1 : x);
   }

   public final int getNeighborY(int y) {
      return this == DOWN ? y - 1 : (this == UP ? y + 1 : y);
   }

   public final int getNeighborZ(int z) {
      return this == NORTH ? z - 1 : (this == SOUTH ? z + 1 : z);
   }

   public final EnumDirection adjustForCoordBaseMode(int coord_base_mode) {
      if (coord_base_mode == 0) {
         return this.isNorthOrSouth() ? this.getOpposite() : this;
      } else if (coord_base_mode == 1) {
         return this.isNorth() ? WEST : (this.isSouth() ? EAST : (this.isWest() ? NORTH : (this.isEast() ? SOUTH : this)));
      } else if (coord_base_mode == 2) {
         return this;
      } else if (coord_base_mode == 3) {
         return this.isNorth() ? EAST : (this.isSouth() ? WEST : (this.isWest() ? NORTH : (this.isEast() ? SOUTH : this)));
      } else {
         Minecraft.setErrorMessage("adjustForCoordBaseMode: invalid coord_base_mode " + coord_base_mode);
         return this;
      }
   }

   public String getDescriptor(boolean capitalized) {
      return capitalized ? StringHelper.capitalize(this.name().toLowerCase()) : this.name().toLowerCase();
   }

   public boolean isAlignedWith(Axis axis) {
      return this.isUpOrDown() ? axis.isUpDown() : (this.isNorthOrSouth() ? axis.isNorthSouth() : axis.isEastWest());
   }
}
