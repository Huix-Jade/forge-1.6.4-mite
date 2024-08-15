package net.minecraft.client.resources;

import com.google.common.base.Function;

class SimpleReloadableResourceManagerINNER1 implements Function {
   // $FF: synthetic field
   final SimpleReloadableResourceManager theSimpleReloadableResourceManager;

   SimpleReloadableResourceManagerINNER1(SimpleReloadableResourceManager var1) {
      this.theSimpleReloadableResourceManager = var1;
   }

   public String apply(ResourcePack var1) {
      return var1.getPackName();
   }

   // $FF: synthetic method
   public Object apply(Object var1) {
      return this.apply((ResourcePack)var1);
   }
}
