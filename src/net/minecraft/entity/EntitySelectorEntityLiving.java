package net.minecraft.entity;

import net.minecraft.command.IEntitySelector;

final class EntitySelectorEntityLiving implements IEntitySelector {
   boolean must_be_alive;
   boolean must_use_new_AI;

   public EntitySelectorEntityLiving(boolean must_be_alive, boolean must_use_new_AI) {
      this.must_be_alive = must_be_alive;
      this.must_use_new_AI = must_use_new_AI;
   }

   public boolean isEntityApplicable(Entity entity) {
      if (entity.isDead) {
         return false;
      } else if (!(entity instanceof EntityLiving)) {
         return false;
      } else {
         EntityLiving entity_living = entity.getAsEntityLiving();
         if (this.must_be_alive && entity_living.getHealth() <= 0.0F) {
            return false;
         } else {
            return !this.must_use_new_AI || entity_living.isAIEnabled();
         }
      }
   }
}
