package net.minecraft.world;

class GameRuleValue {
   private String valueString;
   private boolean valueBoolean;
   private int valueInteger;
   private double valueDouble;

   public GameRuleValue(String var1) {
      this.setValue(var1);
   }

   public void setValue(String var1) {
      this.valueString = var1;
      this.valueBoolean = Boolean.parseBoolean(var1);

      try {
         this.valueInteger = Integer.parseInt(var1);
      } catch (NumberFormatException var4) {
      }

      try {
         this.valueDouble = Double.parseDouble(var1);
      } catch (NumberFormatException var3) {
      }

   }

   public String getGameRuleStringValue() {
      return this.valueString;
   }

   public boolean getGameRuleBooleanValue() {
      return this.valueBoolean;
   }
}
