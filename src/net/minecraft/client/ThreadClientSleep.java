package net.minecraft.client;

class ThreadClientSleep extends Thread {
   // $FF: synthetic field
   final Minecraft mc;

   ThreadClientSleep(Minecraft var1, String var2) {
      super(var2);
      this.mc = var1;
   }

   public void run() {
      while(this.mc.running) {
         try {
            Thread.sleep(2147483647L);
         } catch (InterruptedException var2) {
         }
      }

   }
}
