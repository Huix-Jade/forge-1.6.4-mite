package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumSignal;
import net.minecraft.world.World;

public class CommandShowSeed extends CommandBase {
   public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
      return MinecraftServer.getServer().isSinglePlayer() || super.canCommandSenderUseCommand(par1ICommandSender);
   }

   public String getCommandName() {
      return "seed";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getCommandUsage(ICommandSender par1ICommandSender) {
      return "commands.seed.usage";
   }

   public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
      Object var3 = par1ICommandSender instanceof EntityPlayer ? ((EntityPlayer)par1ICommandSender).worldObj : MinecraftServer.getServer().worldServerForDimension(0);
      par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.seed.success", ((World)var3).getSeed()));
      if (par1ICommandSender instanceof EntityPlayerMP) {
         ((EntityPlayerMP)par1ICommandSender).sendPacket(new Packet85SimpleSignal(EnumSignal.take_screenshot_of_world_seed));
      }

   }
}
