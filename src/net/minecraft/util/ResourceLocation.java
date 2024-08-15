package net.minecraft.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.Validate;

public final class ResourceLocation {
   private final String resourceDomain;
   private final String resourcePath;
   private static List resources_to_verify = new ArrayList();
   public boolean generate_encoded_file;

   public ResourceLocation(String par1Str, String par2Str) {
      this(par1Str, par2Str, true);
   }

   public ResourceLocation(String par1Str, String par2Str, boolean verify) {
      Validate.notNull(par2Str);
      if (par1Str != null && par1Str.length() != 0) {
         this.resourceDomain = par1Str;
      } else {
         this.resourceDomain = "minecraft";
      }

      this.resourcePath = par2Str;
      if (verify) {
         this.setVerificationPending();
      }

   }

   public ResourceLocation(String par1Str) {
      this(par1Str, true);
   }

   public ResourceLocation(String par1Str, boolean verify) {
      String var2 = "minecraft";
      String var3 = par1Str;
      int var4 = par1Str.indexOf(58);
      if (var4 >= 0) {
         var3 = par1Str.substring(var4 + 1, par1Str.length());
         if (var4 > 1) {
            var2 = par1Str.substring(0, var4);
         }
      }

      this.resourceDomain = var2.toLowerCase();
      this.resourcePath = var3;
      if (verify) {
         this.setVerificationPending();
      }

   }

   private void setVerificationPending() {
      if (!this.getResourcePath().endsWith(".mcmeta")) {
         resources_to_verify.add(this);
      }

   }

   public void verifyExistence() {
      if (!this.exists()) {
         Minecraft.setErrorMessage("Resource not found: " + this.getResourcePath());
      }

   }

   public boolean exists() {
      if (Minecraft.theMinecraft == null) {
         Minecraft.setErrorMessage("ResourceLocation.exists: theMinecraft==null, checking too early");
         return false;
      } else if (Minecraft.theMinecraft.mcDefaultResourcePack == null) {
         Minecraft.setErrorMessage("ResourceLocation.exists: mcDefaultResourcePack==null, checking too early");
         return false;
      } else if (Minecraft.theMinecraft.mcDefaultResourcePack.resourceExists(this)) {
         return true;
      } else {
         return Minecraft.MITE_resource_pack != null && Minecraft.MITE_resource_pack.resourceExists(this);
      }
   }

   public static void verifyResourceLocations() {
      int num_resources = resources_to_verify.size();

      for(int i = 0; i < num_resources; ++i) {
         ResourceLocation resource = (ResourceLocation)resources_to_verify.get(i);
         resource.verifyExistence();
      }

      resources_to_verify.clear();
   }

   public String getResourcePath() {
      return this.resourcePath;
   }

   public String getResourceDomain() {
      return this.resourceDomain;
   }

   public String toString() {
      return this.resourceDomain + ":" + this.resourcePath;
   }

   public boolean equals(Object par1Obj) {
      if (this == par1Obj) {
         return true;
      } else if (!(par1Obj instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation var2 = (ResourceLocation)par1Obj;
         return this.resourceDomain.equals(var2.resourceDomain) && this.resourcePath.equals(var2.resourcePath);
      }
   }

   public int hashCode() {
      return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
   }

   public ResourceLocation setGenerateEncodedFile(boolean generate_encoded_file) {
      this.generate_encoded_file = generate_encoded_file;
      return this;
   }
}
