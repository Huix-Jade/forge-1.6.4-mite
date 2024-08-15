package net.minecraft.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.NetworkListenThread;

public class ServerListenThread extends Thread {
   private final List pendingConnections = Collections.synchronizedList(new ArrayList());
   private final HashMap recentConnections = new HashMap();
   private int connectionCounter;
   private final ServerSocket myServerSocket;
   private NetworkListenThread myNetworkListenThread;
   private final InetAddress myServerAddress;
   private final int myPort;

   public ServerListenThread(NetworkListenThread var1, InetAddress var2, int var3) throws IOException {
      super("Listen thread");
      this.myNetworkListenThread = var1;
      this.myPort = var3;
      this.myServerSocket = new ServerSocket(var3, 0, var2);
      this.myServerAddress = var2 == null ? this.myServerSocket.getInetAddress() : var2;
      this.myServerSocket.setPerformancePreferences(0, 2, 1);
   }

   public void processPendingConnections() {
      synchronized(this.pendingConnections) {
         for(int var2 = 0; var2 < this.pendingConnections.size(); ++var2) {
            NetLoginHandler var3 = (NetLoginHandler)this.pendingConnections.get(var2);

            try {
               var3.tryLogin();
            } catch (Exception var6) {
               var3.raiseErrorAndDisconnect("Internal server error");
               this.myNetworkListenThread.getServer().getLogAgent().logWarningException("Failed to handle packet for " + var3.getUsernameAndAddress() + ": " + var6, var6);
            }

            if (var3.connectionComplete) {
               this.pendingConnections.remove(var2--);
            }

            var3.myTCPConnection.wakeThreads();
         }

      }
   }

   public void run() {
      while(this.myNetworkListenThread.isListening) {
         try {
            Socket var1 = this.myServerSocket.accept();
            NetLoginHandler var2 = new NetLoginHandler(this.myNetworkListenThread.getServer(), var1, "Connection #" + this.connectionCounter++);
            this.addPendingConnection(var2);
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

      this.myNetworkListenThread.getServer().getLogAgent().logInfo("Closing listening thread");
   }

   private void addPendingConnection(NetLoginHandler var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Got null pendingconnection!");
      } else {
         synchronized(this.pendingConnections) {
            this.pendingConnections.add(var1);
         }
      }
   }

   public void func_71769_a(InetAddress var1) {
      if (var1 != null) {
         synchronized(this.recentConnections) {
            this.recentConnections.remove(var1);
         }
      }

   }

   public void func_71768_b() {
      try {
         this.myServerSocket.close();
      } catch (Throwable var2) {
      }

   }

   public int getMyPort() {
      return this.myPort;
   }
}
