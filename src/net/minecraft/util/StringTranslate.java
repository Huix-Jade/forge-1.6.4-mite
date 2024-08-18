package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class StringTranslate {
   private static final Pattern field_111053_a = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   private static final Splitter field_135065_b = Splitter.on('=').limit(2);
   private static StringTranslate instance = new StringTranslate();
   private Map languageList = Maps.newHashMap();

   public StringTranslate() {
      for (int i = 0; i < 2; ++i) {
         InputStream var1 = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/" + (i == 0 ? "MITE.lang" : "en_US.lang"));
         if (i != 0 || var1 != null) {
            this.localInject(var1);
         }
      }

   }

   public static void inject(InputStream inputstream)
   {
      instance.localInject(inputstream);
   }

   private void localInject(InputStream inputstream){
      try {
         if (inputstream != null) {
            for (String var3 : IOUtils.readLines(inputstream, Charsets.UTF_8)) {
               if (!var3.isEmpty() && var3.charAt(0) != '#') {
                  String[] var4 = Iterables.toArray(field_135065_b.split(var3), String.class);
                  if (var4 != null && var4.length == 2) {
                     String var5 = var4[0];
                     String var6 = field_111053_a.matcher(var4[1]).replaceAll("%$1s");
                     this.languageList.put(var5, var6);
                  }
               }
            }
         }
      } catch(Exception e) {

      }
   }





   static StringTranslate getInstance() {
      return instance;
   }

   public static synchronized void func_135063_a(Map par0Map) {
      instance.languageList.clear();
      instance.languageList.putAll(par0Map);
   }

   public synchronized String translateKey(String par1Str) {
      return this.func_135064_c(par1Str);
   }

   public synchronized String translateKeyFormat(String par1Str, Object... par2ArrayOfObj) {
      String var3 = this.func_135064_c(par1Str);

      try {
         return String.format(var3, par2ArrayOfObj);
      } catch (IllegalFormatException var5) {
         return "Format error: " + var3;
      }
   }

   private String func_135064_c(String par1Str) {
      String var2 = (String)this.languageList.get(par1Str);
      return var2 == null ? par1Str : var2;
   }

   public synchronized boolean containsTranslateKey(String par1Str) {
      return this.languageList.containsKey(par1Str);
   }
}
