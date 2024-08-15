package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer {
   public TextureMetadataSection func_110494_a(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      JsonObject var4 = var1.getAsJsonObject();
      boolean var5 = this.func_110484_a(var4.get("blur"), "blur", false);
      boolean var6 = this.func_110484_a(var4.get("clamp"), "clamp", false);
      return new TextureMetadataSection(var5, var6);
   }

   public String getSectionName() {
      return "texture";
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      return this.func_110494_a(var1, var2, var3);
   }
}
