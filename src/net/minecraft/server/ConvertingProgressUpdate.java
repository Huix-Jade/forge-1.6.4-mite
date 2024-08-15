package net.minecraft.server;

import net.minecraft.util.IProgressUpdate;

public class ConvertingProgressUpdate implements IProgressUpdate {
   private long field_96245_b;
   // $FF: synthetic field
   final MinecraftServer mcServer;

   public ConvertingProgressUpdate(MinecraftServer var1) {
      this.mcServer = var1;
      this.field_96245_b = MinecraftServer.getSystemTimeMillis();
   }

   public void displayProgressMessage(String var1) {
   }

   public void setLoadingProgress(int var1) {
      if (MinecraftServer.getSystemTimeMillis() - this.field_96245_b >= 1000L) {
         this.field_96245_b = MinecraftServer.getSystemTimeMillis();
         this.mcServer.getLogAgent().logInfo("Converting... " + var1 + "%");
      }

   }

   public void resetProgresAndWorkingMessage(String var1) {
   }
}
