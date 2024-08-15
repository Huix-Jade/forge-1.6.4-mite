package net.minecraft.client.gui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;

public class ChatClickData {
   public static final Pattern pattern = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
   private final FontRenderer fontR;
   private final ChatLine line;
   private final int field_78312_d;
   private final int field_78313_e;
   private final String field_78310_f;
   private final String clickedUrl;

   public ChatClickData(FontRenderer var1, ChatLine var2, int var3, int var4) {
      this.fontR = var1;
      this.line = var2;
      this.field_78312_d = var3;
      this.field_78313_e = var4;
      this.field_78310_f = var1.trimStringToWidth(var2.getChatLineString(), var3);
      this.clickedUrl = this.findClickedUrl();
   }

   public String getClickedUrl() {
      return this.clickedUrl;
   }

   public URI getURI() {
      String var1 = this.getClickedUrl();
      if (var1 == null) {
         return null;
      } else {
         Matcher var2 = pattern.matcher(var1);
         if (var2.matches()) {
            try {
               String var3 = var2.group(0);
               if (var2.group(1) == null) {
                  var3 = "http://" + var3;
               }

               return new URI(var3);
            } catch (URISyntaxException var4) {
               Minecraft.getMinecraft().getLogAgent().logSevereException("Couldn't create URI from chat", var4);
            }
         }

         return null;
      }
   }

   private String findClickedUrl() {
      int var1 = this.field_78310_f.lastIndexOf(" ", this.field_78310_f.length()) + 1;
      if (var1 < 0) {
         var1 = 0;
      }

      int var2 = this.line.getChatLineString().indexOf(" ", var1);
      if (var2 < 0) {
         var2 = this.line.getChatLineString().length();
      }

      return StringUtils.stripControlCodes(this.line.getChatLineString().substring(var1, var2));
   }
}
