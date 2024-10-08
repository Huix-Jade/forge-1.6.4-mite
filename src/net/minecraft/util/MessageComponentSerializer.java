package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class MessageComponentSerializer implements JsonDeserializer, JsonSerializer {
   public ChatMessageComponent deserializeComponent(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      ChatMessageComponent var4 = new ChatMessageComponent();
      JsonObject var5 = (JsonObject)var1;
      JsonElement var6 = var5.get("text");
      JsonElement var7 = var5.get("translate");
      JsonElement var8 = var5.get("color");
      JsonElement var9 = var5.get("bold");
      JsonElement var10 = var5.get("italic");
      JsonElement var11 = var5.get("underlined");
      JsonElement var12 = var5.get("obfuscated");
      if (var8 != null && var8.isJsonPrimitive()) {
         EnumChatFormatting var13 = EnumChatFormatting.func_96300_b(var8.getAsString());
         if (var13 == null || !var13.isColor()) {
            throw new JsonParseException("Given color (" + var8.getAsString() + ") is not a valid selection");
         }

         var4.setColor(var13);
      }

      if (var9 != null && var9.isJsonPrimitive()) {
         var4.setBold(var9.getAsBoolean());
      }

      if (var10 != null && var10.isJsonPrimitive()) {
         var4.setItalic(var10.getAsBoolean());
      }

      if (var11 != null && var11.isJsonPrimitive()) {
         var4.setUnderline(var11.getAsBoolean());
      }

      if (var12 != null && var12.isJsonPrimitive()) {
         var4.setObfuscated(var12.getAsBoolean());
      }

      if (var6 != null) {
         if (var6.isJsonArray()) {
            JsonArray var17 = var6.getAsJsonArray();
            Iterator var14 = var17.iterator();

            while(var14.hasNext()) {
               JsonElement var15 = (JsonElement)var14.next();
               if (var15.isJsonPrimitive()) {
                  var4.addText(var15.getAsString());
               } else if (var15.isJsonObject()) {
                  var4.appendComponent(this.deserializeComponent(var15, var2, var3));
               }
            }
         } else if (var6.isJsonPrimitive()) {
            var4.addText(var6.getAsString());
         }
      } else if (var7 != null && var7.isJsonPrimitive()) {
         JsonElement var18 = var5.get("using");
         if (var18 != null) {
            if (var18.isJsonArray()) {
               ArrayList var19 = Lists.newArrayList();
               Iterator var20 = var18.getAsJsonArray().iterator();

               while(var20.hasNext()) {
                  JsonElement var16 = (JsonElement)var20.next();
                  if (var16.isJsonPrimitive()) {
                     var19.add(var16.getAsString());
                  } else if (var16.isJsonObject()) {
                     var19.add(this.deserializeComponent(var16, var2, var3));
                  }
               }

               var4.addFormatted(var7.getAsString(), var19.toArray());
            } else if (var18.isJsonPrimitive()) {
               var4.addFormatted(var7.getAsString(), var18.getAsString());
            }
         } else {
            var4.addKey(var7.getAsString());
         }
      }

      return var4;
   }

   public JsonElement serializeComponent(ChatMessageComponent var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      if (var1.getColor() != null) {
         var4.addProperty("color", var1.getColor().func_96297_d());
      }

      if (var1.isBold() != null) {
         var4.addProperty("bold", var1.isBold());
      }

      if (var1.isItalic() != null) {
         var4.addProperty("italic", var1.isItalic());
      }

      if (var1.isUnderline() != null) {
         var4.addProperty("underlined", var1.isUnderline());
      }

      if (var1.isObfuscated() != null) {
         var4.addProperty("obfuscated", var1.isObfuscated());
      }

      if (var1.getText() != null) {
         var4.addProperty("text", var1.getText());
      } else if (var1.getTranslationKey() != null) {
         var4.addProperty("translate", var1.getTranslationKey());
         if (var1.getSubComponents() != null && !var1.getSubComponents().isEmpty()) {
            var4.add("using", this.serializeComponentChildren(var1, var2, var3));
         }
      } else if (var1.getSubComponents() != null && !var1.getSubComponents().isEmpty()) {
         var4.add("text", this.serializeComponentChildren(var1, var2, var3));
      }

      return var4;
   }

   private JsonArray serializeComponentChildren(ChatMessageComponent var1, Type var2, JsonSerializationContext var3) {
      JsonArray var4 = new JsonArray();
      Iterator var5 = var1.getSubComponents().iterator();

      while(var5.hasNext()) {
         ChatMessageComponent var6 = (ChatMessageComponent)var5.next();
         if (var6.getText() != null) {
            var4.add(new JsonPrimitive(var6.getText()));
         } else {
            var4.add(this.serializeComponent(var6, var2, var3));
         }
      }

      return var4;
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      return this.deserializeComponent(var1, var2, var3);
   }

   // $FF: synthetic method
   public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
      return this.serializeComponent((ChatMessageComponent)var1, var2, var3);
   }
}
