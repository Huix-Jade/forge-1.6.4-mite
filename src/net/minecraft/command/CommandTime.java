package net.minecraft.command;

import java.util.List;

public class CommandTime extends CommandBase {
   public String getCommandName() {
      return "time";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.time.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      notifyAdmins(par1ICommandSender, "Command '" + this.getCommandName() + "' not available", new Object[0]);
   }

   public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[]{"set", "add"}) : (par2ArrayOfStr.length == 2 && par2ArrayOfStr[0].equals("set") ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[]{"day", "night"}) : null);
   }

   protected void setTime(ICommandSender par1ICommandSender, int par2) {
   }

   protected void addTime(ICommandSender par1ICommandSender, int par2) {
   }
}
