package net.minecraft.client.gui.mco;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenConfigureWorld;
import net.minecraft.client.gui.GuiScreenConfirmation;
import net.minecraft.client.gui.GuiScreenLongRunningTask;
import net.minecraft.client.mco.Backup;
import net.minecraft.client.mco.GuiScreenConfirmationType;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenBackup extends GuiScreen {
   private final GuiScreenConfigureWorld field_110380_a;
   private final long field_110377_b;
   private List field_110378_c = Collections.emptyList();
   private GuiScreenBackupSelectionList field_110375_d;
   private int field_110376_e = -1;
   private GuiButton field_110379_p;

   public GuiScreenBackup(GuiScreenConfigureWorld var1, long var2) {
      this.field_110380_a = var1;
      this.field_110377_b = var2;
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.field_110375_d = new GuiScreenBackupSelectionList(this);
      (new GuiScreenBackupDownloadThread(this)).start();
      this.func_110369_g();
   }

   private void func_110369_g() {
      this.buttonList.add(new GuiButton(0, this.width / 2 + 6, this.height - 52, 153, 20, I18n.getString("gui.back")));
      this.buttonList.add(this.field_110379_p = new GuiButton(1, this.width / 2 - 154, this.height - 52, 153, 20, I18n.getString("mco.backup.button.restore")));
   }

   public void updateScreen() {
      super.updateScreen();
   }

   protected void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id == 1) {
            String var2 = I18n.getString("mco.configure.world.restore.question.line1");
            String var3 = I18n.getString("mco.configure.world.restore.question.line2");
            this.mc.displayGuiScreen(new GuiScreenConfirmation(this, GuiScreenConfirmationType.Warning, var2, var3, 1));
         } else if (var1.id == 0) {
            this.mc.displayGuiScreen(this.field_110380_a);
         } else {
            this.field_110375_d.actionPerformed(var1);
         }

      }
   }

   public void confirmClicked(boolean var1, int var2) {
      if (var1 && var2 == 1) {
         this.func_110374_h();
      } else {
         this.mc.displayGuiScreen(this);
      }

   }

   private void func_110374_h() {
      if (this.field_110376_e >= 0 && this.field_110376_e < this.field_110378_c.size()) {
         Backup var1 = (Backup)this.field_110378_c.get(this.field_110376_e);
         GuiScreenBackupRestoreTask var2 = new GuiScreenBackupRestoreTask(this, var1, (GuiScreenBackupDownloadThread)null);
         GuiScreenLongRunningTask var3 = new GuiScreenLongRunningTask(this.mc, this.field_110380_a, var2);
         var3.func_98117_g();
         this.mc.displayGuiScreen(var3);
      }

   }

   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.field_110375_d.drawScreen(var1, var2, var3);
      this.drawCenteredString(this.fontRenderer, I18n.getString("mco.backup.title"), this.width / 2, 20, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   // $FF: synthetic method
   static Minecraft func_110366_a(GuiScreenBackup var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static List func_110373_a(GuiScreenBackup var0, List var1) {
      return var0.field_110378_c = var1;
   }

   // $FF: synthetic method
   static long func_110367_b(GuiScreenBackup var0) {
      return var0.field_110377_b;
   }

   // $FF: synthetic method
   static Minecraft func_130030_c(GuiScreenBackup var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static GuiScreenConfigureWorld func_130031_d(GuiScreenBackup var0) {
      return var0.field_110380_a;
   }

   // $FF: synthetic method
   static Minecraft func_130035_e(GuiScreenBackup var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static Minecraft func_130036_f(GuiScreenBackup var0) {
      return var0.mc;
   }

   // $FF: synthetic method
   static List func_110370_e(GuiScreenBackup var0) {
      return var0.field_110378_c;
   }

   // $FF: synthetic method
   static int func_130029_a(GuiScreenBackup var0, int var1) {
      return var0.field_110376_e = var1;
   }

   // $FF: synthetic method
   static int func_130034_h(GuiScreenBackup var0) {
      return var0.field_110376_e;
   }

   // $FF: synthetic method
   static FontRenderer func_130032_i(GuiScreenBackup var0) {
      return var0.fontRenderer;
   }

   // $FF: synthetic method
   static FontRenderer func_130033_j(GuiScreenBackup var0) {
      return var0.fontRenderer;
   }
}
