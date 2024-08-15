package net.minecraft.client.resources.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public abstract class BaseMetadataSectionSerializer implements MetadataSectionSerializer {
   protected float func_110487_a(JsonElement var1, String var2, Float var3, float var4, float var5) {
      var2 = this.getSectionName() + "->" + var2;
      if (var1 == null) {
         if (var3 == null) {
            throw new JsonParseException("Missing " + var2 + ": expected float");
         } else {
            return var3;
         }
      } else if (!var1.isJsonPrimitive()) {
         throw new JsonParseException("Invalid " + var2 + ": expected float, was " + var1);
      } else {
         try {
            float var6 = var1.getAsFloat();
            if (var6 < var4) {
               throw new JsonParseException("Invalid " + var2 + ": expected float >= " + var4 + ", was " + var6);
            } else if (var6 > var5) {
               throw new JsonParseException("Invalid " + var2 + ": expected float <= " + var5 + ", was " + var6);
            } else {
               return var6;
            }
         } catch (NumberFormatException var7) {
            throw new JsonParseException("Invalid " + var2 + ": expected float, was " + var1, var7);
         }
      }
   }

   protected int func_110485_a(JsonElement var1, String var2, Integer var3, int var4, int var5) {
      var2 = this.getSectionName() + "->" + var2;
      if (var1 == null) {
         if (var3 == null) {
            throw new JsonParseException("Missing " + var2 + ": expected int");
         } else {
            return var3;
         }
      } else if (!var1.isJsonPrimitive()) {
         throw new JsonParseException("Invalid " + var2 + ": expected int, was " + var1);
      } else {
         try {
            int var6 = var1.getAsInt();
            if (var6 < var4) {
               throw new JsonParseException("Invalid " + var2 + ": expected int >= " + var4 + ", was " + var6);
            } else if (var6 > var5) {
               throw new JsonParseException("Invalid " + var2 + ": expected int <= " + var5 + ", was " + var6);
            } else {
               return var6;
            }
         } catch (NumberFormatException var7) {
            throw new JsonParseException("Invalid " + var2 + ": expected int, was " + var1, var7);
         }
      }
   }

   protected String func_110486_a(JsonElement var1, String var2, String var3, int var4, int var5) {
      var2 = this.getSectionName() + "->" + var2;
      if (var1 == null) {
         if (var3 == null) {
            throw new JsonParseException("Missing " + var2 + ": expected string");
         } else {
            return var3;
         }
      } else if (!var1.isJsonPrimitive()) {
         throw new JsonParseException("Invalid " + var2 + ": expected string, was " + var1);
      } else {
         String var6 = var1.getAsString();
         if (var6.length() < var4) {
            throw new JsonParseException("Invalid " + var2 + ": expected string length >= " + var4 + ", was " + var6);
         } else if (var6.length() > var5) {
            throw new JsonParseException("Invalid " + var2 + ": expected string length <= " + var5 + ", was " + var6);
         } else {
            return var6;
         }
      }
   }

   protected boolean func_110484_a(JsonElement var1, String var2, Boolean var3) {
      var2 = this.getSectionName() + "->" + var2;
      if (var1 == null) {
         if (var3 == null) {
            throw new JsonParseException("Missing " + var2 + ": expected boolean");
         } else {
            return var3;
         }
      } else if (!var1.isJsonPrimitive()) {
         throw new JsonParseException("Invalid " + var2 + ": expected boolean, was " + var1);
      } else {
         boolean var4 = var1.getAsBoolean();
         return var4;
      }
   }
}
