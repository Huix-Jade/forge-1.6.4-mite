package net.minecraft.util;

public enum EnumInsulinResistanceLevel {
   mild("mild", EnumChatFormatting.YELLOW, 48000, true),
   moderate("moderate", EnumChatFormatting.GOLD, 96000, false),
   severe("severe", EnumChatFormatting.RED, 144000, false);

   private final String unlocalized_name;
   private final EnumChatFormatting color;
   private final int threshold;
   private final boolean can_metabolize_food_sugars;

   private EnumInsulinResistanceLevel(String unlocalized_name, EnumChatFormatting color, int threshold, boolean can_metabolize_food_sugars) {
      this.unlocalized_name = unlocalized_name;
      this.color = color;
      this.threshold = threshold;
      this.can_metabolize_food_sugars = can_metabolize_food_sugars;
   }

   static EnumInsulinResistanceLevel get(int ordinal) {
      return values()[ordinal];
   }

   public int getOrdinalForTransmission() {
      return this.ordinal() + 1;
   }

   public static int getOrdinalForTransmission(EnumInsulinResistanceLevel insulin_resistance_level) {
      return insulin_resistance_level == null ? 0 : insulin_resistance_level.getOrdinalForTransmission();
   }

   public static EnumInsulinResistanceLevel getByTransmittedOrdinal(int transmitted_ordinal) {
      return transmitted_ordinal < 1 ? null : values()[transmitted_ordinal - 1];
   }

   boolean isMild() {
      return this == mild;
   }

   boolean isModerate() {
      return this == moderate;
   }

   public boolean isSevere() {
      return this == severe;
   }

   public String getUnlocalizedName() {
      return this.unlocalized_name;
   }

   public EnumChatFormatting getColor() {
      return this.color;
   }

   public float getRedAsFloat() {
      return this.color.getRedAsFloat();
   }

   public float getGreenAsFloat() {
      return this.color.getGreenAsFloat();
   }

   public float getBlueAsFloat() {
      return this.color.getBlueAsFloat();
   }

   int getThreshold() {
      return this.threshold;
   }

   public boolean isLessSevereThan(EnumInsulinResistanceLevel insulin_resistance_level) {
      return insulin_resistance_level != null && insulin_resistance_level.threshold > this.threshold;
   }

   public boolean isMoreSevereThan(EnumInsulinResistanceLevel insulin_resistance_level) {
      return insulin_resistance_level == null || insulin_resistance_level.threshold < this.threshold;
   }

   public EnumInsulinResistanceLevel getNext() {
      return values()[this.ordinal() + 1];
   }

   public static EnumInsulinResistanceLevel getInsulinResistanceLevel(int insulin_resistance) {
      for(int i = values().length - 1; i >= 0; --i) {
         if (insulin_resistance >= values()[i].threshold) {
            return values()[i];
         }
      }

      return null;
   }

   public boolean canMetabolizeFoodSugars() {
      return this.can_metabolize_food_sugars;
   }
}
