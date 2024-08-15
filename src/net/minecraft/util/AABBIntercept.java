package net.minecraft.util;

public final class AABBIntercept {
   public final Vec3 position_hit;
   public final EnumFace face_hit;

   public AABBIntercept(Vec3 position_hit, EnumFace face_hit) {
      this.position_hit = position_hit;
      this.face_hit = face_hit;
   }

   public String toString() {
      return this.position_hit.toString() + ", face=" + this.face_hit;
   }
}
