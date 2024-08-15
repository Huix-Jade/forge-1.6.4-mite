package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;

class GuiBeaconButtonPower extends GuiBeaconButton {
   private final int field_82261_l;
   private final int field_82262_m;
   // $FF: synthetic field
   final GuiBeacon beaconGui;

   public GuiBeaconButtonPower(GuiBeacon var1, int var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, GuiContainer.field_110408_a, 0 + Potion.potionTypes[var5].getStatusIconIndex() % 8 * 18, 198 + Potion.potionTypes[var5].getStatusIconIndex() / 8 * 18);
      this.beaconGui = var1;
      this.field_82261_l = var5;
      this.field_82262_m = var6;
   }

   public void func_82251_b(int var1, int var2) {
      String var3 = I18n.getString(Potion.potionTypes[this.field_82261_l].getName());
      if (this.field_82262_m >= 3 && this.field_82261_l != Potion.regeneration.id) {
         var3 = var3 + " II";
      }

      this.beaconGui.drawCreativeTabHoveringText(var3, var1, var2);
   }
}
