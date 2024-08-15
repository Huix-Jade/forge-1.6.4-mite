package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

public class CommandServerBanlist extends CommandBase {
   public String getCommandName() {
      return "banlist";
   }

   public int getRequiredPermissionLevel() {
      return 3;
   }

   public boolean canCommandSenderUseCommand(ICommandSender var1) {
      return (MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isListActive() || MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isListActive()) && super.canCommandSenderUseCommand(var1);
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.banlist.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length >= 1 && var2[0].equalsIgnoreCase("ips")) {
         var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.banlist.ips", MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getBannedList().size()));
         var1.sendChatToPlayer(ChatMessageComponent.createFromText(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getBannedList().keySet().toArray())));
      } else {
         var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.banlist.players", MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getBannedList().size()));
         var1.sendChatToPlayer(ChatMessageComponent.createFromText(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getBannedList().keySet().toArray())));
      }

   }

   public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, new String[]{"players", "ips"}) : null;
   }
}
