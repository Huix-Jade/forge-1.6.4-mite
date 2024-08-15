package net.minecraft.network.rcon;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender {
   public static final RConConsoleSource consoleBuffer = new RConConsoleSource();
   private StringBuffer buffer = new StringBuffer();

   public void resetLog() {
      this.buffer.setLength(0);
   }

   public String getChatBuffer() {
      return this.buffer.toString();
   }

   public String getCommandSenderName() {
      return "Rcon";
   }

   public void sendChatToPlayer(ChatMessageComponent var1) {
      this.buffer.append(var1.toString());
   }

   public boolean canCommandSenderUseCommand(int var1, String var2) {
      return true;
   }

   public ChunkCoordinates getPlayerCoordinates() {
      return new ChunkCoordinates(0, 0, 0);
   }

   public World getEntityWorld() {
      return MinecraftServer.getServer().getEntityWorld();
   }
}
