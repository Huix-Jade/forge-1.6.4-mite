package net.minecraft.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.world.World;

public class MovingObjectPosition extends RaycastCollision {

   public MovingObjectPosition(Raycast raycast, Entity entity_hit, AABBIntercept intercept) {
      super(raycast, entity_hit, intercept);
   }
}
