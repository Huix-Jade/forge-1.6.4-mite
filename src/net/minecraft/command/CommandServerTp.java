package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandServerTp extends CommandBase {
   public String getCommandName() {
      return "tp";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.tp.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      notifyAdmins(par1ICommandSender, "Command '" + this.getCommandName() + "' not available", new Object[0]);
   }

   public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      return par2ArrayOfStr.length != 1 && par2ArrayOfStr.length != 2 ? null : getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
   }

   public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
      return par2 == 0;
   }
}
