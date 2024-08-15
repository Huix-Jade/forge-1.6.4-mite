package net.minecraft.server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.storage.ThreadedFileIOBase;

final class MinecraftServerGuiINNER1MITE extends WindowAdapter {
   final DedicatedServer field_120023_a;

   MinecraftServerGuiINNER1MITE(DedicatedServer par1DedicatedServer) {
      this.field_120023_a = par1DedicatedServer;
   }

   public void windowClosing(WindowEvent par1WindowEvent) {
      this.field_120023_a.save_world_maps_on_shutdown = this.field_120023_a.isServerSideMappingEnabled();
      this.field_120023_a.initiateShutdown();

      while(!this.field_120023_a.isServerStopped()) {
         try {
            Thread.sleep(100L);
         } catch (InterruptedException var3) {
            var3.printStackTrace();
         }
      }

      ThreadedFileIOBase.reportErrorIfNotFinished();
      System.exit(0);
   }
}
