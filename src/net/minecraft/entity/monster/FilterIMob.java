package net.minecraft.entity.monster;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

final class FilterIMob implements IEntitySelector {
   public boolean isEntityApplicable(Entity var1) {
      return var1 instanceof IMob;
   }
}
