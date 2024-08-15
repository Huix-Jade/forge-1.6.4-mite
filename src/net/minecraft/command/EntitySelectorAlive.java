package net.minecraft.command;

import net.minecraft.entity.Entity;

final class EntitySelectorAlive implements IEntitySelector {
   public boolean isEntityApplicable(Entity var1) {
      return var1.isEntityAlive();
   }
}
