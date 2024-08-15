package net.minecraft.entity.ai;

import java.util.Comparator;
import net.minecraft.entity.Entity;

public class EntityAINearestAttackableTargetSorter implements Comparator {
   private final Entity theEntity;

   public EntityAINearestAttackableTargetSorter(Entity var1) {
      this.theEntity = var1;
   }

   public int compareDistanceSq(Entity var1, Entity var2) {
      double var3 = this.theEntity.getDistanceSqToEntity(var1);
      double var5 = this.theEntity.getDistanceSqToEntity(var2);
      if (var3 < var5) {
         return -1;
      } else {
         return var3 > var5 ? 1 : 0;
      }
   }

   // $FF: synthetic method
   public int compare(Object var1, Object var2) {
      return this.compareDistanceSq((Entity)var1, (Entity)var2);
   }
}
