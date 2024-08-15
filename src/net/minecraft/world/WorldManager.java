package net.minecraft.world;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet55BlockDestroy;
import net.minecraft.network.packet.Packet61DoorChange;
import net.minecraft.network.packet.Packet62LevelSound;
import net.minecraft.network.packet.Packet80LongDistanceSound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticle;

public class WorldManager implements IWorldAccess {
   private MinecraftServer mcServer;
   private WorldServer theWorldServer;

   public WorldManager(MinecraftServer par1MinecraftServer, WorldServer par2WorldServer) {
      this.mcServer = par1MinecraftServer;
      this.theWorldServer = par2WorldServer;
   }

   public void spawnParticle(EnumParticle enum_paticle, double par2, double par4, double par6, double par8, double par10, double par12) {
   }

   public void spawnParticleEx(EnumParticle enum_paticle, int index, int data, double par2, double par4, double par6, double par8, double par10, double par12) {
   }

   public void onEntityCreate(Entity par1Entity) {
      this.theWorldServer.getEntityTracker().addEntityToTracker(par1Entity);
      par1Entity.detectAndRemoveDuplicateEntities();
   }

   public void onEntityDestroy(Entity par1Entity) {
      this.theWorldServer.getEntityTracker().removeEntityFromAllTrackingPlayers(par1Entity);
   }

   public void playSound(String par1Str, double par2, double par4, double par6, float par8, float par9) {
      this.mcServer.getConfigurationManager().sendToAllNear(par2, par4, par6, par8 > 1.0F ? (double)(16.0F * par8) : 16.0, this.theWorldServer.provider.dimensionId, new Packet62LevelSound(par1Str, par2, par4, par6, par8, par9));
   }

   public void playLongDistanceSound(String par1Str, double par2, double par4, double par6, float par8, float par9) {
      this.mcServer.getConfigurationManager().sendToAllOutdoorsNear(par2, par4, par6, 64.0, this.theWorldServer.provider.dimensionId, new Packet80LongDistanceSound(par1Str, par2, par4, par6, par8, par9));
   }

   public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, double par3, double par5, double par7, float par9, float par10) {
      this.mcServer.getConfigurationManager().sendToAllNearExcept(par1EntityPlayer, par3, par5, par7, par9 > 1.0F ? (double)(16.0F * par9) : 16.0, this.theWorldServer.provider.dimensionId, new Packet62LevelSound(par2Str, par3, par5, par7, par9, par10));
   }

   public void markBlockRangeForRenderUpdate(int par1, int par2, int par3, int par4, int par5, int par6) {
   }

   public void markBlockForUpdate(int par1, int par2, int par3) {
      this.theWorldServer.getPlayerManager().markBlockForUpdate(par1, par2, par3);
   }

   public void markBlockForRenderUpdate(int par1, int par2, int par3) {
   }

   public void playRecord(String par1Str, int par2, int par3, int par4) {
   }

   public void playAuxSFX(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6) {
      this.mcServer.getConfigurationManager().sendToAllNearExcept(par1EntityPlayer, (double)par3, (double)par4, (double)par5, 64.0, this.theWorldServer.provider.dimensionId, new Packet61DoorChange(par2, par3, par4, par5, par6, false));
   }

   public void broadcastSound(int par1, int par2, int par3, int par4, int par5) {
      this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet61DoorChange(par1, par2, par3, par4, par5, true));
   }

   public void destroyBlockPartially(int destroyer_entity_id, int x, int y, int z, int tenths_destroyed) {
      Iterator i = this.mcServer.getConfigurationManager().playerEntityList.iterator();

      while(i.hasNext()) {
         EntityPlayerMP player = (EntityPlayerMP)i.next();
         if (player != null && player.worldObj == this.theWorldServer && player.entityId != destroyer_entity_id) {
            double dx = (double)((float)x + 0.5F) - player.posX;
            double dy = (double)((float)y + 0.5F) - player.posY;
            double dz = (double)((float)z + 0.5F) - player.posZ;
            if (dx * dx + dy * dy + dz * dz < 1024.0) {
               player.playerNetServerHandler.sendPacketToPlayer(new Packet55BlockDestroy(destroyer_entity_id, x, y, z, tenths_destroyed));
            }
         }
      }

   }
}
