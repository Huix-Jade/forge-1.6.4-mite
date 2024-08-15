package net.minecraft.client.resources.data;

public class PackMetadataSection implements MetadataSection {
   private final String packDescription;
   private final int packFormat;

   public PackMetadataSection(String var1, int var2) {
      this.packDescription = var1;
      this.packFormat = var2;
   }

   public String getPackDescription() {
      return this.packDescription;
   }

   public int getPackFormat() {
      return this.packFormat;
   }
}
