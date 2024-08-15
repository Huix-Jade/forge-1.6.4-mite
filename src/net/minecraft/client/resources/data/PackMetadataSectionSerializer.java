package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer {
   public PackMetadataSection func_110489_a(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      JsonObject var4 = var1.getAsJsonObject();
      String var5 = this.func_110486_a(var4.get("description"), "description", (String)null, 1, Integer.MAX_VALUE);
      int var6 = this.func_110485_a(var4.get("pack_format"), "pack_format", (Integer)null, 1, Integer.MAX_VALUE);
      return new PackMetadataSection(var5, var6);
   }

   public JsonElement func_110488_a(PackMetadataSection var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("pack_format", var1.getPackFormat());
      var4.addProperty("description", var1.getPackDescription());
      return var4;
   }

   public String getSectionName() {
      return "pack";
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      return this.func_110489_a(var1, var2, var3);
   }

   // $FF: synthetic method
   public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
      return this.func_110488_a((PackMetadataSection)var1, var2, var3);
   }
}
