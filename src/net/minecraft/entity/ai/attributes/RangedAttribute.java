package net.minecraft.entity.ai.attributes;

public class RangedAttribute extends BaseAttribute {
   private final double minimumValue;
   private final double maximumValue;
   private String field_111119_c;

   public RangedAttribute(String var1, double var2, double var4, double var6) {
      super(var1, var2);
      this.minimumValue = var4;
      this.maximumValue = var6;
      if (var4 > var6) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      } else if (var2 < var4) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      } else if (var2 > var6) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public RangedAttribute func_111117_a(String var1) {
      this.field_111119_c = var1;
      return this;
   }

   public String func_111116_f() {
      return this.field_111119_c;
   }

   public double clampValue(double var1) {
      if (var1 < this.minimumValue) {
         var1 = this.minimumValue;
      }

      if (var1 > this.maximumValue) {
         var1 = this.maximumValue;
      }

      return var1;
   }
}
