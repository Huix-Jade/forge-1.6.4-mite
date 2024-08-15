package net.minecraft.client.mco;

public class ExceptionMcoService extends Exception {
   public final int field_96392_a;
   public final String field_96391_b;
   public final int field_130097_c;

   public ExceptionMcoService(int var1, String var2, int var3) {
      super(var2);
      this.field_96392_a = var1;
      this.field_96391_b = var2;
      this.field_130097_c = var3;
   }

   public String toString() {
      return this.field_130097_c != -1 ? "Realms ( ErrorCode: " + this.field_130097_c + " )" : "Realms: " + this.field_96391_b;
   }
}
