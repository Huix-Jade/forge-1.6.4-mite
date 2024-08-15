package net.minecraft.entity.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

class EntityAIAvoidEntitySelector implements IEntitySelector {
   // $FF: synthetic field
   final EntityAIAvoidEntity entityAvoiderAI;

   EntityAIAvoidEntitySelector(EntityAIAvoidEntity var1) {
      this.entityAvoiderAI = var1;
   }

   public boolean isEntityApplicable(Entity var1) {
      return var1.isEntityAlive() && EntityAIAvoidEntity.func_98217_a(this.entityAvoiderAI).getEntitySenses().canSee(var1);
   }
}
