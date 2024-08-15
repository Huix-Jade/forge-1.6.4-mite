package net.minecraft.client.gui;

public class ChatLine {
   private final int updateCounterCreated;
   private final String lineString;
   private final int chatLineID;

   public ChatLine(int var1, String var2, int var3) {
      this.lineString = var2;
      this.updateCounterCreated = var1;
      this.chatLineID = var3;
   }

   public String getChatLineString() {
      return this.lineString;
   }

   public int getUpdatedCounter() {
      return this.updateCounterCreated;
   }

   public int getChatLineID() {
      return this.chatLineID;
   }
}
