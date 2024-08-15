package net.minecraft.client.resources.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.dispenser.IRegistry;
import net.minecraft.dispenser.RegistrySimple;

public class MetadataSerializer {
   private final IRegistry metadataSectionSerializerRegistry = new RegistrySimple();
   private final GsonBuilder gsonBuilder = new GsonBuilder();
   private Gson gson;

   public void registerMetadataSectionType(MetadataSectionSerializer var1, Class var2) {
      this.metadataSectionSerializerRegistry.putObject(var1.getSectionName(), new MetadataSerializerRegistration(this, var1, var2, (MetadataSerializerEmptyAnon)null));
      this.gsonBuilder.registerTypeAdapter(var2, var1);
      this.gson = null;
   }

   public MetadataSection parseMetadataSection(String var1, JsonObject var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("Metadata section name cannot be null");
      } else if (!var2.has(var1)) {
         return null;
      } else if (!var2.get(var1).isJsonObject()) {
         throw new IllegalArgumentException("Invalid metadata for '" + var1 + "' - expected object, found " + var2.get(var1));
      } else {
         MetadataSerializerRegistration var3 = (MetadataSerializerRegistration)this.metadataSectionSerializerRegistry.getObject(var1);
         if (var3 == null) {
            throw new IllegalArgumentException("Don't know how to handle metadata section '" + var1 + "'");
         } else {
            return (MetadataSection)this.getGson().fromJson(var2.getAsJsonObject(var1), var3.field_110500_b);
         }
      }
   }

   private Gson getGson() {
      if (this.gson == null) {
         this.gson = this.gsonBuilder.create();
      }

      return this.gson;
   }
}
