package net.minecraft.client.mco;

import argo.jdom.JsonNode;
import java.util.Date;
import net.minecraft.util.ValueObject;

public class Backup extends ValueObject {
   public String field_110727_a;
   public Date field_110725_b;
   public long field_110726_c;

   public static Backup func_110724_a(JsonNode var0) {
      Backup var1 = new Backup();

      try {
         var1.field_110727_a = var0.getStringValue(new Object[]{"backupId"});
         var1.field_110725_b = new Date(Long.parseLong(var0.getNumberValue(new Object[]{"lastModifiedDate"})));
         var1.field_110726_c = Long.parseLong(var0.getNumberValue(new Object[]{"size"}));
      } catch (IllegalArgumentException var3) {
      }

      return var1;
   }
}
