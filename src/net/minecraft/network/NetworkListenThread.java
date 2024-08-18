package net.minecraft.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ReportedException;

public abstract class NetworkListenThread {
   private final MinecraftServer mcServer;
   private final List connections = Collections.synchronizedList(new ArrayList());
   public volatile boolean isListening;

   public NetworkListenThread(MinecraftServer var1) {
      this.mcServer = var1;
      this.isListening = true;
   }

   public void addPlayer(NetServerHandler var1) {
      this.connections.add(var1);
   }

   public void stopListening() {
      this.isListening = false;
   }

   public void networkTick() {
      for(int var1 = 0; var1 < this.connections.size(); ++var1) {
         NetServerHandler var2 = (NetServerHandler)this.connections.get(var1);

         try {
            var2.networkTick();
         } catch (Exception exception) {
            if (var2.netManager instanceof MemoryConnection) {
               CrashReport var4 = CrashReport.makeCrashReport(exception, "Ticking memory connection");
               CrashReportCategory var5 = var4.makeCategory("Ticking connection");
               var5.addCrashSectionCallable("Connection", new CallableConnectionName(this, var2));
               throw new ReportedException(var4);
            }

            this.mcServer.getLogAgent().logWarningException("Failed to handle packet for " + var2.playerEntity.getEntityName() + "/" + var2.playerEntity.getPlayerIP() + ": " + exception, exception);
            FMLLog.log(Level.SEVERE, exception, "A critical server error occured handling a packet, kicking %s", var2.playerEntity.getEntityName());
            var2.kickPlayerFromServer("Internal server error");
         }

         if (var2.connectionClosed) {
            this.connections.remove(var1--);
         }

         var2.netManager.wakeThreads();
      }

   }

   public MinecraftServer getServer() {
      return this.mcServer;
   }
}
