package net.minecraft.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class MovingObjectPosition {
   private Object object_hit;
   public int blockX;
   public int blockY;
   public int blockZ;
   public int sideHit;
   public Vec3 position_hit;
   public World world;
   public double distance_to_object_hit = Double.MAX_VALUE;

   public MovingObjectPosition(World world, int x, int y, int z, int side_hit, Vec3 position_hit) {
      this.object_hit = world.getBlock(x, y, z);
      this.world = world;
      this.blockX = x;
      this.blockY = y;
      this.blockZ = z;
      this.sideHit = side_hit;
      this.position_hit = position_hit.myVec3LocalPool.getVecFromPool(position_hit.xCoord, position_hit.yCoord, position_hit.zCoord);
   }

   public MovingObjectPosition(Entity entity, double distance_to_entity) {
      this.object_hit = entity;
      this.world = entity.worldObj;
      this.position_hit = entity.worldObj.getWorldVec3Pool().getVecFromPool(entity.posX, entity.posY, entity.posZ);
      this.distance_to_object_hit = distance_to_entity;
   }

   public boolean isBlock() {
      return this.object_hit instanceof Block;
   }

   public boolean isBlockAt(int x, int y, int z) {
      if (!this.isBlock()) {
         return false;
      } else {
         return x == this.blockX && y == this.blockY && z == this.blockZ;
      }
   }

   public boolean isEntity() {
      return this.object_hit instanceof Entity;
   }

   public Block getBlockHit() {
      return this.isBlock() ? (Block)this.object_hit : null;
   }

   public Entity getEntityHit() {
      return this.isEntity() ? (Entity)this.object_hit : null;
   }
}
