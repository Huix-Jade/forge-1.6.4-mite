package net.minecraft.entity;

import java.util.concurrent.Callable;

class CallableEntityType implements Callable {
   // $FF: synthetic field
   final Entity theEntity;

   CallableEntityType(Entity var1) {
      this.theEntity = var1;
   }

   public String callEntityType() {
      return EntityList.getEntityString(this.theEntity) + " (" + this.theEntity.getClass().getCanonicalName() + ")";
   }

   // $FF: synthetic method
   public Object call() {
      return this.callEntityType();
   }
}
