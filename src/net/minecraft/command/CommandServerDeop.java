package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandServerDeop extends CommandBase {
   public String getCommandName() {
      return "deop";
   }

   public int getRequiredPermissionLevel() {
      return 3;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.deop.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer.getServer().getConfigurationManager().removeOp(var2[0]);
         notifyAdmins(var1, "commands.deop.success", new Object[]{var2[0]});
      } else {
         throw new WrongUsageException("commands.deop.usage", new Object[0]);
      }
   }

   public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? getListOfStringsFromIterableMatchingLastWord(var2, MinecraftServer.getServer().getConfigurationManager().getOps()) : null;
   }
}
