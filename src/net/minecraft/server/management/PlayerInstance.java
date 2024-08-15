package net.minecraft.server.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.mite.MITEConstant;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.network.packet.Packet52MultiBlockChange;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.network.packet.Packet97MultiBlockChange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

class PlayerInstance {
   private final List playersInChunk;
   private final ChunkCoordIntPair chunkLocation;
   private short[] locationOfBlockChange;
   private int numberOfTilesToUpdate;
   private int flagsYAreasToUpdate;
   private long previousWorldTime;
   final PlayerManager thePlayerManager;

   public PlayerInstance(PlayerManager par1PlayerManager, int par2, int par3) {
      this.thePlayerManager = par1PlayerManager;
      this.playersInChunk = new ArrayList();
      this.locationOfBlockChange = new short[64];
      this.chunkLocation = new ChunkCoordIntPair(par2, par3);
      par1PlayerManager.getWorldServer().theChunkProviderServer.loadChunk(par2, par3);
   }

   public void addPlayer(EntityPlayerMP par1EntityPlayerMP) {
      if (this.playersInChunk.contains(par1EntityPlayerMP)) {
         throw new IllegalStateException("Failed to add player. " + par1EntityPlayerMP + " already is in chunk " + this.chunkLocation.chunkXPos + ", " + this.chunkLocation.chunkZPos);
      } else {
         if (this.playersInChunk.isEmpty()) {
            this.previousWorldTime = PlayerManager.getWorldServer(this.thePlayerManager).getTotalWorldTime();
         }

         this.playersInChunk.add(par1EntityPlayerMP);
         par1EntityPlayerMP.loadedChunks.add(this.chunkLocation);
      }
   }

