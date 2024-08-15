package net.minecraft.item;

public enum EnumRarity {
   common(15, "Common", 100),
   uncommon(14, "Uncommon", 25),
   rare(11, "Rare", 5),
   epic(13, "Epic", 1);

   public final int rarityColor;
   public final String rarityName;
   public final int standard_weight;

   private EnumRarity(int color_index, String name, int standard_weight) {
      this.rarityColor = color_index;
      this.rarityName = name;
      this.standard_weight = standard_weight;
   }
}
