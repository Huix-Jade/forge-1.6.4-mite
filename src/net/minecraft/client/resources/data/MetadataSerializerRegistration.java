package net.minecraft.client.resources.data;

class MetadataSerializerRegistration {
   final MetadataSectionSerializer field_110502_a;
   final Class field_110500_b;
   // $FF: synthetic field
   final MetadataSerializer field_110501_c;

   private MetadataSerializerRegistration(MetadataSerializer var1, MetadataSectionSerializer var2, Class var3) {
      this.field_110501_c = var1;
      this.field_110502_a = var2;
      this.field_110500_b = var3;
   }

   // $FF: synthetic method
   MetadataSerializerRegistration(MetadataSerializer var1, MetadataSectionSerializer var2, Class var3, MetadataSerializerEmptyAnon var4) {
      this(var1, var2, var3);
   }
}
