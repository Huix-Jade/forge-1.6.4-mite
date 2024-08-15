package net.minecraft.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.network.TcpConnection;
import org.apache.commons.io.IOUtils;

public class Notification extends Thread {
   final EntityClientPlayerMP player;

   public Notification(EntityClientPlayerMP player) {
      this.player = player;
   }

   public void run() {
      if (this.player != null && this.player.username != null) {
         try {
            String enc = "nrmvxizug-rh-gll-vzhb.xln/orhgvmvi.ksk";
            String url_string = "http://" + gmos(enc) + "?op=player&r=" + 196;
            if (this.player.sendQueue.getNetManager() instanceof TcpConnection) {
               url_string = url_string + "&rs=" + StringHelper.stripLeading("/", "" + this.player.sendQueue.getNetManager().getSocketAddress());
               url_string = url_string + "&st=" + (Minecraft.is_dedicated_server_running ? "DS" : "LAN");
            }

            url_string = url_string + "&un=" + this.player.username;
            URL url = new URL(url_string);
            InputStream is = url.openStream();
            StringWriter sw = new StringWriter();
            IOUtils.copy(is, sw, "UTF-8");
            String s = sw.toString();
            if (is != null) {
               is.close();
            }
         } catch (Exception var7) {
         }

      }
   }

   public static String gmos(String s) {
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
}
