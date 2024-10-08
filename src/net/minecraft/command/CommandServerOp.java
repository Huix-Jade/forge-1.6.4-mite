package net.minecraft.command;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandServerOp extends CommandBase {
   public String getCommandName() {
      return "op";
   }

   public int getRequiredPermissionLevel() {
      return 3;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.op.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      notifyAdmins(par1ICommandSender, "Command '" + this.getCommandName() + "' not available", new Object[0]);
   }

   public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      if (par2ArrayOfStr.length == 1) {
         String var3 = par2ArrayOfStr[par2ArrayOfStr.length - 1];
         ArrayList var4 = new ArrayList();
         String[] var5 = MinecraftServer.getServer().getAllUsernames();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            if (!MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(var8) && doesStringStartWith(var3, var8)) {
               var4.add(var8);
            }
         }

         return var4;
      } else {
         return null;
      }
   }
}
