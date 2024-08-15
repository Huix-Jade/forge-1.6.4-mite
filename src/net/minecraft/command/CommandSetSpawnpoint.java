package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

public class CommandSetSpawnpoint extends CommandBase {
   public String getCommandName() {
      return "spawnpoint";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.spawnpoint.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      EntityPlayerMP var3 = par2ArrayOfStr.length == 0 ? getCommandSenderAsPlayer(par1ICommandSender) : getPlayer(par1ICommandSender, par2ArrayOfStr[0]);
      if (par2ArrayOfStr.length == 4) {
         if (var3.worldObj != null) {
            byte var4 = 1;
            int var10 = var4 + 1;
            int min_xz = MathHelper.floor_double(var3.worldObj.min_entity_pos_xz);
            int max_xz = MathHelper.floor_double(var3.worldObj.max_entity_pos_xz);
            int var6 = parseIntBounded(par1ICommandSender, par2ArrayOfStr[var4], min_xz, max_xz);
            int var7 = parseIntBounded(par1ICommandSender, par2ArrayOfStr[var10++], 0, 256);
            int var8 = parseIntBounded(par1ICommandSender, par2ArrayOfStr[var10++], min_xz, max_xz);
            var3.setSpawnChunk(new ChunkCoordinates(var6, var7, var8), true);
            notifyAdmins(par1ICommandSender, "commands.spawnpoint.success", new Object[]{var3.getEntityName(), var6, var7, var8});
         }
      } else {
         if (par2ArrayOfStr.length > 1) {
            throw new WrongUsageException("commands.spawnpoint.usage", new Object[0]);
         }

         ChunkCoordinates var11 = var3.getPlayerCoordinates();
         var3.setSpawnChunk(var11, true);
         notifyAdmins(par1ICommandSender, "commands.spawnpoint.success", new Object[]{var3.getEntityName(), var11.posX, var11.posY, var11.posZ});
      }

   }

   public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      return par2ArrayOfStr.length != 1 && par2ArrayOfStr.length != 2 ? null : getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
   }

   public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
      return par2 == 0;
   }
}
