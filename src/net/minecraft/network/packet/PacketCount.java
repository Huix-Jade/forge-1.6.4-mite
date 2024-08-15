package net.minecraft.network.packet;

import java.util.HashMap;
import java.util.Map;

public class PacketCount {
   public static boolean allowCounting = true;
   private static final Map packetCountForID = new HashMap();
   private static final Map sizeCountForID = new HashMap();
   private static final Object lock = new Object();

   public static void countPacket(int var0, long var1) {
      if (allowCounting) {
         synchronized(lock) {
            if (packetCountForID.containsKey(var0)) {
               packetCountForID.put(var0, (Long)packetCountForID.get(var0) + 1L);
               sizeCountForID.put(var0, (Long)sizeCountForID.get(var0) + var1);
            } else {
               packetCountForID.put(var0, 1L);
               sizeCountForID.put(var0, var1);
            }

         }
      }
   }
}
