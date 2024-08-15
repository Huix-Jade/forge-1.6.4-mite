package net.minecraft.client.gui;

import java.net.ConnectException;
import java.net.UnknownHostException;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.packet.Packet2ClientProtocol;

class ThreadOnlineConnect extends Thread {
   // $FF: synthetic field
   final String field_96595_a;
   // $FF: synthetic field
   final int field_96593_b;
   // $FF: synthetic field
   final TaskOnlineConnect field_96594_c;

   ThreadOnlineConnect(TaskOnlineConnect var1, String var2, int var3) {
      this.field_96594_c = var1;
      this.field_96595_a = var2;
      this.field_96593_b = var3;
   }

   public void run() {
      try {
         TaskOnlineConnect.func_96583_a(this.field_96594_c, new NetClientHandler(this.field_96594_c.getMinecraft(), this.field_96595_a, this.field_96593_b, TaskOnlineConnect.func_98172_a(this.field_96594_c)));
         if (this.field_96594_c.wasScreenClosed()) {
            return;
         }

         this.field_96594_c.setMessage(I18n.getString("mco.connect.authorizing"));
         TaskOnlineConnect.func_96580_a(this.field_96594_c).addToSendQueue(new Packet2ClientProtocol(78, this.field_96594_c.getMinecraft().getSession().getUsername(), this.field_96595_a, this.field_96593_b));
      } catch (UnknownHostException var2) {
         if (this.field_96594_c.wasScreenClosed()) {
            return;
         }

         this.field_96594_c.getMinecraft().displayGuiScreen(new GuiScreenDisconnectedOnline(TaskOnlineConnect.func_98172_a(this.field_96594_c), "connect.failed", "disconnect.genericReason", new Object[]{"Unknown host '" + this.field_96595_a + "'"}));
      } catch (ConnectException var3) {
         if (this.field_96594_c.wasScreenClosed()) {
            return;
         }

         this.field_96594_c.getMinecraft().displayGuiScreen(new GuiScreenDisconnectedOnline(TaskOnlineConnect.func_98172_a(this.field_96594_c), "connect.failed", "disconnect.genericReason", new Object[]{var3.getMessage()}));
      } catch (Exception var4) {
         if (this.field_96594_c.wasScreenClosed()) {
            return;
         }

         var4.printStackTrace();
         this.field_96594_c.getMinecraft().displayGuiScreen(new GuiScreenDisconnectedOnline(TaskOnlineConnect.func_98172_a(this.field_96594_c), "connect.failed", "disconnect.genericReason", new Object[]{var4.toString()}));
      }

   }
}
