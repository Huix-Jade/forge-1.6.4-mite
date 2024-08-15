package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer {
   public AnimationMetadataSection func_110493_a(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      ArrayList var4 = Lists.newArrayList();
      JsonObject var5 = (JsonObject)var1;
      int var6 = this.func_110485_a(var5.get("frametime"), "frametime", 1, 1, Integer.MAX_VALUE);
      int var8;
      if (var5.has("frames")) {
         try {
            JsonArray var7 = var5.getAsJsonArray("frames");

            for(var8 = 0; var8 < var7.size(); ++var8) {
               JsonElement var9 = var7.get(var8);
               AnimationFrame var10 = this.parseAnimationFrame(var8, var9);
               if (var10 != null) {
                  var4.add(var10);
               }
            }
         } catch (ClassCastException var11) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + var5.get("frames"), var11);
         }
      }

      int var12 = this.func_110485_a(var5.get("width"), "width", -1, 1, Integer.MAX_VALUE);
      var8 = this.func_110485_a(var5.get("height"), "height", -1, 1, Integer.MAX_VALUE);
      return new AnimationMetadataSection(var4, var12, var8, var6);
   }

   private AnimationFrame parseAnimationFrame(int var1, JsonElement var2) {
      if (var2.isJsonPrimitive()) {
         try {
            return new AnimationFrame(var2.getAsInt());
         } catch (NumberFormatException var6) {
            throw new JsonParseException("Invalid animation->frames->" + var1 + ": expected number, was " + var2, var6);
         }
      } else if (var2.isJsonObject()) {
         JsonObject var3 = var2.getAsJsonObject();
         int var4 = this.func_110485_a(var3.get("time"), "frames->" + var1 + "->time", -1, 1, Integer.MAX_VALUE);
         int var5 = this.func_110485_a(var3.get("index"), "frames->" + var1 + "->index", (Integer)null, 0, Integer.MAX_VALUE);
         return new AnimationFrame(var5, var4);
      } else {
         return null;
      }
   }

   public JsonElement func_110491_a(AnimationMetadataSection var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("frametime", var1.getFrameTime());
      if (var1.getFrameWidth() != -1) {
         var4.addProperty("width", var1.getFrameWidth());
      }

      if (var1.getFrameHeight() != -1) {
         var4.addProperty("height", var1.getFrameHeight());
      }

      if (var1.getFrameCount() > 0) {
         JsonArray var5 = new JsonArray();

         for(int var6 = 0; var6 < var1.getFrameCount(); ++var6) {
            if (var1.frameHasTime(var6)) {
               JsonObject var7 = new JsonObject();
               var7.addProperty("index", var1.getFrameIndex(var6));
               var7.addProperty("time", var1.getFrameTimeSingle(var6));
               var5.add(var7);
            } else {
               var5.add(new JsonPrimitive(var1.getFrameIndex(var6)));
            }
         }

         var4.add("frames", var5);
      }

      return var4;
   }

   public String getSectionName() {
      return "animation";
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      return this.func_110493_a(var1, var2, var3);
   }

   // $FF: synthetic method
   public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
      return this.func_110491_a((AnimationMetadataSection)var1, var2, var3);
   }
}
