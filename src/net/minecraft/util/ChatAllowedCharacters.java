package net.minecraft.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class ChatAllowedCharacters {
   public static final String allowedCharacters = getAllowedCharacters();
   /**
    * Array of the special characters that are allowed in any text drawing of Minecraft.
    */
   public static final char[] allowedCharactersArray = new char[] {'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

   private static String getAllowedCharacters() {
      StringBuilder var0 = new StringBuilder();

      try {
         BufferedReader var1 = new BufferedReader(new InputStreamReader(
                 Objects.requireNonNull(ChatAllowedCharacters.class.getResourceAsStream("/font.txt")), "UTF-8"));
         String var2 = "";

         while((var2 = var1.readLine()) != null) {
            if (!var2.startsWith("#")) {
               var0.append(var2);
            }
         }

         var1.close();
      } catch (Exception var3) {
      }

      return var0.toString();
   }

   public static boolean isAllowedCharacter(char var0) {
      return var0 != 167 && (allowedCharacters.indexOf(var0) >= 0 || var0 > ' ');
   }

   public static String filerAllowedCharacters(String var0) {
      StringBuilder var1 = new StringBuilder();
      char[] var2 = var0.toCharArray();

       for (char var5 : var2) {
           if (isAllowedCharacter(var5)) {
               var1.append(var5);
           }
       }

      return var1.toString();
   }
}
