package net.minecraft.client;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.stats.IStatStringFormat;

class StatStringFormatKeyInv implements IStatStringFormat {
   // $FF: synthetic field
   final Minecraft mc;

   StatStringFormatKeyInv(Minecraft var1) {
      this.mc = var1;
   }

   public String formatString(String var1) {
      try {
         return String.format(var1, GameSettings.getKeyDisplayString(this.mc.gameSettings.keyBindInventory.keyCode));
      } catch (Exception var3) {
         return "Error: " + var3.getLocalizedMessage();
      }
   }
}
