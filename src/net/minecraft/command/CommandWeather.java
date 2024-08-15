package net.minecraft.command;

import java.util.List;

public class CommandWeather extends CommandBase {
   public String getCommandName() {
      return "weather";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.weather.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      throw new WrongUsageException("commands.weather.usage", new Object[0]);
   }

   public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[]{"clear", "rain", "thunder"}) : null;
   }
}
