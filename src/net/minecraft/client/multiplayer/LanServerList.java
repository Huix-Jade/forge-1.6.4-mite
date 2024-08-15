package net.minecraft.client.multiplayer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LanServerList {
   private ArrayList listOfLanServers = new ArrayList();
   boolean wasUpdated;

   public synchronized boolean getWasUpdated() {
      return this.wasUpdated;
   }

   public synchronized void setWasNotUpdated() {
      this.wasUpdated = false;
   }

   public synchronized List getLanServers() {
      return Collections.unmodifiableList(this.listOfLanServers);
   }

   public synchronized void func_77551_a(String var1, InetAddress var2) {
      String var3 = ThreadLanServerPing.getMotdFromPingResponse(var1);
      String var4 = ThreadLanServerPing.getAdFromPingResponse(var1);
      if (var4 != null) {
         var4 = var2.getHostAddress() + ":" + var4;
         boolean var5 = false;
         Iterator var6 = this.listOfLanServers.iterator();

         while(var6.hasNext()) {
            LanServer var7 = (LanServer)var6.next();
            if (var7.getServerIpPort().equals(var4)) {
               var7.updateLastSeen();
               var5 = true;
               break;
            }
         }

         if (!var5) {
            this.listOfLanServers.add(new LanServer(var3, var4));
            this.wasUpdated = true;
         }

      }
   }
}
