package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

public class CommandServerList extends CommandBase {
   public String getCommandName() {
      return "list";
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.players.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.players.list", MinecraftServer.getServer().getCurrentPlayerCount(), MinecraftServer.getServer().getMaxPlayers()));
      var1.sendChatToPlayer(ChatMessageComponent.createFromText(MinecraftServer.getServer().getConfigurationManager().getPlayerListAsString()));
   }
}
