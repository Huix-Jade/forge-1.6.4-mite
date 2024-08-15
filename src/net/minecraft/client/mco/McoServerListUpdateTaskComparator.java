package net.minecraft.client.mco;

import java.util.Comparator;

class McoServerListUpdateTaskComparator implements Comparator {
   private final String field_140069_b;
   // $FF: synthetic field
   final McoServerListUpdateTask field_140070_a;

   private McoServerListUpdateTaskComparator(McoServerListUpdateTask var1, String var2) {
      this.field_140070_a = var1;
      this.field_140069_b = var2;
   }

   public int func_140068_a(McoServer var1, McoServer var2) {
      if (var1.field_96405_e.equals(var2.field_96405_e)) {
         if (var1.field_96408_a < var2.field_96408_a) {
            return 1;
         } else {
            return var1.field_96408_a > var2.field_96408_a ? -1 : 0;
         }
      } else if (var1.field_96405_e.equals(this.field_140069_b)) {
         return -1;
      } else if (var2.field_96405_e.equals(this.field_140069_b)) {
         return 1;
      } else {
         if (var1.field_96404_d.equals("CLOSED") || var2.field_96404_d.equals("CLOSED")) {
            if (var1.field_96404_d.equals("CLOSED")) {
               return 1;
            }

            if (var2.field_96404_d.equals("CLOSED")) {
               return 0;
            }
         }

         if (var1.field_96408_a < var2.field_96408_a) {
            return 1;
         } else {
            return var1.field_96408_a > var2.field_96408_a ? -1 : 0;
         }
      }
   }

   // $FF: synthetic method
   public int compare(Object var1, Object var2) {
      return this.func_140068_a((McoServer)var1, (McoServer)var2);
   }

   // $FF: synthetic method
   McoServerListUpdateTaskComparator(McoServerListUpdateTask var1, String var2, McoServerListEmptyAnon var3) {
      this(var1, var2);
   }
}
