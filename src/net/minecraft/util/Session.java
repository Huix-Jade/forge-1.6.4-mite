package net.minecraft.util;

public class Session {
   private final String username;
   private final String sessionId;

   public Session(String var1, String var2) {
      this.username = var1;
      this.sessionId = var2;
   }

   public String getUsername() {
      return this.username;
   }

   public String getSessionID() {
      return this.sessionId;
   }
}
