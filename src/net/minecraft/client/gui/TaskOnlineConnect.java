package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.ExceptionRetryCall;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.mco.McoServerAddress;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.resources.I18n;

public class TaskOnlineConnect extends TaskLongRunning {
   private NetClientHandler field_96586_a;
   private final McoServer field_96585_c;
   private final GuiScreen field_96584_d;

   public TaskOnlineConnect(GuiScreen var1, McoServer var2) {
      this.field_96584_d = var1;
      this.field_96585_c = var2;
   }

   public void run() {
      this.setMessage(I18n.getString("mco.connect.connecting"));
      McoClient var1 = new McoClient(this.getMinecraft().getSession());
      boolean var2 = false;
      boolean var3 = false;
      int var4 = 5;
      McoServerAddress var5 = null;

      for(int var6 = 0; var6 < 10 && !this.wasScreenClosed(); ++var6) {
         try {
            var5 = var1.func_96374_a(this.field_96585_c.field_96408_a);
            var2 = true;
         } catch (ExceptionRetryCall var8) {
            var4 = var8.field_96393_c;
         } catch (ExceptionMcoService var9) {
            var3 = true;
            this.setFailedMessage(var9.toString());
            Minecraft.getMinecraft().getLogAgent().logSevere(var9.toString());
            break;
         } catch (IOException var10) {
            Minecraft.getMinecraft().getLogAgent().logWarning("Realms: could not parse response");
         } catch (Exception var11) {
            var3 = true;
            this.setFailedMessage(var11.getLocalizedMessage());
         }

         if (var2) {
            break;
         }

         this.func_111251_a(var4);
      }

      if (!this.wasScreenClosed() && !var3) {
         if (var2) {
            ServerAddress var12 = ServerAddress.func_78860_a(var5.field_96417_a);
            this.func_96582_a(var12.getIP(), var12.getPort());
         } else {
            this.getMinecraft().displayGuiScreen(this.field_96584_d);
         }
      }

   }

   private void func_111251_a(int var1) {
      try {
         Thread.sleep((long)(var1 * 1000));
      } catch (InterruptedException var3) {
         Minecraft.getMinecraft().getLogAgent().logWarning(var3.getLocalizedMessage());
      }

   }

   private void func_96582_a(String var1, int var2) {
      (new ThreadOnlineConnect(this, var1, var2)).start();
   }

   public void updateScreen() {
      if (this.field_96586_a != null) {
         this.field_96586_a.processReadPackets();
      }

   }

   // $FF: synthetic method
   static NetClientHandler func_96583_a(TaskOnlineConnect var0, NetClientHandler var1) {
      return var0.field_96586_a = var1;
   }

   // $FF: synthetic method
   static GuiScreen func_98172_a(TaskOnlineConnect var0) {
      return var0.field_96584_d;
   }

   // $FF: synthetic method
   static NetClientHandler func_96580_a(TaskOnlineConnect var0) {
      return var0.field_96586_a;
   }
}
