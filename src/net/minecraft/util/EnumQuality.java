package net.minecraft.util;

import java.util.List;
import net.minecraft.client.Minecraft;

public enum EnumQuality {
   wretched("wretched", "Wretched Quality", 0.5F),
   poor("poor", "Poor Quality", 0.75F),
   average("average", "Average Quality", 1.0F),
   fine("fine", "Fine Quality", 1.5F),
   excellent("excellent", "Excellent Quality", 2.0F),
   superb("superb", "Superb Quality", 2.5F),
   masterwork("masterwork", "Masterwork", 3.0F),
   legendary("legendary", "Legendary", 3.5F);

   private final String unlocalized_name;
   private final float durability_modifier;

   private EnumQuality(String unlocalized_name, String descriptor, float durability_modifier) {
      this.unlocalized_name = unlocalized_name;
      this.durability_modifier = durability_modifier;
   }

   public static EnumQuality get(int ordinal) {
      return values()[ordinal];
   }

   public String getDescriptor() {
      return Translator.get("quality." + this.unlocalized_name);
   }

   public float getDurabilityModifier() {
      return this.durability_modifier;
   }

   public boolean isAverage() {
      return this == average;
   }

   public boolean isLowerThan(EnumQuality quality) {
      return this.ordinal() < quality.ordinal();
   }

   public boolean isHigherThan(EnumQuality quality) {
      return this.ordinal() > quality.ordinal();
   }

   public boolean isAverageOrLower() {
      return this.ordinal() <= average.ordinal();
   }

   public boolean isAverageOrHigher() {
      return this.ordinal() >= average.ordinal();
   }

   public static EnumQuality getHighestQuality() {
      return get(values().length - 1);
   }

   public static EnumQuality getLowest(EnumQuality first, EnumQuality second) {
      return first.isLowerThan(second) ? first : second;
   }

   public static EnumQuality getLowest(List list) {
      if (list.size() == 0) {
         return null;
      } else {
         EnumQuality lowest_quality = (EnumQuality)list.get(0);

         for(int i = 1; i < list.size(); ++i) {
            lowest_quality = getLowest((EnumQuality)list.get(i), lowest_quality);
         }

         return lowest_quality;
      }
   }

   public static EnumQuality getHighest(EnumQuality first, EnumQuality second) {
      return first.isHigherThan(second) ? first : second;
   }

   public static EnumQuality getHighest(List list) {
      if (list.size() == 0) {
         return null;
      } else {
         EnumQuality highest_quality = (EnumQuality)list.get(0);

         for(int i = 1; i < list.size(); ++i) {
            highest_quality = getHighest((EnumQuality)list.get(i), highest_quality);
         }

         return highest_quality;
      }
   }

   public EnumQuality getNextLower() {
      int ordinal = this.ordinal() - 1;
      if (ordinal < 0) {
         Minecraft.setErrorMessage("getNextLower: quality is already the lowest");
         ordinal = 0;
      }

      return get(ordinal);
   }

   public String toString() {
      return this.getDescriptor();
   }

   public String getUnlocalizedName() {
      return this.unlocalized_name;
   }
}
