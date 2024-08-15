package net.minecraft.world.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;

public class ThreadedFileIOBase implements Runnable {
   public static final ThreadedFileIOBase threadedIOInstance = new ThreadedFileIOBase();
   private List threadedIOQueue = Collections.synchronizedList(new ArrayList());
   private volatile boolean isThreadWaiting;
   public final Thread thread;

   private ThreadedFileIOBase() {
      Thread var1 = new Thread(this, "File IO Thread");
      var1.setPriority(1);
      var1.start();
      this.thread = var1;
   }

   public void run() {
      while(true) {
         this.processQueue();
      }
   }

   private void processQueue() {
      for(int var1 = 0; var1 < this.threadedIOQueue.size(); ++var1) {
         IThreadedFileIO var2 = (IThreadedFileIO)this.threadedIOQueue.get(var1);
         boolean var3 = var2.writeNextIO();
         if (!var3) {
            this.threadedIOQueue.remove(var1--);
         }

         try {
            Thread.sleep(this.isThreadWaiting ? 0L : 10L);
         } catch (InterruptedException var6) {
            var6.printStackTrace();
         }
      }

      if (this.threadedIOQueue.isEmpty()) {
         try {
            Thread.sleep(25L);
         } catch (InterruptedException var5) {
            var5.printStackTrace();
         }
      }

   }

   public void queueIO(IThreadedFileIO par1IThreadedFileIO) {
      if (!this.threadedIOQueue.contains(par1IThreadedFileIO)) {
         this.threadedIOQueue.add(par1IThreadedFileIO);
      }

   }

   public static void waitForFinish() {
      threadedIOInstance.isThreadWaiting = true;

      try {
         while(!isFinished()) {
            Thread.sleep(10L);
         }
      } catch (InterruptedException var4) {
         InterruptedException e = var4;
         e.printStackTrace();
      } finally {
         threadedIOInstance.isThreadWaiting = false;
      }

   }

   public static boolean isFinished() {
      return threadedIOInstance.threadedIOQueue.isEmpty();
   }

   public static void reportErrorIfNotFinished() {
      if (!isFinished()) {
         Minecraft.setErrorMessage("Warning: Not all pending chunks were saved to disk!");
      }

   }
}
