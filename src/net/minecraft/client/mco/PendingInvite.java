package net.minecraft.client.mco;

import argo.jdom.JsonNode;
import net.minecraft.util.ValueObject;

public class PendingInvite extends ValueObject {
   public String field_130094_a;
   public String field_130092_b;
   public String field_130093_c;

   public static PendingInvite func_130091_a(JsonNode var0) {
      PendingInvite var1 = new PendingInvite();

      try {
         var1.field_130094_a = var0.getStringValue(new Object[]{"invitationId"});
         var1.field_130092_b = var0.getStringValue(new Object[]{"worldName"});
         var1.field_130093_c = var0.getStringValue(new Object[]{"worldOwnerName"});
      } catch (Exception var3) {
      }

      return var1;
   }
}
