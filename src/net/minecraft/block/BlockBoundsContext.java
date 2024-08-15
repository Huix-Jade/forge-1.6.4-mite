package net.minecraft.block;

import net.minecraft.entity.Entity;

public final class BlockBoundsContext {
   public static final int FOR_ENTITY_COLLISION = 1;
   public static final int FOR_RAYCAST = 2;
   public static final int FOR_PLAYER_SELECTION = 4;
   private final int context;
   public final Entity entity;

   public BlockBoundsContext(int context, Entity entity) {
      if (context == 4) {
         context |= 2;
      }

      this.context = context;
      this.entity = entity;
   }

   public boolean isForEntityCollision() {
      return BitHelper.isBitSet(this.context, 1);
   }

   public boolean isForRaycast() {
      return BitHelper.isBitSet(this.context, 2);
   }

   public boolean isForPlayerSelection() {
      return BitHelper.isBitSet(this.context, 4);
   }
}
