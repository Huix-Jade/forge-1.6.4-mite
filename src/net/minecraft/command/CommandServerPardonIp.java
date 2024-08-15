package net.minecraft.command;

import java.util.List;
import java.util.regex.Matcher;
import net.minecraft.server.MinecraftServer;

public class CommandServerPardonIp extends CommandBase {
   public String getCommandName() {
      return "pardon-ip";
   }

   public int getRequiredPermissionLevel() {
      return 3;
   }

   public boolean canCommandSenderUseCommand(ICommandSender var1) {
      return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isListActive() && super.canCommandSenderUseCommand(var1);
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.unbanip.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length == 1 && var2[0].length() > 1) {
         Matcher var3 = CommandServerBanIp.IPv4Pattern.matcher(var2[0]);
         if (var3.matches()) {
            MinecraftServer.getServer().getConfigurationManager().getBannedIPs().remove(var2[0]);
            notifyAdmins(var1, "commands.unbanip.success", new Object[]{var2[0]});
         } else {
            throw new SyntaxErrorException("commands.unbanip.invalid", new Object[0]);
         }
      } else {
         throw new WrongUsageException("commands.unbanip.usage", new Object[0]);
      }
   }

   public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? getListOfStringsFromIterableMatchingLastWord(var2, MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getBannedList().keySet()) : null;
   }
}
