package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandServerSaveAll extends CommandBase {
   public String getCommandName() {
      return "save-all";
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.save.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      MinecraftServer var3 = MinecraftServer.getServer();
      var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.save.start"));
      if (var3.getConfigurationManager() != null) {
         var3.getConfigurationManager().saveAllPlayerData();
      }

      try {
         int var4;
         WorldServer var5;
         boolean var6;
         for(var4 = 0; var4 < var3.worldServers.length; ++var4) {
            if (var3.worldServers[var4] != null) {
               var5 = var3.worldServers[var4];
               var6 = var5.canNotSave;
               var5.canNotSave = false;
               var5.saveAllChunks(true, (IProgressUpdate)null);
               var5.canNotSave = var6;
            }
         }

         if (var2.length > 0 && "flush".equals(var2[0])) {
            var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.save.flushStart"));

            for(var4 = 0; var4 < var3.worldServers.length; ++var4) {
               if (var3.worldServers[var4] != null) {
                  var5 = var3.worldServers[var4];
                  var6 = var5.canNotSave;
                  var5.canNotSave = false;
                  var5.saveChunkData();
                  var5.canNotSave = var6;
               }
            }

            var1.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.save.flushEnd"));
         }
      } catch (MinecraftException var7) {
         notifyAdmins(var1, "commands.save.failed", new Object[]{var7.getMessage()});
         return;
      }

      notifyAdmins(var1, "commands.save.success", new Object[0]);
   }
}
