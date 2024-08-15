package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

public class CommandServerSay extends CommandBase {
   public String getCommandName() {
      return "say";
   }

   public int getRequiredPermissionLevel() {
      return 1;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.say.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length > 0 && var2[0].length() > 0) {
         String var3 = func_82361_a(var1, var2, 0, true);
         MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromTranslationWithSubstitutions("chat.type.announcement", var1.getCommandSenderName(), var3));
      } else {
         throw new WrongUsageException("commands.say.usage", new Object[0]);
      }
   }

   public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
      return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
   }
}
