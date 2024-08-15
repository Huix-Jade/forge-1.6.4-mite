package net.minecraft.client.gui;

public class GuiPlayerInfo {
   public final String name;
   private final String nameinLowerCase;
   public int responseTime;
   public int level;

   public GuiPlayerInfo(String par1Str, int level) {
      this.name = par1Str;
      this.nameinLowerCase = par1Str.toLowerCase();
      this.level = level;
   }
}
