package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import net.minecraft.client.resources.data.MetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class SimpleResource implements Resource {
   private final Map mapMetadataSections = Maps.newHashMap();
   private final ResourceLocation srResourceLocation;
   private final InputStream resourceInputStream;
   private final InputStream mcmetaInputStream;
   private final MetadataSerializer srMetadataSerializer;
   private boolean mcmetaJsonChecked;
   private JsonObject mcmetaJson;

   public SimpleResource(ResourceLocation var1, InputStream var2, InputStream var3, MetadataSerializer var4) {
      this.srResourceLocation = var1;
      this.resourceInputStream = var2;
      this.mcmetaInputStream = var3;
      this.srMetadataSerializer = var4;
   }

   public InputStream getInputStream() {
      return this.resourceInputStream;
   }

   public boolean hasMetadata() {
      return this.mcmetaInputStream != null;
   }

   public MetadataSection getMetadata(String var1) {
      if (!this.hasMetadata()) {
         return null;
      } else {
         if (this.mcmetaJson == null && !this.mcmetaJsonChecked) {
            this.mcmetaJsonChecked = true;
            BufferedReader var2 = null;

            try {
               var2 = new BufferedReader(new InputStreamReader(this.mcmetaInputStream));
               this.mcmetaJson = (new JsonParser()).parse(var2).getAsJsonObject();
            } finally {
               IOUtils.closeQuietly(var2);
            }
         }

         MetadataSection var6 = (MetadataSection)this.mapMetadataSections.get(var1);
         if (var6 == null) {
            var6 = this.srMetadataSerializer.parseMetadataSection(var1, this.mcmetaJson);
         }

         return var6;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof SimpleResource) {
         SimpleResource var2 = (SimpleResource)var1;
         return this.srResourceLocation != null ? this.srResourceLocation.equals(var2.srResourceLocation) : var2.srResourceLocation == null;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.srResourceLocation == null ? 0 : this.srResourceLocation.hashCode();
   }
}
