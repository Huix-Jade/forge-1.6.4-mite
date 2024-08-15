package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale {
   private static final Splitter splitter = Splitter.on('=').limit(2);
   private static final Pattern field_135031_c = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   Map field_135032_a = Maps.newHashMap();
   private boolean field_135029_d;

   public synchronized void loadLocaleDataFiles(ResourceManager var1, List var2) {
      this.field_135032_a.clear();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String var5 = String.format("lang/%s.lang", var4);
         Iterator var6 = var1.getResourceDomains().iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();

            try {
               this.loadLocaleData(var1.getAllResources(new ResourceLocation(var7, var5)));
            } catch (IOException var9) {
            }
         }
      }

      this.checkUnicode();
   }

   public boolean isUnicode() {
      return this.field_135029_d;
   }

   private void checkUnicode() {
      this.field_135029_d = false;
      Iterator var1 = this.field_135032_a.values().iterator();

      while(true) {
         while(var1.hasNext()) {
            String var2 = (String)var1.next();

            for(int var3 = 0; var3 < var2.length(); ++var3) {
               if (var2.charAt(var3) >= 256) {
                  this.field_135029_d = true;
                  break;
               }
            }
         }

         return;
      }
   }

   private void loadLocaleData(List var1) throws IOException {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Resource var3 = (Resource)var2.next();
         this.loadLocaleData(var3.getInputStream());
      }

   }

   private void loadLocaleData(InputStream var1) throws IOException {
      Iterator var2 = IOUtils.readLines(var1, Charsets.UTF_8).iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (!var3.isEmpty() && var3.charAt(0) != '#') {
            String[] var4 = (String[])Iterables.toArray(splitter.split(var3), String.class);
            if (var4 != null && var4.length == 2) {
               String var5 = var4[0];
               String var6 = field_135031_c.matcher(var4[1]).replaceAll("%$1s");
               this.field_135032_a.put(var5, var6);
            }
         }
      }

   }

   private String translateKeyPrivate(String var1) {
      String var2 = (String)this.field_135032_a.get(var1);
      return var2 == null ? var1 : var2;
   }

   public String translateKey(String var1) {
      return this.translateKeyPrivate(var1);
   }

   public String formatMessage(String var1, Object[] var2) {
      String var3 = this.translateKeyPrivate(var1);

      try {
         return String.format(var3, var2);
      } catch (IllegalFormatException var5) {
         return "Format error: " + var3;
      }
   }
}
