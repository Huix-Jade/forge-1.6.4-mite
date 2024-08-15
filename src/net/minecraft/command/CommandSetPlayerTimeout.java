package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandSetPlayerTimeout extends CommandBase {
   public String getCommandName() {
      return "setidletimeout";
   }

   public int getRequiredPermissionLevel() {
      return 3;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.setidletimeout.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         int var3 = parseIntWithMin(var1, var2[0], 0);
         MinecraftServer.getServer().func_143006_e(var3);
         notifyAdmins(var1, "commands.setidletimeout.success", new Object[]{var3});
      } else {
         throw new WrongUsageException("commands.setidletimeout.usage", new Object[0]);
      }
   }
}
