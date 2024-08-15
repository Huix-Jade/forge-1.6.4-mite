package net.minecraft.command;

import java.util.List;

public class CommandDifficulty extends CommandBase {
   private static final String[] difficulties = new String[]{"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};

   public String getCommandName() {
      return "difficulty";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.difficulty.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      notifyAdmins(par1ICommandSender, "Command '" + this.getCommandName() + "' not available", new Object[0]);
   }

   protected int getDifficultyForName(ICommandSender par1ICommandSender, String par2Str) {
      return !par2Str.equalsIgnoreCase("peaceful") && !par2Str.equalsIgnoreCase("p") ? (!par2Str.equalsIgnoreCase("easy") && !par2Str.equalsIgnoreCase("e") ? (!par2Str.equalsIgnoreCase("normal") && !par2Str.equalsIgnoreCase("n") ? (!par2Str.equalsIgnoreCase("hard") && !par2Str.equalsIgnoreCase("h") ? parseIntBounded(par1ICommandSender, par2Str, 0, 3) : 3) : 2) : 1) : 0;
   }

   public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[]{"peaceful", "easy", "normal", "hard"}) : null;
   }
}
