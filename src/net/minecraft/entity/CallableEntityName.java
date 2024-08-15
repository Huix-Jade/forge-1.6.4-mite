package net.minecraft.entity;

import java.util.concurrent.Callable;

class CallableEntityName implements Callable {
   // $FF: synthetic field
   final Entity theEntity;

   CallableEntityName(Entity var1) {
      this.theEntity = var1;
   }

   public String callEntityName() {
      return this.theEntity.getEntityName();
   }

   // $FF: synthetic method
   public Object call() {
      return this.callEntityName();
   }
}
