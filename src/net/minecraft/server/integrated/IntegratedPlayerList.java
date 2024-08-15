package net.minecraft.server.integrated;

import java.net.SocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

public class IntegratedPlayerList extends ServerConfigurationManager {
   private NBTTagCompound hostPlayerData;

   public IntegratedPlayerList(IntegratedServer var1) {
      super(var1);
      this.viewDistance = 10;
   }

   protected void writePlayerData(EntityPlayerMP var1) {
      if (var1.getCommandSenderName().equals(this.getIntegratedServer().getServerOwner())) {
         this.hostPlayerData = new NBTTagCompound();
         var1.writeToNBT(this.hostPlayerData);
      }

      super.writePlayerData(var1);
   }

   public String allowUserToConnect(SocketAddress var1, String var2) {
      return var2.equalsIgnoreCase(this.getIntegratedServer().getServerOwner()) ? "That name is already taken." : super.allowUserToConnect(var1, var2);
   }

   public IntegratedServer getIntegratedServer() {
      return (IntegratedServer)super.getServerInstance();
   }

   public NBTTagCompound getHostPlayerData() {
      return this.hostPlayerData;
   }

   // $FF: synthetic method
   public MinecraftServer getServerInstance() {
      return this.getIntegratedServer();
   }
}
