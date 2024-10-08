package net.minecraft.client.mco;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ValueObject;

public class PendingInvitesList extends ValueObject {
   public List field_130096_a = Lists.newArrayList();

   public static PendingInvitesList func_130095_a(String var0) {
      PendingInvitesList var1 = new PendingInvitesList();

      try {
         JsonRootNode var2 = (new JdomParser()).parse(var0);
         if (var2.isArrayNode(new Object[]{"invites"})) {
            Iterator var3 = var2.getArrayNode(new Object[]{"invites"}).iterator();

            while(var3.hasNext()) {
               JsonNode var4 = (JsonNode)var3.next();
               var1.field_130096_a.add(PendingInvite.func_130091_a(var4));
            }
         }
      } catch (InvalidSyntaxException var5) {
      }

      return var1;
   }
}
