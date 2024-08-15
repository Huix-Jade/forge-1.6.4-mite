package net.minecraft.command;

import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumGameType;

public class CommandDefaultGameMode extends CommandGameMode {
   public String getCommandName() {
      return "defaultgamemode";
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.defaultgamemode.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      notifyAdmins(par1ICommandSender, "Command '" + this.getCommandName() + "' not available", new Object[0]);
   }

   protected void setGameType(EnumGameType par1EnumGameType) {
      MinecraftServer var2 = MinecraftServer.getServer();
      var2.setGameType(par1EnumGameType);
      EntityPlayerMP var4;
      if (var2.getForceGamemode()) {
         for(Iterator var3 = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator(); var3.hasNext(); var4.fallDistance = 0.0F) {
            var4 = (EntityPlayerMP)var3.next();
            var4.setGameType(par1EnumGameType);
         }
      }

   }
}
