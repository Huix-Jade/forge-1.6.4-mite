package net.minecraft.dispenser;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class RegistrySimple implements IRegistry {
   protected final Map registryObjects = this.func_111054_a();

   protected HashMap func_111054_a() {
      return Maps.newHashMap();
   }

   public Object getObject(Object var1) {
      return this.registryObjects.get(var1);
   }

   public void putObject(Object var1, Object var2) {
      this.registryObjects.put(var1, var2);
   }
}
