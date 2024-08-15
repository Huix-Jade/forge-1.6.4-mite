package net.minecraft.entity.ai.attributes;

public abstract class BaseAttribute implements Attribute {
   private final String field_111115_a;
   private final double defaultValue;
   private boolean shouldWatch;

   protected BaseAttribute(String var1, double var2) {
      this.field_111115_a = var1;
      this.defaultValue = var2;
      if (var1 == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   public String getAttributeUnlocalizedName() {
      return this.field_111115_a;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public boolean getShouldWatch() {
      return this.shouldWatch;
   }

   public BaseAttribute setShouldWatch(boolean var1) {
      this.shouldWatch = var1;
      return this;
   }

   public int hashCode() {
      return this.field_111115_a.hashCode();
   }
}
