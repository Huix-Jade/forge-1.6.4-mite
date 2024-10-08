package net.minecraft.network.rcon;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class RConThreadClient extends RConThreadBase {
   private boolean loggedIn;
   private Socket clientSocket;
   private byte[] buffer = new byte[1460];
   private String rconPassword;

   RConThreadClient(IServer par1IServer, Socket par2Socket) {
      super(par1IServer);
      this.clientSocket = par2Socket;

      try {
         this.clientSocket.setSoTimeout(0);
      } catch (Exception var4) {
         this.running = false;
      }

      this.rconPassword = par1IServer.getStringProperty("rcon.password", "");
      this.logInfo("Rcon connection from: " + par2Socket.getInetAddress());
   }

   public void run() {
      while(true) {
         try {
            if (this.running) {
               BufferedInputStream var1 = new BufferedInputStream(this.clientSocket.getInputStream());
               int var2 = var1.read(this.buffer, 0, 1460);
               if (10 > var2) {
                  return;
               }

               byte var3 = 0;
               int var4 = RConUtils.getBytesAsLEInt(this.buffer, 0, var2);
               if (var4 != var2 - 4) {
                  continue;
               }

               int var21 = var3 + 4;
               int var5 = RConUtils.getBytesAsLEInt(this.buffer, var21, var2);
               var21 += 4;
               int var6 = RConUtils.getRemainingBytesAsLEInt(this.buffer, var21);
               var21 += 4;
               String var8;
               switch (var6) {
                  case 2:
                     if (this.loggedIn) {
                        var8 = RConUtils.getBytesAsString(this.buffer, var21, var2);

                        try {
                           this.sendMultipacketResponse(var5, this.server.executeCommand(var8, false));
                        } catch (Exception var16) {
                           this.sendMultipacketResponse(var5, "Error executing: " + var8 + " (" + var16.getMessage() + ")");
                        }
                        continue;
                     }

                     this.sendLoginFailedResponse();
                     continue;
                  case 3:
                     var8 = RConUtils.getBytesAsString(this.buffer, var21, var2);
                     int var10000 = var21 + var8.length();
                     if (0 != var8.length() && var8.equals(this.rconPassword)) {
                        this.loggedIn = true;
                        this.sendResponse(var5, 2, "");
                        continue;
                     }

                     this.loggedIn = false;
                     this.sendLoginFailedResponse();
                     continue;
                  default:
                     this.sendMultipacketResponse(var5, String.format("Unknown request %s", Integer.toHexString(var6)));
                     continue;
               }
            }
         } catch (SocketTimeoutException var17) {
         } catch (IOException var18) {
         } catch (Exception var19) {
            System.out.println(var19);
         } finally {
            this.closeSocket();
         }

         return;
      }
   }

   private void sendResponse(int par1, int par2, String par3Str) throws IOException {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(1248);
      DataOutputStream var5 = new DataOutputStream(var4);
      byte[] var6 = par3Str.getBytes("UTF-8");
      var5.writeInt(Integer.reverseBytes(var6.length + 10));
      var5.writeInt(Integer.reverseBytes(par1));
      var5.writeInt(Integer.reverseBytes(par2));
      var5.write(var6);
      var5.write(0);
      var5.write(0);
      this.clientSocket.getOutputStream().write(var4.toByteArray());
   }

   private void sendLoginFailedResponse() throws IOException {
      this.sendResponse(-1, 2, "");
   }

   private void sendMultipacketResponse(int par1, String par2Str) throws IOException {
      int var3 = par2Str.length();

      do {
         int var4 = 4096 <= var3 ? 4096 : var3;
         this.sendResponse(par1, 0, par2Str.substring(0, var4));
         par2Str = par2Str.substring(var4);
         var3 = par2Str.length();
      } while(0 != var3);

   }

   private void closeSocket() {
      if (null != this.clientSocket) {
         try {
            this.clientSocket.close();
         } catch (IOException var2) {
            this.logWarning("IO: " + var2.getMessage());
         }

         this.clientSocket = null;
      }

   }
}
