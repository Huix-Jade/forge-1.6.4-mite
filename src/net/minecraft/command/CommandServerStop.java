package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandServerStop extends CommandBase {
   public String getCommandName() {
      return "stop";
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.stop.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      notifyAdmins(par1ICommandSender, "commands.stop.start", new Object[0]);
      MinecraftServer.getServer().save_world_maps_on_shutdown = MinecraftServer.getServer().isServerSideMappingEnabled();
      MinecraftServer.getServer().initiateShutdown();
   }
}
