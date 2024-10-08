package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet62LevelSound;

public class CommandPlaySound extends CommandBase {
   public String getCommandName() {
      return "playsound";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.playsound.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      if (par2ArrayOfStr.length < 2) {
         throw new WrongUsageException(this.getCommandUsage(par1ICommandSender), new Object[0]);
      } else {
         byte var3 = 0;
         int var36 = var3 + 1;
         String var4 = par2ArrayOfStr[var3];
         EntityPlayerMP var5 = getPlayer(par1ICommandSender, par2ArrayOfStr[var36++]);
         double var6 = (double)var5.getPlayerCoordinates().posX;
         double var8 = (double)var5.getPlayerCoordinates().posY;
         double var10 = (double)var5.getPlayerCoordinates().posZ;
         double var12 = 1.0;
         double var14 = 1.0;
         double var16 = 0.0;
         if (par2ArrayOfStr.length > var36) {
            var6 = func_110666_a(par1ICommandSender, var6, par2ArrayOfStr[var36++], var5.worldObj.min_block_xz, var5.worldObj.max_block_xz);
         }

         if (par2ArrayOfStr.length > var36) {
            var8 = func_110665_a(par1ICommandSender, var8, par2ArrayOfStr[var36++], 0, 0);
         }

         if (par2ArrayOfStr.length > var36) {
            var10 = func_110666_a(par1ICommandSender, var10, par2ArrayOfStr[var36++], var5.worldObj.min_block_xz, var5.worldObj.max_block_xz);
         }

         if (par2ArrayOfStr.length > var36) {
            var12 = func_110661_a(par1ICommandSender, par2ArrayOfStr[var36++], 0.0, 3.4028234663852886E38);
         }

         if (par2ArrayOfStr.length > var36) {
            var14 = func_110661_a(par1ICommandSender, par2ArrayOfStr[var36++], 0.0, 2.0);
         }

         if (par2ArrayOfStr.length > var36) {
            var16 = func_110661_a(par1ICommandSender, par2ArrayOfStr[var36++], 0.0, 1.0);
         }

         double var18 = var12 > 1.0 ? var12 * 16.0 : 16.0;
         double var20 = var5.getDistance(var6, var8, var10);
         if (var20 > var18) {
            if (var16 <= 0.0) {
               throw new CommandException("commands.playsound.playerTooFar", new Object[]{var5.getEntityName()});
            }

            double var22 = var6 - var5.posX;
            double var24 = var8 - var5.posY;
            double var26 = var10 - var5.posZ;
            double var28 = Math.sqrt(var22 * var22 + var24 * var24 + var26 * var26);
            double var30 = var5.posX;
            double var32 = var5.posY;
            double var34 = var5.posZ;
            if (var28 > 0.0) {
               var30 += var22 / var28 * 2.0;
               var32 += var24 / var28 * 2.0;
               var34 += var26 / var28 * 2.0;
            }

            var5.playerNetServerHandler.sendPacketToPlayer(new Packet62LevelSound(var4, var30, var32, var34, (float)var16, (float)var14));
         } else {
            var5.playerNetServerHandler.sendPacketToPlayer(new Packet62LevelSound(var4, var6, var8, var10, (float)var12, (float)var14));
         }

         notifyAdmins(par1ICommandSender, "commands.playsound.success", new Object[]{var4, var5.getEntityName()});
      }
   }

   public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
      return par2 == 1;
   }
}
