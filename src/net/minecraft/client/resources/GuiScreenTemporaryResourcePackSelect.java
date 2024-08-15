package net.minecraft.client.resources;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumOS;
import net.minecraft.util.Util;
import org.lwjgl.Sys;

public class GuiScreenTemporaryResourcePackSelect extends GuiScreen {
   protected GuiScreen field_110347_a;
   private int refreshTimer = -1;
   private GuiScreenTemporaryResourcePackSelectSelectionList field_110346_c;
   private GameSettings field_96146_n;

   public GuiScreenTemporaryResourcePackSelect(GuiScreen var1, GameSettings var2) {
      this.field_110347_a = var1;
      this.field_96146_n = var2;
   }

   public void initGui() {
      this.buttonList.add(new GuiSmallButton(5, this.width / 2 - 154, this.height - 48, I18n.getString("resourcePack.openFolder")));
      this.buttonList.add(new GuiSmallButton(6, this.width / 2 + 4, this.height - 48, I18n.getString("gui.done")));
      this.field_110346_c = new GuiScreenTemporaryResourcePackSelectSelectionList(this, this.mc.getResourcePackRepository());
      this.field_110346_c.registerScrollButtons(7, 8);
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == 5) {
            File var2 = GuiScreenTemporaryResourcePackSelectSelectionList.func_110510_a(this.field_110346_c).getDirResourcepacks();
            String var3 = var2.getAbsolutePath();
            if (Util.getOSType() == EnumOS.MACOS) {
               try {
                  this.mc.getLogAgent().logInfo(var3);
                  Runtime.getRuntime().exec(new String[]{"/usr/bin/open", var3});
                  return;
               } catch (IOException var9) {
                  var9.printStackTrace();
               }
            } else if (Util.getOSType() == EnumOS.WINDOWS) {
               String var4 = String.format("cmd.exe /C start \"Open file\" \"%s\"", var3);

               try {
                  Runtime.getRuntime().exec(var4);
                  return;
               } catch (IOException var8) {
                  var8.printStackTrace();
               }
            }

            boolean var10 = false;

            try {
               Class var5 = Class.forName("java.awt.Desktop");
               Object var6 = var5.getMethod("getDesktop").invoke((Object)null);
               var5.getMethod("browse", URI.class).invoke(var6, var2.toURI());
            } catch (Throwable var7) {
               var7.printStackTrace();
               var10 = true;
            }

            if (var10) {
               this.mc.getLogAgent().logInfo("Opening via system class!");
               Sys.openURL("file://" + var3);
            }
         } else if (var1.id == 6) {
            this.mc.displayGuiScreen(this.field_110347_a);
         } else {
            this.field_110346_c.actionPerformed(var1);
         }

      }
   }

   protected void mouseClicked(int var1, int var2, int var3) {
      super.mouseClicked(var1, var2, var3);
   }

   protected void mouseMovedOrUp(int var1, int var2, int var3) {
      super.mouseMovedOrUp(var1, var2, var3);
   }

   public void drawScreen(int var1, int var2, float var3) {
      this.field_110346_c.drawScreen(var1, var2, var3);
      if (this.refreshTimer <= 0) {
         GuiScreenTemporaryResourcePackSelectSelectionList.func_110510_a(this.field_110346_c).updateRepositoryEntriesAll();
         this.refreshTimer = 20;
      }

      this.drawCenteredString(this.fontRenderer, I18n.getString("resourcePack.title"), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.getString("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
      super.drawScreen(var1, var2, var3);
   }

   public void updateScreen() {
      super.updateScreen();
      --this.refreshTimer;
   }

   // $FF: synthetic method
   static Minecraft func_110344_a(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_110341_b(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_110339_c(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_110345_d(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_110334_e(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_110340_f(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static FontRenderer func_130017_g(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.fontRenderer;
   }

   // $FF: synthetic method
   static FontRenderer func_130016_h(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.fontRenderer;
   }

   // $FF: synthetic method
   static FontRenderer func_110337_i(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.fontRenderer;
   }

   // $FF: synthetic method
   static FontRenderer func_110335_j(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.fontRenderer;
   }

   // $FF: synthetic method
   static FontRenderer func_110338_k(GuiScreenTemporaryResourcePackSelect var0) {
      return var0.fontRenderer;
   }
}
