package net.minecraft.command;

import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public interface ICommandSender {
   String getCommandSenderName();

   void sendChatToPlayer(ChatMessageComponent var1);

   boolean canCommandSenderUseCommand(int var1, String var2);

   ChunkCoordinates getPlayerCoordinates();

   World getEntityWorld();
}
