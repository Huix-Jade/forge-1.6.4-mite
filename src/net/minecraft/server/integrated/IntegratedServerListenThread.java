package net.minecraft.server.integrated;

import java.io.IOException;
import java.net.InetAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.MemoryConnection;
import net.minecraft.network.NetworkListenThread;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerListenThread;
import net.minecraft.util.HttpUtil;

public class IntegratedServerListenThread extends NetworkListenThread {
   private final MemoryConnection netMemoryConnection;
   private MemoryConnection theMemoryConnection;
   private String field_71759_e;
   private boolean field_71756_f;
   private ServerListenThread myServerListenThread;

   public IntegratedServerListenThread(IntegratedServer var1) throws IOException {
      super(var1);
      this.netMemoryConnection = new MemoryConnection(var1.getLogAgent(), (NetHandler)null);
   }

   public void func_71754_a(MemoryConnection var1, String var2) {
      this.theMemoryConnection = var1;
      this.field_71759_e = var2;
   }

   public String func_71755_c() throws IOException {
      if (this.myServerListenThread == null) {
         int var1 = -1;

         try {
            var1 = HttpUtil.func_76181_a();
         } catch (IOException var4) {
         }

         if (var1 <= 0) {
            var1 = 25564;
         }

         try {
            this.myServerListenThread = new ServerListenThread(this, (InetAddress)null, var1);
            this.myServerListenThread.start();
         } catch (IOException var3) {
            throw var3;
         }
      }

      return String.valueOf(this.myServerListenThread.getMyPort());
   }

   public void stopListening() {
      super.stopListening();
      if (this.myServerListenThread != null) {
         this.getIntegratedServer().getLogAgent().logInfo("Stopping server connection");
         this.myServerListenThread.func_71768_b();
         this.myServerListenThread.interrupt();
         this.myServerListenThread = null;
      }

   }

   public void networkTick() {
      if (this.theMemoryConnection != null) {
         EntityPlayerMP var1 = this.getIntegratedServer().getConfigurationManager().createPlayerForUser(this.field_71759_e);
         if (var1 != null) {
            this.netMemoryConnection.pairWith(this.theMemoryConnection);
            this.field_71756_f = true;
            this.getIntegratedServer().getConfigurationManager().initializeConnectionToPlayer(this.netMemoryConnection, var1);
         }

         this.theMemoryConnection = null;
         this.field_71759_e = null;
      }

      if (this.myServerListenThread != null) {
         this.myServerListenThread.processPendingConnections();
      }

      super.networkTick();
   }

   public IntegratedServer getIntegratedServer() {
      return (IntegratedServer)super.getServer();
   }

   public boolean isGamePaused() {
      return this.field_71756_f && this.netMemoryConnection.getPairedConnection().isConnectionActive() && this.netMemoryConnection.getPairedConnection().isGamePaused();
   }

   // $FF: synthetic method
   public MinecraftServer getServer() {
      return this.getIntegratedServer();
   }
}
