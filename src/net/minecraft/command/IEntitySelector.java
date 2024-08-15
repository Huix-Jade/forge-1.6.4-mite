package net.minecraft.command;

import net.minecraft.entity.Entity;

public interface IEntitySelector {
   IEntitySelector selectAnything = new EntitySelectorAlive();
   IEntitySelector selectInventories = new EntitySelectorInventory();

   boolean isEntityApplicable(Entity var1);
}
