package net.minecraft.util;

public class TentativeBoundingBox {
   int x;
   int y;
   int z;
   public AxisAlignedBB bb;
   public int countdown_for_clearing;

   public TentativeBoundingBox(int x, int y, int z, AxisAlignedBB bb) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.bb = bb;
      this.countdown_for_clearing = 20;
   }

   public boolean matches(int x, int y, int z) {
      return x == this.x && y == this.y && z == this.z;
   }
}
