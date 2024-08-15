package net.minecraft.server.dedicated;

class DedicatedServerSleepThread extends Thread {
   // $FF: synthetic field
   final DedicatedServer theDecitatedServer;

   DedicatedServerSleepThread(DedicatedServer var1) {
      this.theDecitatedServer = var1;
      this.setDaemon(true);
      this.start();
   }

   public void run() {
      while(true) {
         try {
            Thread.sleep(2147483647L);
         } catch (InterruptedException var2) {
         }
      }
   }
}
