package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumGameType;

public class CommandServerPublishLocal extends CommandBase {
   public String getCommandName() {
      return "publish";
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.publish.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      String var3 = MinecraftServer.getServer().shareToLAN(EnumGameType.SURVIVAL, false);
      if (var3 != null) {
         notifyAdmins(var1, "commands.publish.started", new Object[]{var3});
      } else {
         notifyAdmins(var1, "commands.publish.failed", new Object[0]);
      }

   }
}
