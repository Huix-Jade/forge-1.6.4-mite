package net.minecraft.command;

import net.minecraft.client.Minecraft;

public class CommandToggleDownfall extends CommandBase {
   public String getCommandName() {
      return "toggledownfall";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.downfall.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      this.toggleDownfall();
      notifyAdmins(par1ICommandSender, "commands.downfall.success", new Object[0]);
   }

   protected void toggleDownfall() {
      Minecraft.setErrorMessage("toggleDownfall: called?");
   }
}
