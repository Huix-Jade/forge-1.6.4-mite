package net.minecraft.network.packet;

import java.util.Collection;

public class Packet44UpdateAttributesSnapshot {
   private final String field_142043_b;
   private final double field_142044_c;
   private final Collection field_142042_d;
   // $FF: synthetic field
   final Packet44UpdateAttributes field_142045_a;

   public Packet44UpdateAttributesSnapshot(Packet44UpdateAttributes var1, String var2, double var3, Collection var5) {
      this.field_142045_a = var1;
      this.field_142043_b = var2;
      this.field_142044_c = var3;
      this.field_142042_d = var5;
   }

   public String func_142040_a() {
      return this.field_142043_b;
   }

   public double func_142041_b() {
      return this.field_142044_c;
   }

   public Collection func_142039_c() {
      return this.field_142042_d;
   }
}
