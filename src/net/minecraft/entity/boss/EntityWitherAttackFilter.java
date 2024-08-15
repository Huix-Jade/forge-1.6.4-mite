package net.minecraft.entity.boss;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;

final class EntityWitherAttackFilter implements IEntitySelector {
   public boolean isEntityApplicable(Entity var1) {
      return var1 instanceof EntityLivingBase && ((EntityLivingBase)var1).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
   }
}
