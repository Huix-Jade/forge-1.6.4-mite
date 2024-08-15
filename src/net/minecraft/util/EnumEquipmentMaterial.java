package net.minecraft.util;

public enum EnumEquipmentMaterial {
   leather(1.0F, 10, EnumQuality.fine, "leather"),
   wood(0.5F, 10, EnumQuality.fine, "wood"),
   flint(1.0F, 0, EnumQuality.fine, "flint"),
   obsidian(2.0F, 0, EnumQuality.fine, "obsidian"),
   rusted_iron(4.0F, 0, EnumQuality.poor, "rusted_iron"),
   copper(4.0F, 30, EnumQuality.excellent, "copper"),
   silver(4.0F, 30, EnumQuality.excellent, "silver"),
   gold(4.0F, 50, EnumQuality.superb, "gold"),
   iron(8.0F, 30, EnumQuality.masterwork, "iron"),
   ancient_metal(16.0F, 40, EnumQuality.masterwork, "ancient_metal"),
   mithril(64.0F, 100, EnumQuality.legendary, "mithril"),
   adamantium(256.0F, 40, EnumQuality.legendary, "adamantium"),
   netherrack(4.0F, 0, EnumQuality.average, "netherrack"),
   glass(2.0F, 0, EnumQuality.average, "glass"),
   quartz(4.0F, 40, EnumQuality.fine, "quartz"),
   emerald(8.0F, 70, EnumQuality.excellent, "emerald"),
   diamond(16.0F, 100, EnumQuality.superb, "diamond");

   public final float durability;
   public final int enchantability;
   public final EnumQuality max_quality;
   public final String name;

   private EnumEquipmentMaterial(float durability, int enchantability, EnumQuality max_quality, String name) {
      this.durability = durability;
      this.enchantability = enchantability;
      this.max_quality = max_quality;
      this.name = name;
   }
}
