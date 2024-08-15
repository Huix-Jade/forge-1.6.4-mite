package net.minecraft.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.logging.ILogAgent;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

public class HttpUtil {
   public static String buildPostString(Map par0Map) {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = par0Map.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         if (var1.length() > 0) {
            var1.append('&');
         }

         UnsupportedEncodingException var5;
         try {
            var1.append(URLEncoder.encode((String)var3.getKey(), "UTF-8"));
         } catch (UnsupportedEncodingException var6) {
            var5 = var6;
            var5.printStackTrace();
         }

         if (var3.getValue() != null) {
            var1.append('=');

            try {
               var1.append(URLEncoder.encode(var3.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
            }
         }
      }

      return var1.toString();
   }

   public static String sendPost(ILogAgent par0ILogAgent, URL par1URL, Map par2Map, boolean par3) {
      return sendPost(par0ILogAgent, par1URL, buildPostString(par2Map), par3);
   }

   private static String sendPost(ILogAgent par0ILogAgent, URL par1URL, String par2Str, boolean par3) {
      try {
         Proxy var4 = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();
         if (var4 == null) {
            var4 = Proxy.NO_PROXY;
         }

         HttpURLConnection var5 = (HttpURLConnection)par1URL.openConnection(var4);
         var5.setRequestMethod("POST");
         var5.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         var5.setRequestProperty("Content-Length", "" + par2Str.getBytes().length);
         var5.setRequestProperty("Content-Language", "en-US");
         var5.setUseCaches(false);
         var5.setDoInput(true);
         var5.setDoOutput(true);
         DataOutputStream var6 = new DataOutputStream(var5.getOutputStream());
         var6.writeBytes(par2Str);
         var6.flush();
         var6.close();
         BufferedReader var7 = new BufferedReader(new InputStreamReader(var5.getInputStream()));
         StringBuffer var9 = new StringBuffer();

         String var8;
         while((var8 = var7.readLine()) != null) {
            var9.append(var8);
            var9.append('\r');
         }

         var7.close();
         return var9.toString();
      } catch (Exception var10) {
         if (!par3) {
            if (par0ILogAgent != null) {
               par0ILogAgent.logSevereException("Could not post to " + par1URL, var10);
            } else {
               Logger.getAnonymousLogger().log(Level.SEVERE, "Could not post to " + par1URL, var10);
            }
         }

         return "";
      }
   }

   public static int func_76181_a() throws IOException {
      ServerSocket var0 = null;
      boolean var1 = true;

      int var10;
      try {
         var0 = new ServerSocket(0);
         var10 = var0.getLocalPort();
      } finally {
         try {
            if (var0 != null) {
               var0.close();
            }
         } catch (IOException var9) {
         }

      }

      return var10;
   }

   public static String performGetRequest(String url_string, int connection_timeout_ms, int read_timeout_ms) {
      try {
         URLConnection c = (new URL(url_string)).openConnection();
         c.setConnectTimeout(connection_timeout_ms);
         c.setReadTimeout(read_timeout_ms);
         InputStream is = c.getInputStream();
         StringWriter sw = new StringWriter();
         IOUtils.copy(is, sw, "UTF-8");
         String s = sw.toString();
         is.close();
         return s;
      } catch (Exception var7) {
         Exception e = var7;
         if (Minecraft.inDevMode()) {
            System.out.println("performGetRequest(" + url_string + "): " + e.toString());
         }

         return null;
      }
   }
}
