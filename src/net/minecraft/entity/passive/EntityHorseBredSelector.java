package net.minecraft.entity.passive;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

final class EntityHorseBredSelector implements IEntitySelector {
   public boolean isEntityApplicable(Entity var1) {
      return var1 instanceof EntityHorse && ((EntityHorse)var1).func_110205_ce();
   }
}
