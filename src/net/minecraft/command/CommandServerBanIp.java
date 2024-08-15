package net.minecraft.command;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;

public class CommandServerBanIp extends CommandBase {
   public static final Pattern IPv4Pattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

   public String getCommandName() {
      return "ban-ip";
   }

   public int getRequiredPermissionLevel() {
      return 3;
   }

   public boolean canCommandSenderUseCommand(ICommandSender var1) {
      return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isListActive() && super.canCommandSenderUseCommand(var1);
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.banip.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length >= 1 && var2[0].length() > 1) {
         Matcher var3 = IPv4Pattern.matcher(var2[0]);
         String var4 = null;
         if (var2.length >= 2) {
            var4 = func_82360_a(var1, var2, 1);
         }

         if (var3.matches()) {
            this.banIP(var1, var2[0], var4);
         } else {
            EntityPlayerMP var5 = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var2[0]);
            if (var5 == null) {
               throw new PlayerNotFoundException("commands.banip.invalid", new Object[0]);
            }

            this.banIP(var1, var5.getPlayerIP(), var4);
         }

      } else {
         throw new WrongUsageException("commands.banip.usage", new Object[0]);
      }
   }

   public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
   }

   protected void banIP(ICommandSender var1, String var2, String var3) {
      BanEntry var4 = new BanEntry(var2);
      var4.setBannedBy(var1.getCommandSenderName());
      if (var3 != null) {
         var4.setBanReason(var3);
      }

      MinecraftServer.getServer().getConfigurationManager().getBannedIPs().put(var4);
      List var5 = MinecraftServer.getServer().getConfigurationManager().getPlayerList(var2);
      String[] var6 = new String[var5.size()];
      int var7 = 0;

      EntityPlayerMP var9;
      for(Iterator var8 = var5.iterator(); var8.hasNext(); var6[var7++] = var9.getEntityName()) {
         var9 = (EntityPlayerMP)var8.next();
         var9.playerNetServerHandler.kickPlayerFromServer("You have been IP banned.");
      }

      if (var5.isEmpty()) {
         notifyAdmins(var1, "commands.banip.success", new Object[]{var2});
      } else {
         notifyAdmins(var1, "commands.banip.success.players", new Object[]{var2, joinNiceString(var6)});
      }

   }
}
