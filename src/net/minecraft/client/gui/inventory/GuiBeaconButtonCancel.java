package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;

class GuiBeaconButtonCancel extends GuiBeaconButton {
   // $FF: synthetic field
   final GuiBeacon beaconGui;

   public GuiBeaconButtonCancel(GuiBeacon var1, int var2, int var3, int var4) {
      super(var2, var3, var4, GuiBeacon.getBeaconGuiTextures(), 112, 220);
      this.beaconGui = var1;
   }

   public void func_82251_b(int var1, int var2) {
      this.beaconGui.drawCreativeTabHoveringText(I18n.getString("gui.cancel"), var1, var2);
   }
}
