package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandServerPardon extends CommandBase {
   public String getCommandName() {
      return "pardon";
   }

   public int getRequiredPermissionLevel() {
      return 3;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.unban.usage";
   }

   public boolean canCommandSenderUseCommand(ICommandSender var1) {
      return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isListActive() && super.canCommandSenderUseCommand(var1);
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().remove(var2[0]);
         notifyAdmins(var1, "commands.unban.success", new Object[]{var2[0]});
      } else {
         throw new WrongUsageException("commands.unban.usage", new Object[0]);
      }
   }

   public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? getListOfStringsFromIterableMatchingLastWord(var2, MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getBannedList().keySet()) : null;
   }
}
