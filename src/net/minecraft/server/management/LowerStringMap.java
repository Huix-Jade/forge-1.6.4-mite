package net.minecraft.server.management;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LowerStringMap implements Map {
   private final Map internalMap = new LinkedHashMap();

   public int size() {
      return this.internalMap.size();
   }

   public boolean isEmpty() {
      return this.internalMap.isEmpty();
   }

   public boolean containsKey(Object var1) {
      return this.internalMap.containsKey(var1.toString().toLowerCase());
   }

   public boolean containsValue(Object var1) {
      return this.internalMap.containsKey(var1);
   }

   public Object get(Object var1) {
      return this.internalMap.get(var1.toString().toLowerCase());
   }

   public Object putLower(String var1, Object var2) {
      return this.internalMap.put(var1.toLowerCase(), var2);
   }

   public Object remove(Object var1) {
      return this.internalMap.remove(var1.toString().toLowerCase());
   }

   public void putAll(Map var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         this.putLower((String)var3.getKey(), var3.getValue());
      }

   }

   public void clear() {
      this.internalMap.clear();
   }

   public Set keySet() {
      return this.internalMap.keySet();
   }

   public Collection values() {
      return this.internalMap.values();
   }

   public Set entrySet() {
      return this.internalMap.entrySet();
   }

   // $FF: synthetic method
   public Object put(Object var1, Object var2) {
      return this.putLower((String)var1, var2);
   }
}
