package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.ExceptionRetryCall;
import net.minecraft.client.mco.McoClient;

class RunnableTitleScreen extends Thread {
   // $FF: synthetic field
   final GuiMainMenu theMainMenu;

   RunnableTitleScreen(GuiMainMenu var1) {
      this.theMainMenu = var1;
   }

   public void run() {
      McoClient var1 = new McoClient(GuiMainMenu.func_110348_a(this.theMainMenu).getSession());
      boolean var2 = false;

      for(int var3 = 0; var3 < 3; ++var3) {
         try {
            Boolean var4 = var1.func_96375_b();
            if (var4) {
               GuiMainMenu.func_130021_b(this.theMainMenu);
            }

            GuiMainMenu.func_110349_a(var4);
         } catch (ExceptionRetryCall var6) {
            var2 = true;
         } catch (ExceptionMcoService var7) {
            GuiMainMenu.func_130018_c(this.theMainMenu).getLogAgent().logSevere(var7.toString());
         } catch (IOException var8) {
            GuiMainMenu.func_130019_d(this.theMainMenu).getLogAgent().logWarning("Realms: could not parse response");
         }

         if (!var2) {
            break;
         }

         try {
            Thread.sleep(10000L);
         } catch (InterruptedException var5) {
            Thread.currentThread().interrupt();
         }
      }

   }
}