   public void removePlayer(EntityPlayerMP par1EntityPlayerMP) {
      if (this.playersInChunk.contains(par1EntityPlayerMP)) {
         Chunk var2 = PlayerManager.getWorldServer(this.thePlayerManager).getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);
         par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet51MapChunk(var2, true, 0));
         this.playersInChunk.remove(par1EntityPlayerMP);
         par1EntityPlayerMP.loadedChunks.remove(this.chunkLocation);
         if (this.playersInChunk.isEmpty()) {
            long var3 = (long)this.chunkLocation.chunkXPos + 2147483647L | (long)this.chunkLocation.chunkZPos + 2147483647L << 32;
            this.increaseInhabitedTime(var2);
            PlayerManager.getChunkWatchers(this.thePlayerManager).remove(var3);
            PlayerManager.getChunkWatcherList(this.thePlayerManager).remove(this);
            if (this.numberOfTilesToUpdate > 0) {
               PlayerManager.getChunkWatchersWithPlayers(this.thePlayerManager).remove(this);
            }

            this.thePlayerManager.getWorldServer().theChunkProviderServer.unloadChunksIfNotNearSpawn(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);
         }
      }

   }

   public void processChunk() {
      this.increaseInhabitedTime(PlayerManager.getWorldServer(this.thePlayerManager).getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos));
   }

   private void increaseInhabitedTime(Chunk par1Chunk) {
      par1Chunk.inhabitedTime += PlayerManager.getWorldServer(this.thePlayerManager).getTotalWorldTime() - this.previousWorldTime;
      this.previousWorldTime = PlayerManager.getWorldServer(this.thePlayerManager).getTotalWorldTime();
   }

   public void flagChunkForUpdate(int par1, int par2, int par3) {
      if (this.numberOfTilesToUpdate == 0) {
         PlayerManager.getChunkWatchersWithPlayers(this.thePlayerManager).add(this);
      }

      this.flagsYAreasToUpdate |= 1 << (par2 >> 4);
      if (this.numberOfTilesToUpdate < 64) {
         short var4 = (short)(par1 << 12 | par3 << 8 | par2);

         for(int var5 = 0; var5 < this.numberOfTilesToUpdate; ++var5) {
            if (this.locationOfBlockChange[var5] == var4) {
               return;
            }
         }

         this.locationOfBlockChange[this.numberOfTilesToUpdate++] = var4;
      }

   }

   public void sendToAllPlayersWatchingChunk(Packet par1Packet) {
      for(int var2 = 0; var2 < this.playersInChunk.size(); ++var2) {
         EntityPlayerMP var3 = (EntityPlayerMP)this.playersInChunk.get(var2);
         if (!var3.loadedChunks.contains(this.chunkLocation)) {
            var3.playerNetServerHandler.sendPacketToPlayer(par1Packet);
         }
      }

   }

   public void sendChunkUpdate() {
      if (MITEConstant.usePacket51ForLargePacket52s()) {
         this.sendChunkUpdateMITE();
      } else {
         if (this.numberOfTilesToUpdate != 0) {
            int var1;
            int var2;
            int var3;
            if (this.numberOfTilesToUpdate == 1) {
               var1 = this.chunkLocation.chunkXPos * 16 + (this.locationOfBlockChange[0] >> 12 & 15);
               var2 = this.locationOfBlockChange[0] & 255;
               var3 = this.chunkLocation.chunkZPos * 16 + (this.locationOfBlockChange[0] >> 8 & 15);
               this.sendToAllPlayersWatchingChunk(new Packet53BlockChange(var1, var2, var3, PlayerManager.getWorldServer(this.thePlayerManager)));
               if (PlayerManager.getWorldServer(this.thePlayerManager).blockHasTileEntity(var1, var2, var3)) {
                  this.sendTileToAllPlayersWatchingChunk(PlayerManager.getWorldServer(this.thePlayerManager).getBlockTileEntity(var1, var2, var3));
               }
            } else {
               int var4;
               if (this.numberOfTilesToUpdate != 64) {
                  if (MITEConstant.usePacket97MultiBlockChange()) {
                     this.sendToAllPlayersWatchingChunk(new Packet97MultiBlockChange(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos, this.locationOfBlockChange, this.numberOfTilesToUpdate, PlayerManager.getWorldServer(this.thePlayerManager)));
                  } else {
                     this.sendToAllPlayersWatchingChunk(new Packet52MultiBlockChange(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos, this.locationOfBlockChange, this.numberOfTilesToUpdate, PlayerManager.getWorldServer(this.thePlayerManager)));
                  }

                  for(var1 = 0; var1 < this.numberOfTilesToUpdate; ++var1) {
                     var2 = this.chunkLocation.chunkXPos * 16 + (this.locationOfBlockChange[var1] >> 12 & 15);
                     var3 = this.locationOfBlockChange[var1] & 255;
                     var4 = this.chunkLocation.chunkZPos * 16 + (this.locationOfBlockChange[var1] >> 8 & 15);
                     if (PlayerManager.getWorldServer(this.thePlayerManager).blockHasTileEntity(var2, var3, var4)) {
                        this.sendTileToAllPlayersWatchingChunk(PlayerManager.getWorldServer(this.thePlayerManager).getBlockTileEntity(var2, var3, var4));
                     }
                  }
               } else {
                  var1 = this.chunkLocation.chunkXPos * 16;
                  var2 = this.chunkLocation.chunkZPos * 16;
                  this.sendToAllPlayersWatchingChunk(new Packet51MapChunk(PlayerManager.getWorldServer(this.thePlayerManager).getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos), false, this.flagsYAreasToUpdate));

                  for(var3 = 0; var3 < 16; ++var3) {
                     if ((this.flagsYAreasToUpdate & 1 << var3) != 0) {
                        var4 = var3 << 4;
                        List var5 = PlayerManager.getWorldServer(this.thePlayerManager).getAllTileEntityInBox(var1, var4, var2, var1 + 16, var4 + 16, var2 + 16);

                        for(int var6 = 0; var6 < var5.size(); ++var6) {
                           this.sendTileToAllPlayersWatchingChunk((TileEntity)var5.get(var6));
                        }
                     }
                  }
               }
            }

            this.numberOfTilesToUpdate = 0;
            this.flagsYAreasToUpdate = 0;
         }

      }
   }

   private void sendChunkUpdateMITE() {
      if (this.numberOfTilesToUpdate != 0) {
         int var1;
         int var2;
         int var3;
         if (this.numberOfTilesToUpdate == 1) {
            var1 = this.chunkLocation.chunkXPos * 16 + (this.locationOfBlockChange[0] >> 12 & 15);
            var2 = this.locationOfBlockChange[0] & 255;
            var3 = this.chunkLocation.chunkZPos * 16 + (this.locationOfBlockChange[0] >> 8 & 15);
            this.sendToAllPlayersWatchingChunk(new Packet53BlockChange(var1, var2, var3, PlayerManager.getWorldServer(this.thePlayerManager)));
            if (PlayerManager.getWorldServer(this.thePlayerManager).blockHasTileEntity(var1, var2, var3)) {
               this.sendTileToAllPlayersWatchingChunk(PlayerManager.getWorldServer(this.thePlayerManager).getBlockTileEntity(var1, var2, var3));
            }
         } else {
            WorldServer world_server = PlayerManager.getWorldServer(this.thePlayerManager);
            Packet51MapChunk packet_51 = null;
            Packet52MultiBlockChange packet_52 = null;
            boolean is_dedicated_server = MinecraftServer.getServer().isDedicatedServer();
            Iterator i = this.playersInChunk.iterator();

            label85:
            while(true) {
               while(true) {
                  EntityPlayerMP player;
                  do {
                     if (!i.hasNext()) {
                        break label85;
                     }

                     player = (EntityPlayerMP)i.next();
                  } while(player.loadedChunks.contains(this.chunkLocation));

                  boolean prevent_packet_52;
                  int var6;
                  if (this.numberOfTilesToUpdate != 64 && this.numberOfTilesToUpdate > 8) {
                     int x = this.chunkLocation.chunkXPos * 16 + 8;
                     var6 = this.chunkLocation.chunkZPos * 16 + 8;
                     int dx = x - (int)player.posX;
                     int dz = var6 - (int)player.posZ;
                     long distance_sq_to_player = (long)(dx * dx + dz * dz);
                     prevent_packet_52 = distance_sq_to_player > 16384L;
                  } else {
                     prevent_packet_52 = false;
                  }

                  int var4;
                  if (this.numberOfTilesToUpdate != 64 && !prevent_packet_52) {
                     if (packet_52 == null) {
                        packet_52 = new Packet52MultiBlockChange(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos, this.locationOfBlockChange, this.numberOfTilesToUpdate, PlayerManager.getWorldServer(this.thePlayerManager));
                     }

                     player.playerNetServerHandler.sendPacketToPlayer(packet_52);

                     for(var1 = 0; var1 < this.numberOfTilesToUpdate; ++var1) {
                        var2 = this.chunkLocation.chunkXPos * 16 + (this.locationOfBlockChange[var1] >> 12 & 15);
                        var3 = this.locationOfBlockChange[var1] & 255;
                        var4 = this.chunkLocation.chunkZPos * 16 + (this.locationOfBlockChange[var1] >> 8 & 15);
                        if (world_server.blockHasTileEntity(var2, var3, var4)) {
                           this.sendTileToPlayer(world_server.getBlockTileEntity(var2, var3, var4), player);
                        }
                     }
                  } else {
                     var1 = this.chunkLocation.chunkXPos * 16;
                     var2 = this.chunkLocation.chunkZPos * 16;
                     if (packet_51 == null) {
                        packet_51 = new Packet51MapChunk(PlayerManager.getWorldServer(this.thePlayerManager).getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos), false, this.flagsYAreasToUpdate);
                     }

                     player.playerNetServerHandler.sendPacketToPlayer(packet_51);

                     for(var3 = 0; var3 < 16; ++var3) {
                        if ((this.flagsYAreasToUpdate & 1 << var3) != 0) {
                           var4 = var3 << 4;
                           List var5 = world_server.getAllTileEntityInBox(var1, var4, var2, var1 + 16, var4 + 16, var2 + 16);

                           for(var6 = 0; var6 < var5.size(); ++var6) {
                              this.sendTileToPlayer((TileEntity)var5.get(var6), player);
                           }
                        }
                     }
                  }
               }
            }
         }

         this.numberOfTilesToUpdate = 0;
         this.flagsYAreasToUpdate = 0;
      }

   }

   private void sendTileToAllPlayersWatchingChunk(TileEntity par1TileEntity) {
      if (par1TileEntity != null) {
         Packet var2 = par1TileEntity.getDescriptionPacket();
         if (var2 != null) {
            this.sendToAllPlayersWatchingChunk(var2);
         }
      }

   }

   private void sendTileToPlayer(TileEntity tile_entity, EntityPlayerMP player) {
      if (tile_entity != null) {
         Packet packet = tile_entity.getDescriptionPacket();
         if (packet != null) {
            player.playerNetServerHandler.sendPacketToPlayer(packet);
         }

      }
   }

   static ChunkCoordIntPair getChunkLocation(PlayerInstance par0PlayerInstance) {
      return par0PlayerInstance.chunkLocation;
   }

   static List getPlayersInChunk(PlayerInstance par0PlayerInstance) {
      return par0PlayerInstance.playersInChunk;
   }
}
