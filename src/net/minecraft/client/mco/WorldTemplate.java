package net.minecraft.client.mco;

import argo.jdom.JsonNode;
import net.minecraft.util.ValueObject;

public class WorldTemplate extends ValueObject {
   public String field_110734_a;
   public String field_110732_b;
   public String field_110733_c;
   public String field_110731_d;

   public static WorldTemplate func_110730_a(JsonNode var0) {
      WorldTemplate var1 = new WorldTemplate();

      try {
         var1.field_110734_a = var0.getNumberValue(new Object[]{"id"});
         var1.field_110732_b = var0.getStringValue(new Object[]{"name"});
         var1.field_110733_c = var0.getStringValue(new Object[]{"version"});
         var1.field_110731_d = var0.getStringValue(new Object[]{"author"});
      } catch (IllegalArgumentException var3) {
      }

      return var1;
   }
}
