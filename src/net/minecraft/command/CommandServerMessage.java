package net.minecraft.command;

import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class CommandServerMessage extends CommandBase {
   public List getCommandAliases() {
      return Arrays.asList("w", "msg");
   }

   public String getCommandName() {
      return "tell";
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.message.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.message.usage", new Object[0]);
      } else {
         EntityPlayerMP var3 = getPlayer(var1, var2[0]);
         if (var3 == null) {
            throw new PlayerNotFoundException();
         } else if (var3 == var1) {
            throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);
         } else {
            String var4 = func_82361_a(var1, var2, 1, !(var1 instanceof EntityPlayer));
            var3.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.message.display.incoming", var1.getCommandSenderName(), var4).setColor(EnumChatFormatting.GRAY).setItalic(true));
            var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.message.display.outgoing", var3.getCommandSenderName(), var4).setColor(EnumChatFormatting.GRAY).setItalic(true));
         }
      }
   }

   public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
      return getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
   }

   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}
