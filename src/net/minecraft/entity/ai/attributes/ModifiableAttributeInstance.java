package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements AttributeInstance {
   private final BaseAttributeMap field_111138_a;
   private final Attribute field_111136_b;
   private final Map field_111137_c = Maps.newHashMap();
   private final Map field_111134_d = Maps.newHashMap();
   private final Map field_111135_e = Maps.newHashMap();
   private double baseValue;
   private boolean field_111133_g = true;
   private double field_111139_h;

   public ModifiableAttributeInstance(BaseAttributeMap var1, Attribute var2) {
      this.field_111138_a = var1;
      this.field_111136_b = var2;
      this.baseValue = var2.getDefaultValue();

      for(int var3 = 0; var3 < 3; ++var3) {
         this.field_111137_c.put(var3, new HashSet());
      }

   }

   public Attribute func_111123_a() {
      return this.field_111136_b;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setAttribute(double var1) {
      if (var1 != this.getBaseValue()) {
         this.baseValue = var1;
         this.func_111131_f();
      }
   }

   public Collection func_111130_a(int var1) {
      return (Collection)this.field_111137_c.get(var1);
   }

   public Collection func_111122_c() {
      HashSet var1 = new HashSet();

      for(int var2 = 0; var2 < 3; ++var2) {
         var1.addAll(this.func_111130_a(var2));
      }

      return var1;
   }

   public AttributeModifier getModifier(UUID var1) {
      return (AttributeModifier)this.field_111135_e.get(var1);
   }

   public void applyModifier(AttributeModifier var1) {
      if (this.getModifier(var1.getID()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Object var2 = (Set)this.field_111134_d.get(var1.getName());
         if (var2 == null) {
            var2 = new HashSet();
            this.field_111134_d.put(var1.getName(), var2);
         }

         ((Set)this.field_111137_c.get(var1.getOperation())).add(var1);
         ((Set)var2).add(var1);
         this.field_111135_e.put(var1.getID(), var1);
         this.func_111131_f();
      }
   }

   private void func_111131_f() {
      this.field_111133_g = true;
      this.field_111138_a.func_111149_a(this);
   }

   public void removeModifier(AttributeModifier var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         Set var3 = (Set)this.field_111137_c.get(var2);
         var3.remove(var1);
      }

      Set var4 = (Set)this.field_111134_d.get(var1.getName());
      if (var4 != null) {
         var4.remove(var1);
         if (var4.isEmpty()) {
            this.field_111134_d.remove(var1.getName());
         }
      }

      this.field_111135_e.remove(var1.getID());
      this.func_111131_f();
   }

   public void func_142049_d() {
      Collection var1 = this.func_111122_c();
      if (var1 != null) {
         ArrayList var4 = new ArrayList(var1);
         Iterator var2 = var4.iterator();

         while(var2.hasNext()) {
            AttributeModifier var3 = (AttributeModifier)var2.next();
            this.removeModifier(var3);
         }

      }
   }

   public double getAttributeValue() {
      if (this.field_111133_g) {
         this.field_111139_h = this.func_111129_g();
         this.field_111133_g = false;
      }

      return this.field_111139_h;
   }

   private double func_111129_g() {
      double var1 = this.getBaseValue();

      AttributeModifier var4;
      for(Iterator var3 = this.func_111130_a(0).iterator(); var3.hasNext(); var1 += var4.getAmount()) {
         var4 = (AttributeModifier)var3.next();
      }

      double var7 = var1;

      Iterator var5;
      AttributeModifier var6;
      for(var5 = this.func_111130_a(1).iterator(); var5.hasNext(); var7 += var1 * var6.getAmount()) {
         var6 = (AttributeModifier)var5.next();
      }

      for(var5 = this.func_111130_a(2).iterator(); var5.hasNext(); var7 *= 1.0 + var6.getAmount()) {
         var6 = (AttributeModifier)var5.next();
      }

      return this.field_111136_b.clampValue(var7);
   }
}
