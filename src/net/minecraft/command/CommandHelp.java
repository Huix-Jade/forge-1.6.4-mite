package net.minecraft.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class CommandHelp extends CommandBase {
   public String getCommandName() {
      return "help";
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.help.usage";
   }

   public List getCommandAliases() {
      return Arrays.asList("?");
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      List var3 = this.getSortedPossibleCommands(var1);
      byte var4 = 7;
      int var5 = (var3.size() - 1) / var4;
      boolean var6 = false;

      ICommand var9;
      int var11;
      try {
         var11 = var2.length == 0 ? 0 : parseIntBounded(var1, var2[0], 1, var5 + 1) - 1;
      } catch (NumberInvalidException var10) {
         Map var8 = this.getCommands();
         var9 = (ICommand)var8.get(var2[0]);
         if (var9 != null) {
            throw new WrongUsageException(var9.getCommandUsage(var1), new Object[0]);
         }

         throw new CommandNotFoundException();
      }

      int var7 = Math.min((var11 + 1) * var4, var3.size());
      var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.help.header", var11 + 1, var5 + 1).setColor(EnumChatFormatting.DARK_GREEN));

      for(int var12 = var11 * var4; var12 < var7; ++var12) {
         var9 = (ICommand)var3.get(var12);
         var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(var9.getCommandUsage(var1)));
      }

      if (var11 == 0 && var1 instanceof EntityPlayer) {
         var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.help.footer").setColor(EnumChatFormatting.GREEN));
      }

   }

   protected List getSortedPossibleCommands(ICommandSender var1) {
      List var2 = MinecraftServer.getServer().getCommandManager().getPossibleCommands(var1);
      Collections.sort(var2);
      return var2;
   }

   protected Map getCommands() {
      return MinecraftServer.getServer().getCommandManager().getCommands();
   }
}
