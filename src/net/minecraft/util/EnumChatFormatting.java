package net.minecraft.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public enum EnumChatFormatting {
   BLACK('0', 0, 0, 0),
   DARK_BLUE('1', 0, 0, 170),
   DARK_GREEN('2', 0, 170, 0),
   DARK_AQUA('3', 0, 170, 170),
   DARK_RED('4', 170, 0, 0),
   DARK_PURPLE('5', 170, 0, 170),
   GOLD('6', 255, 170, 0),
   GRAY('7', 170, 170, 170),
   DARK_GRAY('8', 85, 85, 85),
   BLUE('9', 85, 85, 255),
   GREEN('a', 85, 255, 85),
   AQUA('b', 85, 255, 255),
   RED('c', 255, 85, 85),
   LIGHT_PURPLE('d', 255, 85, 255),
   YELLOW('e', 255, 255, 85),
   WHITE('f', 255, 255, 255),
   BROWN('g', 192, 144, 96),
   LIGHT_GRAY('h', 192, 192, 192),
   OBFUSCATED('k', true),
   BOLD('l', true),
   STRIKETHROUGH('m', true),
   UNDERLINE('n', true),
   ITALIC('o', true),
   RESET('r', false);

   private static final Map field_96321_w = new HashMap();
   private static final Map field_96331_x = new HashMap();
   private static final Pattern field_96330_y = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-OR]");
   private final char field_96329_z;
   private final boolean field_96303_A;
   private final String field_96304_B;
   public int rgb;
   public int rgb_shadow;
   public int rgb_anaglyph;
   public int rgb_anaglyph_shadow;
   public int r;
   public int g;
   public int b;

   private EnumChatFormatting(char par3, boolean par4) {
      this.field_96329_z = par3;
      this.field_96303_A = par4;
      this.field_96304_B = "§" + par3;
   }

   private EnumChatFormatting(char c, int r, int g, int b) {
      this(c, false);
      this.r = r;
      this.g = g;
      this.b = b;
      this.rgb = (r & 255) << 16 | (g & 255) << 8 | b & 255;
      this.rgb_shadow = (r / 4 & 255) << 16 | (g / 4 & 255) << 8 | b / 4 & 255;
      int r_anaglyph = (r * 30 + g * 59 + b * 11) / 100;
      int g_anaglyph = (r * 30 + g * 70) / 100;
      int b_anaglyph = (r * 30 + b * 70) / 100;
      this.rgb_anaglyph = (r_anaglyph & 255) << 16 | (g_anaglyph & 255) << 8 | b_anaglyph & 255;
      this.rgb_anaglyph_shadow = (r_anaglyph / 4 & 255) << 16 | (g_anaglyph / 4 & 255) << 8 | b_anaglyph / 4 & 255;
   }

   public static EnumChatFormatting getByChar(char c) {
      for(int i = 0; i < values().length; ++i) {
         if (values()[i].field_96329_z == c) {
            return values()[i];
         }
      }

      return null;
   }

   public char func_96298_a() {
      return this.field_96329_z;
   }

   public boolean func_96301_b() {
      return this.field_96303_A;
   }

   public boolean isColor() {
      return !this.field_96303_A && this != RESET;
   }

   public String func_96297_d() {
      return this.name().toLowerCase();
   }

   public String toString() {
      return this.field_96304_B;
   }

   public static String func_110646_a(String par0Str) {
      return par0Str == null ? null : field_96330_y.matcher(par0Str).replaceAll("");
   }

   public static EnumChatFormatting func_96300_b(String par0Str) {
      return par0Str == null ? null : (EnumChatFormatting)field_96331_x.get(par0Str.toLowerCase());
   }

   public static Collection func_96296_a(boolean par0, boolean par1) {
      ArrayList var2 = new ArrayList();
      EnumChatFormatting[] var3 = values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumChatFormatting var6 = var3[var5];
         if ((!var6.isColor() || par0) && (!var6.func_96301_b() || par1)) {
            var2.add(var6.func_96297_d());
         }
      }

      return var2;
   }

   public float getRedAsFloat() {
      return (float)this.r / 255.0F;
   }

   public float getGreenAsFloat() {
      return (float)this.g / 255.0F;
   }

   public float getBlueAsFloat() {
      return (float)this.b / 255.0F;
   }

   static {
      EnumChatFormatting[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         EnumChatFormatting var3 = var0[var2];
         field_96321_w.put(var3.func_96298_a(), var3);
         field_96331_x.put(var3.func_96297_d(), var3);
      }

   }
}
