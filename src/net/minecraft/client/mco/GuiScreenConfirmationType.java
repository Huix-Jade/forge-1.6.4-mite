package net.minecraft.client.mco;

public enum GuiScreenConfirmationType {
   Warning("Warning!", 16711680),
   Info("Info!", 8226750);

   public final int field_140075_c;
   public final String field_140072_d;

   private GuiScreenConfirmationType(String var3, int var4) {
      this.field_140072_d = var3;
      this.field_140075_c = var4;
   }
}
