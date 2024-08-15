package net.minecraft.entity.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

class EntityAINearestAttackableTargetSelector implements IEntitySelector {
   // $FF: synthetic field
   final IEntitySelector field_111103_c;
   // $FF: synthetic field
   final EntityAINearestAttackableTarget field_111102_d;

   EntityAINearestAttackableTargetSelector(EntityAINearestAttackableTarget var1, IEntitySelector var2) {
      this.field_111102_d = var1;
      this.field_111103_c = var2;
   }

   public boolean isEntityApplicable(Entity var1) {
      if (!(var1 instanceof EntityLivingBase)) {
         return false;
      } else {
         return this.field_111103_c != null && !this.field_111103_c.isEntityApplicable(var1) ? false : this.field_111102_d.isSuitableTarget((EntityLivingBase)var1, false);
      }
   }
}
