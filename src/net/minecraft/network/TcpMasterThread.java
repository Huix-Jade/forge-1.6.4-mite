package net.minecraft.network;

class TcpMasterThread extends Thread {
   // $FF: synthetic field
   final TcpConnection theTcpConnection;

   TcpMasterThread(TcpConnection var1) {
      this.theTcpConnection = var1;
   }

   public void run() {
      try {
         Thread.sleep(5000L);
         if (TcpConnection.getReadThread(this.theTcpConnection).isAlive()) {
            try {
               TcpConnection.getReadThread(this.theTcpConnection).stop();
            } catch (Throwable var3) {
            }
         }

         if (TcpConnection.getWriteThread(this.theTcpConnection).isAlive()) {
            try {
               TcpConnection.getWriteThread(this.theTcpConnection).stop();
            } catch (Throwable var2) {
            }
         }
      } catch (InterruptedException var4) {
         var4.printStackTrace();
      }

   }
}
