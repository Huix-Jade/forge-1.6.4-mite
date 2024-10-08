package net.minecraft.client.gui.mco;

import java.text.DateFormat;
import java.util.Date;
import net.minecraft.client.gui.GuiScreenSelectLocation;
import net.minecraft.client.mco.Backup;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.server.MinecraftServer;

class GuiScreenBackupSelectionList extends GuiScreenSelectLocation {
   // $FF: synthetic field
   final GuiScreenBackup field_111249_a;

   public GuiScreenBackupSelectionList(GuiScreenBackup var1) {
      super(GuiScreenBackup.func_130036_f(var1), var1.width, var1.height, 32, var1.height - 64, 36);
      this.field_111249_a = var1;
   }

   protected int getSize() {
      return GuiScreenBackup.func_110370_e(this.field_111249_a).size() + 1;
   }

   protected void elementClicked(int var1, boolean var2) {
      if (var1 < GuiScreenBackup.func_110370_e(this.field_111249_a).size()) {
         GuiScreenBackup.func_130029_a(this.field_111249_a, var1);
      }
   }

   protected boolean isSelected(int var1) {
      return var1 == GuiScreenBackup.func_130034_h(this.field_111249_a);
   }

   protected boolean func_104086_b(int var1) {
      return false;
   }

   protected int func_130003_b() {
      return this.getSize() * 36;
   }

   protected void func_130004_c() {
      this.field_111249_a.drawDefaultBackground();
   }

   protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
      if (var1 < GuiScreenBackup.func_110370_e(this.field_111249_a).size()) {
         this.func_111246_b(var1, var2, var3, var4, var5);
      }

   }

   private void func_111246_b(int var1, int var2, int var3, int var4, Tessellator var5) {
      Backup var6 = (Backup)GuiScreenBackup.func_110370_e(this.field_111249_a).get(var1);
      this.field_111249_a.drawString(GuiScreenBackup.func_130032_i(this.field_111249_a), "Backup (" + this.func_111248_a(MinecraftServer.getSystemTimeMillis() - var6.field_110725_b.getTime()) + ")", var2 + 2, var3 + 1, 16777215);
      this.field_111249_a.drawString(GuiScreenBackup.func_130033_j(this.field_111249_a), this.func_111247_a(var6.field_110725_b), var2 + 2, var3 + 12, 7105644);
   }

   private String func_111247_a(Date var1) {
      return DateFormat.getDateTimeInstance(3, 3).format(var1);
   }

   private String func_111248_a(Long var1) {
      if (var1 < 0L) {
         return "right now";
      } else {
         long var2 = var1 / 1000L;
         if (var2 < 60L) {
            return (var2 == 1L ? "1 second" : var2 + " seconds") + " ago";
         } else {
            long var4;
            if (var2 < 3600L) {
               var4 = var2 / 60L;
               return (var4 == 1L ? "1 minute" : var4 + " minutes") + " ago";
            } else if (var2 < 86400L) {
               var4 = var2 / 3600L;
               return (var4 == 1L ? "1 hour" : var4 + " hours") + " ago";
            } else {
               var4 = var2 / 86400L;
               return (var4 == 1L ? "1 day" : var4 + " days") + " ago";
            }
         }
      }
   }
}
