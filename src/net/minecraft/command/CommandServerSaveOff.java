package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandServerSaveOff extends CommandBase {
   public String getCommandName() {
      return "save-off";
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.save-off.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      MinecraftServer var3 = MinecraftServer.getServer();
      boolean var4 = false;

      for(int var5 = 0; var5 < var3.worldServers.length; ++var5) {
         if (var3.worldServers[var5] != null) {
            WorldServer var6 = var3.worldServers[var5];
            if (!var6.canNotSave) {
               var6.canNotSave = true;
               var4 = true;
            }
         }
      }

      if (var4) {
         notifyAdmins(var1, "commands.save.disabled", new Object[0]);
      } else {
         throw new CommandException("commands.save-off.alreadyOff", new Object[0]);
      }
   }
}
