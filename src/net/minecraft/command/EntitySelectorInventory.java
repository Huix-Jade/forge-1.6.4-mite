package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;

final class EntitySelectorInventory implements IEntitySelector {
   public boolean isEntityApplicable(Entity var1) {
      return var1 instanceof IInventory && var1.isEntityAlive();
   }
}
