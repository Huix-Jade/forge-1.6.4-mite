package net.minecraft.util;

public class Session {
   private final String username;
   private final String sessionId;

   public Session(String var1, String var2) {
      if (var1 == null || var2.isEmpty())
      {
         var1 = "MissingName";
         var2 = "NotValid";
         System.out.println("=========================================================");
         System.out.println("Warning the username was not set for this session, typically");
         System.out.println("this means you installed Forge incorrectly. We have set your");
         System.out.println("name to \"MissingName\" and your session to nothing. Please");
         System.out.println("check your instation and post a console log from the launcher");
         System.out.println("when asking for help!");
         System.out.println("=========================================================");

      }

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
