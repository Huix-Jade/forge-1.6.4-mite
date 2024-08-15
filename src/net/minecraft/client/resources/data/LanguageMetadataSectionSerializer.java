package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.resources.Language;

public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer {
   public LanguageMetadataSection func_135020_a(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      JsonObject var4 = var1.getAsJsonObject();
      HashSet var5 = Sets.newHashSet();
      Iterator var6 = var4.entrySet().iterator();

      String var8;
      String var11;
      String var12;
      boolean var13;
      do {
         if (!var6.hasNext()) {
            return new LanguageMetadataSection(var5);
         }

         Map.Entry var7 = (Map.Entry)var6.next();
         var8 = (String)var7.getKey();
         JsonElement var9 = (JsonElement)var7.getValue();
         if (!var9.isJsonObject()) {
            throw new JsonParseException("Invalid language->'" + var8 + "': expected object, was " + var9);
         }

         JsonObject var10 = var9.getAsJsonObject();
         var11 = this.func_110486_a(var10.get("region"), "region", "", 0, Integer.MAX_VALUE);
         var12 = this.func_110486_a(var10.get("name"), "name", "", 0, Integer.MAX_VALUE);
         var13 = this.func_110484_a(var10.get("bidirectional"), "bidirectional", false);
         if (var11.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var8 + "'->region: empty value");
         }

         if (var12.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var8 + "'->name: empty value");
         }
      } while(var5.add(new Language(var8, var11, var12, var13)));

      throw new JsonParseException("Duplicate language->'" + var8 + "' defined");
   }

   public String getSectionName() {
      return "language";
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      return this.func_135020_a(var1, var2, var3);
   }
}
