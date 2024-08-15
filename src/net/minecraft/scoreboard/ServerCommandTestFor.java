package net.minecraft.scoreboard;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.tileentity.TileEntityCommandBlock;

public class ServerCommandTestFor extends CommandBase {
   public String getCommandName() {
      return "testfor";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender var1) {
      return "commands.testfor.usage";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      if (var2.length != 1) {
         throw new WrongUsageException("commands.testfor.usage", new Object[0]);
      } else if (!(var1 instanceof TileEntityCommandBlock)) {
         throw new CommandException("commands.testfor.failed", new Object[0]);
      } else {
         getPlayer(var1, var2[0]);
      }
   }

   public boolean isUsernameIndex(String[] var1, int var2) {
      return var2 == 0;
   }
}
