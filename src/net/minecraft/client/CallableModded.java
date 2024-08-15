package net.minecraft.client;

import java.util.concurrent.Callable;

class CallableModded implements Callable {
   // $FF: synthetic field
   final Minecraft mc;

   CallableModded(Minecraft var1) {
      this.mc = var1;
   }

   public String getClientProfilerEnabled() {
      String var1 = ClientBrandRetriever.getClientModName();
      if (!var1.equals("vanilla")) {
         return "Definitely; Client brand changed to '" + var1 + "'";
      } else {
         return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
      }
   }

   // $FF: synthetic method
   public Object call() {
      return this.getClientProfilerEnabled();
   }
}
