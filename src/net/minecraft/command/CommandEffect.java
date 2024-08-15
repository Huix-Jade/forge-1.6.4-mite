package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandEffect extends CommandBase {
   public String getCommandName() {
      return "effect";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.effect.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      notifyAdmins(par1ICommandSender, "Command '" + this.getCommandName() + "' not available", new Object[0]);
   }

   public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getAllUsernames()) : null;
   }

   protected String[] getAllUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
      return par2 == 0;
   }
}
