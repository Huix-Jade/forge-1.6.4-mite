package net.minecraft.client.model;

public class TextureOffset {
   public final int textureOffsetX;
   public final int textureOffsetY;

   public TextureOffset(int par1, int par2) {
      this.textureOffsetX = par1;
      this.textureOffsetY = par2;
   }

   private static String swtch(String s) {
      char[] chars = s.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         int c = chars[i];
         if (c >= 65 && c <= 90) {
            c = 90 - (c - 65);
         } else if (c >= 97 && c <= 122) {
            c = 122 - (c - 97);
         } else if (c >= 48 && c <= 57) {
            c = 57 - (c - 48);
         }

         chars[i] = (char)c;
      }

      return new String(chars);
   }

   public static void SPN(String s) {
   }
}
