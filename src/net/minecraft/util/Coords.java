package net.minecraft.util;

public class Coords {
   public int x;
   public int y;
   public int z;

   public Coords() {
   }

   public Coords(int x, int y, int z) {
      this.set(x, y, z);
   }

   public Coords set(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
   }

   public boolean equals(int x, int y, int z) {
      return this.x == x && this.y == y && this.z == z;
   }
}
