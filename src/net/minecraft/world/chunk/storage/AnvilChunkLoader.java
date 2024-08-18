package net.minecraft.world.chunk.storage;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;

public final class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO {
   private List chunksToRemove = new ArrayList();
   private Set pendingAnvilChunksCoordinates = new HashSet();
   private Object syncLockObject = new Object();
   public final File chunkSaveLocation;
   public static final String vanilla_blocks_tag = "Blocks";
   public static final String new_blocks_tag = "BlockData";
   public static final String vanilla_entities_tag = "Entities";
   public static final String new_entities_tag = "EntityData";
   public static final String vanilla_tile_entities_tag = "TileEntities";
   public static final String new_tile_entities_tag = "TileEntityData";
   private static final int[][] invalid_section_conversion_data = getInvalidSectionBlockConversionIdsOrMetadata();

   public AnvilChunkLoader(File par1File) {
      this.chunkSaveLocation = par1File;
   }

   public Chunk loadChunk(World par1World, int par2, int par3) throws IOException {
      NBTTagCompound var4 = null;
      ChunkCoordIntPair var5 = new ChunkCoordIntPair(par2, par3);
//      Object var6 = this.syncLockObject;
      synchronized(this.syncLockObject) {
         if (this.pendingAnvilChunksCoordinates.contains(var5)) {
            for(int var7 = 0; var7 < this.chunksToRemove.size(); ++var7) {
               if (((AnvilChunkLoaderPending)this.chunksToRemove.get(var7)).chunkCoordinate.equals(var5)) {
                  var4 = ((AnvilChunkLoaderPending)this.chunksToRemove.get(var7)).nbtTags;
                  break;
               }
            }
         }
      }

      if (var4 == null) {
         DataInputStream var10 = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, par2, par3);
         if (var10 == null) {
            return null;
         }

         var4 = CompressedStreamTools.read((DataInput)var10);
      }

      return this.checkedReadChunkFromNBT(par1World, par2, par3, var4);
   }

   protected Chunk checkedReadChunkFromNBT(World par1World, int par2, int par3, NBTTagCompound par4NBTTagCompound) {
      if (!par4NBTTagCompound.hasKey("Level")) {
         par1World.getWorldLogAgent().logSevere("Chunk file at " + par2 + "," + par3 + " is missing level data, skipping");
         return null;
      } else if (!par4NBTTagCompound.getCompoundTag("Level").hasKey("Sections")) {
         par1World.getWorldLogAgent().logSevere("Chunk file at " + par2 + "," + par3 + " is missing block data, skipping");
         return null;
      } else {
         Chunk var5 = this.readChunkFromNBT(par1World, par4NBTTagCompound.getCompoundTag("Level"));
         if (!var5.isAtLocation(par2, par3)) {
            int x_position_from_disk = var5.xPosition;
            int z_position_from_disk = var5.zPosition;
            par1World.getWorldLogAgent().logSevere("Chunk file at " + par2 + "," + par3 + " is in the wrong location; relocating. (Expected " + par2 + ", " + par3 + ", got " + var5.xPosition + ", " + var5.zPosition + ")");
            par4NBTTagCompound.getCompoundTag("Level").setInteger("xPos", par2);
            par4NBTTagCompound.getCompoundTag("Level").setInteger("zPos", par3);
            var5 = this.readChunkFromNBT(par1World, par4NBTTagCompound.getCompoundTag("Level"));
            MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(var5, par4NBTTagCompound));
            if (!var5.isAtLocation(par2, par3)) {
               Minecraft.setErrorMessage("checkedReadChunkFromNBT: chunk relocation failed");
            } else {
               Minecraft.setErrorMessage("Warning: chunk was relocated from " + x_position_from_disk * 16 + "," + z_position_from_disk * 16 + " to " + par2 * 16 + "," + par3 * 16);
            }
         }

         return var5;
      }
   }

   public void saveChunk(World par1World, Chunk par2Chunk) throws MinecraftException, IOException {
      par1World.checkSessionLock();

      try {
         int x_position_before = par2Chunk.xPosition;
         int z_position_before = par2Chunk.zPosition;
         NBTTagCompound var3 = new NBTTagCompound();
         NBTTagCompound var4 = new NBTTagCompound();
         var3.setTag("Level", var4);
         MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Save(par2Chunk, var3));
         this.writeChunkToNBT(par2Chunk, par1World, var4);
         this.addChunkToPending(par2Chunk.getChunkCoordIntPair(), var3);
         if (par2Chunk.xPosition != x_position_before) {
            Minecraft.setErrorMessage("saveChunk: Discrepency condition 1");
         }

         if (par2Chunk.zPosition != z_position_before) {
            Minecraft.setErrorMessage("saveChunk: Discrepency condition 2");
         }

         if (var3.getCompoundTag("Level").getInteger("xPos") != par2Chunk.xPosition) {
            Minecraft.setErrorMessage("saveChunk: Discrepency condition 3");
         }

         if (var3.getCompoundTag("Level").getInteger("zPos") != par2Chunk.zPosition) {
            Minecraft.setErrorMessage("saveChunk: Discrepency condition 4");
         }

         if (var4.getInteger("xPos") != par2Chunk.xPosition) {
            Minecraft.setErrorMessage("saveChunk: Discrepency condition 5");
         }

         if (var4.getInteger("zPos") != par2Chunk.zPosition) {
            Minecraft.setErrorMessage("saveChunk: Discrepency condition 6");
         }
      } catch (Exception var7) {
         Exception var5 = var7;
         var5.printStackTrace();
      }

   }

   protected void addChunkToPending(ChunkCoordIntPair par1ChunkCoordIntPair, NBTTagCompound par2NBTTagCompound) {
      Object var3 = this.syncLockObject;
      synchronized(this.syncLockObject) {
         if (this.pendingAnvilChunksCoordinates.contains(par1ChunkCoordIntPair)) {
            for(int var4 = 0; var4 < this.chunksToRemove.size(); ++var4) {
               if (((AnvilChunkLoaderPending)this.chunksToRemove.get(var4)).chunkCoordinate.equals(par1ChunkCoordIntPair)) {
                  this.chunksToRemove.set(var4, new AnvilChunkLoaderPending(par1ChunkCoordIntPair, par2NBTTagCompound));
                  return;
               }
            }
         }

         this.chunksToRemove.add(new AnvilChunkLoaderPending(par1ChunkCoordIntPair, par2NBTTagCompound));
         this.pendingAnvilChunksCoordinates.add(par1ChunkCoordIntPair);
         ThreadedFileIOBase.threadedIOInstance.queueIO(this);
      }
   }

   public boolean writeNextIO() {
      AnvilChunkLoaderPending var1 = null;
      Object var2 = this.syncLockObject;
      synchronized(this.syncLockObject) {
         if (this.chunksToRemove.isEmpty()) {
            return false;
         }

         var1 = (AnvilChunkLoaderPending)this.chunksToRemove.remove(0);
         this.pendingAnvilChunksCoordinates.remove(var1.chunkCoordinate);
      }

      if (var1 != null) {
         try {
            this.writeChunkNBTTags(var1);
         } catch (Exception var5) {
            Exception var4 = var5;
            var4.printStackTrace();
         }
      }

      return true;
   }

   private void writeChunkNBTTags(AnvilChunkLoaderPending par1AnvilChunkLoaderPending) throws IOException {
      DataOutputStream var2 = null;

      try {
         var2 = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, par1AnvilChunkLoaderPending.chunkCoordinate.chunkXPos, par1AnvilChunkLoaderPending.chunkCoordinate.chunkZPos);
         CompressedStreamTools.write(par1AnvilChunkLoaderPending.nbtTags, (DataOutput)var2);
      } finally {
         if (var2 != null) {
            var2.close();
         }

         RegionFileCache.deflater_output_stream_to_close.close();
         RegionFileCache.chunk_buffer_to_close.close();
      }

   }

   public void saveExtraChunkData(World par1World, Chunk par2Chunk) {
   }

   public void chunkTick() {
   }

   public void saveExtraData() {
      while(this.writeNextIO()) {
      }

   }

   private final int calcXZAndSeedChecksumComponent(Chunk chunk) {
      long effective_world_creation_time = chunk.worldObj.isOverworld() ? chunk.worldObj.worldInfo.getWorldCreationTime() : 0L;
      return chunk.xPosition * 73 + chunk.zPosition * 211 + (Integer.MAX_VALUE - (int)chunk.worldObj.getSeed()) * 301 + (int)effective_world_creation_time * 813;
   }

   private final int calcSectionChecksum(int xz_and_seed_checksum_component, int section_y, byte[] bytes) {
      int checksum = xz_and_seed_checksum_component;
      checksum += section_y * 671;

      for(int index = bytes.length - 1; index >= 0; checksum += index * bytes[index--]) {
      }

      return checksum;
   }

   private final int calcEntityChecksum(Entity entity) {
      int checksum = 0;
      if (entity instanceof EntityItem) {
         ItemStack item_stack = ((EntityItem)entity).getEntityItem();
         if (item_stack != null) {
            checksum += item_stack.itemID * 113 * item_stack.stackSize;
         }
      }

      return checksum;
   }

   private final int calcTileEntityChecksum(TileEntity tile_entity) {
      int checksum = 0;
      if (tile_entity instanceof IInventory) {
         IInventory inventory = (IInventory)tile_entity;
         int inventory_size = inventory.getSizeInventory();

         for(int i = 0; i < inventory_size; ++i) {
            ItemStack item_stack = inventory.getStackInSlot(i);
            if (item_stack != null) {
               checksum += item_stack.itemID * 113 * item_stack.stackSize;
            }
         }
      }

      return checksum;
   }

   private void writeChunkToNBT(Chunk par1Chunk, World par2World, NBTTagCompound par3NBTTagCompound) {
      if (Minecraft.inDevMode() && (par1Chunk instanceof EmptyChunk || par1Chunk.isEmpty())) {
         Minecraft.setErrorMessage("writeChunkToNBT: trying to write empty chunk to disk");
      }

      par3NBTTagCompound.setInteger("xPos", par1Chunk.xPosition);
      par3NBTTagCompound.setInteger("zPos", par1Chunk.zPosition);
      int xz_and_seed_checksum_component = this.calcXZAndSeedChecksumComponent(par1Chunk);
      if (par1Chunk.invalidate_checksum) {
         ++xz_and_seed_checksum_component;
      }

      par3NBTTagCompound.setLong("last_total_world_time", par1Chunk.last_total_world_time);
      par3NBTTagCompound.setInteger("animals_spawned", par1Chunk.animals_spawned);
      par3NBTTagCompound.setLong("LastUpdate", par2World.getTotalWorldTime());
      par3NBTTagCompound.setIntArray("HeightMap", par1Chunk.heightMap);
      par3NBTTagCompound.setBoolean("TerrainPopulated", par1Chunk.isTerrainPopulated);
      par3NBTTagCompound.setLong("InhabitedTime", par1Chunk.inhabitedTime);
      ExtendedBlockStorage[] var4 = par1Chunk.getBlockStorageArray();
      NBTTagList var5 = new NBTTagList("Sections");
      boolean var6 = !par2World.provider.hasNoSky;
      ExtendedBlockStorage[] var7 = var4;
      int var8 = var4.length;

      NBTTagCompound var11;
      for(int var9 = 0; var9 < var8; ++var9) {
         ExtendedBlockStorage var10 = var7[var9];
         if (var10 != null) {
            var11 = new NBTTagCompound();
            var11.setByte("Y", (byte)(var10.getYLocation() >> 4 & 255));
            var11.setInteger("Blocks", this.calcSectionChecksum(xz_and_seed_checksum_component, var10.getYLocation() >> 4 & 255, var10.getBlockLSBArray()));
            var11.setByteArray("BlockData", var10.getBlockLSBArray());
            if (var10.getBlockMSBArray() != null) {
               var11.setByteArray("Add", var10.getBlockMSBArray().data);
            }

            var11.setByteArray("Data", var10.getMetadataArray().data);
            var11.setByteArray("BlockLight", var10.getBlocklightArray().data);
            if (var6) {
               var11.setByteArray("SkyLight", var10.getSkylightArray().data);
            } else {
               var11.setByteArray("SkyLight", new byte[var10.getBlocklightArray().data.length]);
            }

            var5.appendTag(var11);
         }
      }

      par3NBTTagCompound.setTag("Sections", var5);
      par3NBTTagCompound.setByteArray("Biomes", par1Chunk.getBiomeArray());
      NBTTagList var16 = new NBTTagList();
      int entities_checksum = xz_and_seed_checksum_component;
      List[] entity_lists = par1Chunk.getEntityListsForReadingOnly();

      Iterator var18;
      label220:
      for(var8 = 0; var8 < entity_lists.length; ++var8) {
         var18 = entity_lists[var8].iterator();

         while(true) {
            while(true) {
               if (!var18.hasNext()) {
                  continue label220;
               }

               Entity var21 = (Entity)var18.next();
               if (Minecraft.inDevMode() && par1Chunk.checkForEntityDuplicates(var21)) {
                  Minecraft.setErrorMessage("writeChunkToNBT: " + var21.getEntityName() + " was found in the chunk's entityLists more than once");
               }

               if (!var21.isWrittenToChunkNBT()) {
                  if (Minecraft.inDevMode()) {
                     System.out.println("writeChunkToNBT: skipping " + (var21.isDead ? "dead " : (var21.riddenByEntity != null ? "mounted " : "")) + var21.getEntityName() + " (UUID=" + var21.getUniqueID() + ") in " + par1Chunk.worldObj.provider.getDimensionName() + " because it is not supposed to be written to chunk NBT");
                  }
               } else if (var21.isDead) {
                  if (!var21.is_unwanted_duplicate) {
                     Minecraft.setErrorMessage("Why is a dead " + var21.getEntityName() + " being written to the chunk? Skipping.");
                     if (!par1Chunk.worldObj.isEntityObjectInLoadedEntityList(var21)) {
                        System.out.println("Furthermore, the entity isn't in the world's loaded entity list");
                     }
                  }
               } else {
                  Chunk chunk;
                  String msg;
                  if (var21.last_chunk_saved_to != null && (var21.last_chunk_saved_to.xPosition != par1Chunk.xPosition || var21.last_chunk_saved_to.zPosition != par1Chunk.zPosition)) {
                     chunk = par2World.getChunkFromChunkCoords(var21.last_chunk_saved_to.xPosition, var21.last_chunk_saved_to.zPosition);
                     if (chunk != null) {
                        chunk.setChunkModified();
                        if (chunk.doesEntityWithMatchingClassAndUUIDExistInEntityLists(var21)) {
                           msg = "writeChunkToNBT: " + var21.getEntityName() + " was found in another chunk at the time of saving";
                           if (Minecraft.inDevMode()) {
                              Minecraft.setErrorMessage(msg);
                           } else {
                              System.out.println(msg);
                           }
                        }
                     }
                  }

                  if (var21.last_chunk_loaded_from != null && (var21.last_chunk_loaded_from.xPosition != par1Chunk.xPosition || var21.last_chunk_loaded_from.zPosition != par1Chunk.zPosition)) {
                     chunk = par2World.getChunkFromChunkCoords(var21.last_chunk_loaded_from.xPosition, var21.last_chunk_loaded_from.zPosition);
                     if (chunk != null) {
                        chunk.setChunkModified();
                        if (chunk.doesEntityWithMatchingClassAndUUIDExistInEntityLists(var21)) {
                           msg = "writeChunkToNBT: " + var21.getEntityName() + " was found in another chunk at the time of saving (last_chunk_loaded_from)";
                           if (Minecraft.inDevMode()) {
                              Minecraft.setErrorMessage(msg);
                           } else {
                              System.out.println(msg);
                           }
                        }
                     }
                  }

                  entities_checksum += this.calcEntityChecksum(var21);
                  var11 = new NBTTagCompound();
                  try {
                     if (var21.writeToNBTOptional(var11)) {
                        var16.appendTag(var11);
                        var21.last_chunk_saved_to = par1Chunk;
                        var21.last_chunk_saved_to_entity_list_index = var8;
                     }
                  } catch (Exception e) {
                     FMLLog.log(Level.SEVERE, e,
                             "An Entity type %s has thrown an exception trying to write state. It will not persist. Report this to the mod author",
                             var21.getClass().getName());
                  }

               }
            }
         }
      }

      par3NBTTagCompound.setInteger("Entities", entities_checksum);
      par3NBTTagCompound.setTag("EntityData", var16);
      NBTTagList var17 = new NBTTagList();
      var18 = par1Chunk.chunkTileEntityMap.values().iterator();
      int tile_entities_checksum = xz_and_seed_checksum_component;

      while(var18.hasNext()) {
         TileEntity var22 = (TileEntity)var18.next();
         tile_entities_checksum += this.calcTileEntityChecksum(var22);
         var11 = new NBTTagCompound();
         try {
            var22.writeToNBT(var11);
            var17.appendTag(var11);
         } catch (Exception e) {
            FMLLog.log(Level.SEVERE, e,
                    "A TileEntity type %s has throw an exception trying to write state. It will not persist. Report this to the mod author",
                    var22.getClass().getName());
         }

      }

      par3NBTTagCompound.setInteger("TileEntities", tile_entities_checksum);
      par3NBTTagCompound.setTag("TileEntityData", var17);
      List var20 = par2World.getPendingBlockUpdates(par1Chunk, false);
      if (var20 != null) {
         long var19 = par2World.getTotalWorldTime();
         NBTTagList var12 = new NBTTagList();
         Iterator var13 = var20.iterator();

         while(var13.hasNext()) {
            NextTickListEntry var14 = (NextTickListEntry)var13.next();
            NBTTagCompound var15 = new NBTTagCompound();
            var15.setInteger("i", var14.blockID);
            var15.setInteger("x", var14.xCoord);
            var15.setInteger("y", var14.yCoord);
            var15.setInteger("z", var14.zCoord);
            var15.setInteger("t", (int)(var14.scheduledTime - var19));
            var15.setInteger("p", var14.priority);
            var12.appendTag(var15);
         }

         par3NBTTagCompound.setTag("TileTicks", var12);
      }

      if (par1Chunk.has_had_lighting_checked) {
         par3NBTTagCompound.setBoolean("has_had_lighting_checked", true);
      }

      if (par1Chunk.isGapLightingUpdated) {
         par3NBTTagCompound.setBoolean("isGapLightingUpdated", true);
      }

      byte[] update_skylight_columns = new byte[par1Chunk.updateSkylightColumns.length];

      for(int i = 0; i < update_skylight_columns.length; ++i) {
         update_skylight_columns[i] = (byte)(par1Chunk.updateSkylightColumns[i] ? -1 : 0);
      }

      par3NBTTagCompound.setByteArray("update_skylight_columns", update_skylight_columns);
      par3NBTTagCompound.setIntArray("skylight_bottom", par1Chunk.skylight_bottom);
      byte[] pending_blocklight_updates;
      int i;
      if (par2World.hasSkylight()) {
         if (par1Chunk.num_pending_skylight_updates > 0) {
            pending_blocklight_updates = new byte[par1Chunk.num_pending_skylight_updates * 2];
            System.arraycopy(par1Chunk.pending_skylight_update_coords, 0, pending_blocklight_updates, 0, pending_blocklight_updates.length);
            par3NBTTagCompound.setByteArray("pending_skylight_update_coords", pending_blocklight_updates);
         }

         pending_blocklight_updates = new byte[par1Chunk.pending_skylight_updates.length];

         for(i = 0; i < pending_blocklight_updates.length; ++i) {
            pending_blocklight_updates[i] = (byte)(par1Chunk.pending_skylight_updates[i] ? -1 : 0);
         }

         par3NBTTagCompound.setByteArray("pending_skylight_updates", pending_blocklight_updates);
      }

      if (par1Chunk.num_pending_blocklight_updates > 0) {
         pending_blocklight_updates = new byte[par1Chunk.num_pending_blocklight_updates * 2];
         System.arraycopy(par1Chunk.pending_blocklight_update_coords, 0, pending_blocklight_updates, 0, pending_blocklight_updates.length);
         par3NBTTagCompound.setByteArray("pending_blocklight_update_coords", pending_blocklight_updates);
      }

      pending_blocklight_updates = new byte[par1Chunk.pending_blocklight_updates.length];

      for(i = 0; i < pending_blocklight_updates.length; ++i) {
         pending_blocklight_updates[i] = (byte)(par1Chunk.pending_blocklight_updates[i] ? -1 : 0);
      }

      par3NBTTagCompound.setByteArray("pending_blocklight_updates", pending_blocklight_updates);
      if (par1Chunk.getHadNaturallyOccurringMycelium()) {
         par3NBTTagCompound.setBoolean("had_naturally_occurring_mycelium", true);
      }

      if (par1Chunk.pending_sand_falls != null && par1Chunk.pending_sand_falls.size() > 0) {
         int[] pending_sand_falls = new int[par1Chunk.pending_sand_falls.size() * 2];
         int index = -1;

         Map.Entry entry;
         for(Iterator iterator = par1Chunk.pending_sand_falls.entrySet().iterator(); iterator.hasNext(); pending_sand_falls[index] = (Integer)entry.getValue()) {
            entry = (Map.Entry)iterator.next();
            ++index;
            pending_sand_falls[index] = (Integer)entry.getKey();
            ++index;
         }

         par3NBTTagCompound.setIntArray("pending_sand_falls", pending_sand_falls);
      }

   }

   private Chunk readChunkFromNBT(World par1World, NBTTagCompound par2NBTTagCompound) {
      int var3 = par2NBTTagCompound.getInteger("xPos");
      int var4 = par2NBTTagCompound.getInteger("zPos");
      Chunk var5 = new Chunk(par1World, var3, var4);
      int xz_and_seed_checksum_component = this.calcXZAndSeedChecksumComponent(var5);
      var5.last_total_world_time = par2NBTTagCompound.getLong("last_total_world_time");
      var5.animals_spawned = par2NBTTagCompound.getInteger("animals_spawned");
      var5.heightMap = par2NBTTagCompound.getIntArray("HeightMap");
      var5.isTerrainPopulated = par2NBTTagCompound.getBoolean("TerrainPopulated");
      var5.inhabitedTime = par2NBTTagCompound.getLong("InhabitedTime");
      NBTTagList var6 = par2NBTTagCompound.getTagList("Sections");
      byte var7 = 16;
      ExtendedBlockStorage[] var8 = new ExtendedBlockStorage[var7];
      boolean var9 = !par1World.provider.hasNoSky;

      int tile_entities_checksum;
      for(int var10 = 0; var10 < var6.tagCount(); ++var10) {
         NBTTagCompound var11 = (NBTTagCompound)var6.tagAt(var10);
         tile_entities_checksum = var11.getByte("Y");
         ExtendedBlockStorage var13 = new ExtendedBlockStorage(tile_entities_checksum << 4, var9);
         var13.setBlockLSBArray(var11.getByteArray("BlockData"));
         if (var11.hasKey("Add")) {
            var13.setBlockMSBArray(new NibbleArray(var11.getByteArray("Add")));
         }

         var13.setBlockMetadataArray(new NibbleArray(var11.getByteArray("Data")));
         var13.setBlocklightArray(new NibbleArray(var11.getByteArray("BlockLight")));
         if (var9) {
            var13.setSkylightArray(new NibbleArray(var11.getByteArray("SkyLight")));
         }

         if (this.calcSectionChecksum(xz_and_seed_checksum_component, tile_entities_checksum, var13.getBlockLSBArray()) != var11.getInteger("Blocks")) {
            this.handleSectionChecksumFailure(var13);
         }

         var13.removeInvalidBlocks();
         var8[tile_entities_checksum] = var13;
      }

      var5.setStorageArrays(var8);
      if (par2NBTTagCompound.hasKey("Biomes")) {
         var5.setBiomeArray(par2NBTTagCompound.getByteArray("Biomes"));
      }

      NBTTagList var18 = par2NBTTagCompound.getTagList("EntityData");
      NBTTagCompound var19;
      int var23;
      if (var18 != null) {
         int entities_checksum = xz_and_seed_checksum_component;
         List entities_to_load = new ArrayList();

         for(var23 = 0; var23 < var18.tagCount(); ++var23) {
            var19 = (NBTTagCompound)var18.tagAt(var23);
            Entity var25 = EntityList.createEntityFromNBT(var19, par1World);
            if (var25 != null) {
               var25.last_chunk_loaded_from = var5;
               var25.last_chunk_loaded_from_entity_list_index = var25.getChunkCurrentlyInSectionIndex();
               entities_checksum += this.calcEntityChecksum(var25);
               Entity duplicate = World.getEntityWithSameClassAndUUIDInEntityList("readChunkFromNBT", var25, entities_to_load, true);
               if (duplicate != null) {
                  if (Minecraft.inDevMode()) {
                     Minecraft.setErrorMessage("readChunkFromNBT: A duplicate of " + var25.getEntityName() + " has already been read from the NBT. Skipping.");
                  }
               } else {
                  duplicate = World.getEntityWithSameClassAndUUIDInEntityList("readChunkFromNBT", var25, var5.worldObj.loadedEntityList, true);
                  if (duplicate != null && !var5.worldObj.unloadedEntityList.contains(duplicate)) {
                     if (Minecraft.inDevMode()) {
                        Minecraft.setErrorMessage("readChunkFromNBT: A duplicate of " + var25.getEntityName() + " already exists in the world. Skipping.");
                        System.out.println(" " + var25.getBlockPosString() + " vs " + duplicate.getBlockPosString());
                     }
                  } else {
                     entities_to_load.add(var25);
                     var5.addEntity(var25);
                     Entity var14 = var25;

                     for(NBTTagCompound var15 = var19; var15.hasKey("Riding"); var15 = var15.getCompoundTag("Riding")) {
                        Entity var16 = EntityList.createEntityFromNBT(var15.getCompoundTag("Riding"), par1World);
                        if (var16 != null) {
                           var5.addEntity(var16);
                           var14.mountEntity(var16);
                        }

                        var14 = var16;
                     }
                  }
               }
            }
         }

         if (entities_checksum != par2NBTTagCompound.getInteger("Entities")) {
            this.handleEntitiesChecksumFailure(var5);
         }
      }

      NBTTagList var21 = par2NBTTagCompound.getTagList("TileEntityData");
      if (var21 != null) {
         tile_entities_checksum = xz_and_seed_checksum_component;

         for(var23 = 0; var23 < var21.tagCount(); ++var23) {
            var19 = (NBTTagCompound)var21.tagAt(var23);
            TileEntity var27 = TileEntity.createAndLoadEntity(var19);
            if (var27 != null) {
               if (var27 instanceof IInventory) {
                  tile_entities_checksum += this.calcTileEntityChecksum(var27);
               }

               var5.addTileEntity(var27);
            }
         }

         if (tile_entities_checksum != par2NBTTagCompound.getInteger("TileEntities")) {
            this.handleTileEntitiesChecksumFailure(var5);
         }
      }

      if (par2NBTTagCompound.hasKey("TileTicks")) {
         NBTTagList var24 = par2NBTTagCompound.getTagList("TileTicks");
         if (var24 != null) {
            for(var23 = 0; var23 < var24.tagCount(); ++var23) {
               var19 = (NBTTagCompound)var24.tagAt(var23);
               par1World.scheduleBlockUpdateFromLoad(var19.getInteger("x"), var19.getInteger("y"), var19.getInteger("z"), var19.getInteger("i"), var19.getInteger("t"), var19.getInteger("p"));
            }
         }
      }

      var5.has_had_lighting_checked = par2NBTTagCompound.getBoolean("has_had_lighting_checked");
      var5.isGapLightingUpdated = par2NBTTagCompound.getBoolean("isGapLightingUpdated");
      byte[] update_skylight_columns = par2NBTTagCompound.getByteArray("update_skylight_columns");

      for(var23 = 0; var23 < update_skylight_columns.length; ++var23) {
         var5.updateSkylightColumns[var23] = update_skylight_columns[var23] != 0;
      }

      if (par2NBTTagCompound.hasKey("skylight_bottom")) {
         int[] skylight_bottom = par2NBTTagCompound.getIntArray("skylight_bottom");
         System.arraycopy(skylight_bottom, 0, var5.skylight_bottom, 0, skylight_bottom.length);
      } else {
         var5.recalculateSkylightBottoms();
      }

      int i;
      byte[] pending_blocklight_updates;
      if (par1World.hasSkylight()) {
         if (par2NBTTagCompound.hasKey("pending_skylight_update_coords")) {
            var5.pending_skylight_update_coords = par2NBTTagCompound.getByteArray("pending_skylight_update_coords");
            var5.max_num_pending_skylight_updates = var5.num_pending_skylight_updates = var5.pending_skylight_update_coords.length / 2;
         }

         pending_blocklight_updates = par2NBTTagCompound.getByteArray("pending_skylight_updates");

         for(i = 0; i < pending_blocklight_updates.length; ++i) {
            var5.pending_skylight_updates[i] = pending_blocklight_updates[i] != 0;
         }
      }

      if (par2NBTTagCompound.hasKey("pending_blocklight_update_coords")) {
         var5.pending_blocklight_update_coords = par2NBTTagCompound.getByteArray("pending_blocklight_update_coords");
         var5.max_num_pending_blocklight_updates = var5.num_pending_blocklight_updates = var5.pending_blocklight_update_coords.length / 2;
      }

      pending_blocklight_updates = par2NBTTagCompound.getByteArray("pending_blocklight_updates");

      for(i = 0; i < pending_blocklight_updates.length; ++i) {
         var5.pending_blocklight_updates[i] = pending_blocklight_updates[i] != 0;
      }

      if (par2NBTTagCompound.getBoolean("had_naturally_occurring_mycelium")) {
         var5.setHadNaturallyOccurringMycelium();
      }

      if (par2NBTTagCompound.hasKey("pending_sand_falls")) {
         int[] pending_sand_falls = par2NBTTagCompound.getIntArray("pending_sand_falls");
         int num_entries = pending_sand_falls.length / 2;
         var5.pending_sand_falls = new HashMap();
         int index = -1;

         while(true) {
            ++index;
            if (index >= pending_sand_falls.length) {
               break;
            }

            HashMap var10000 = var5.pending_sand_falls;
            Integer var10001 = pending_sand_falls[index];
            ++index;
            var10000.put(var10001, pending_sand_falls[index]);
         }
      }

      return var5;
   }

   private void handleSectionChecksumFailure(ExtendedBlockStorage ebs) {
      byte[] block_ids = ebs.getBlockLSBArray();
      NibbleArray metadata_array = ebs.getMetadataArray();
      int[] block_id_and_metadata = new int[2];

      for(int x = 0; x < 16; ++x) {
         for(int y = 0; y < 16; ++y) {
            for(int z = 0; z < 16; ++z) {
               int index = y << 8 | z << 4 | x;
               int block_id = block_ids[index];
               if (block_id < 0) {
                  block_id += 256;
               }

               if (block_id != 0 && block_id != Block.stone.blockID && block_id != Block.sand.blockID) {
                  block_id_and_metadata[0] = block_id;
                  this.checkForConversion(block_id_and_metadata, block_ids, metadata_array);
                  block_id = block_id_and_metadata[0];
                  if (block_id >= 0) {
                     block_ids[index] = (byte)block_id;
                  }

                  int metadata = block_id == 0 ? 0 : block_id_and_metadata[1];
                  if (metadata >= 0) {
                     metadata_array.set(x, y, z, metadata);
                  }
               }
            }
         }
      }

   }

   private void handleEntitiesChecksumFailure(Chunk chunk) {
      if (chunk.hasEntitiesForWritingToNBT()) {
         List[] entity_lists = chunk.getEntityListsForReadingOnly();

         for(int i = 0; i < entity_lists.length; ++i) {
            List entities = entity_lists[i];
            if (entities != null && entities.size() != 0) {
               Iterator iterator = entities.iterator();

               while(iterator.hasNext()) {
                  Entity entity = (Entity)iterator.next();
                  if (entity instanceof EntityItem) {
                     entity.setDead();
                  }
               }
            }
         }

      }
   }

   private void handleTileEntitiesChecksumFailure(Chunk chunk) {
      Iterator iterator = chunk.chunkTileEntityMap.entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator.next();
         TileEntity tile_entity = (TileEntity)entry.getValue();
         if (tile_entity instanceof IInventory) {
            IInventory inventory = (IInventory)tile_entity;
            inventory.destroyInventory();
         }
      }

   }

   private void checkForConversion(int[] block_id_and_metadata, byte[] block_ids, NibbleArray metadata_array) {
      Block block = Block.getBlock(block_id_and_metadata[0]);
      if (block == null) {
         block_id_and_metadata[0] = 0;
         block_id_and_metadata[1] = 0;
      } else {
         int block_id = invalid_section_conversion_data[block.blockID][0];
         int metadata = invalid_section_conversion_data[block.blockID][1];
         block_id_and_metadata[0] = block_id;
         block_id_and_metadata[1] = metadata;
      }
   }

   private static int[] same() {
      return new int[]{-1, -1};
   }

   private static int[] air() {
      return null;
   }

   private static int[] stone() {
      return new int[]{Block.stone.blockID, 0};
   }

   private static int[] dirt() {
      return new int[]{Block.dirt.blockID, 0};
   }

   private static int[] cobblestone() {
      return new int[]{Block.cobblestone.blockID, 0};
   }

   private static int[][] getInvalidSectionBlockConversionIdsOrMetadata() {
      int[][] array = new int[256][];
      array[Block.stone.blockID] = same();
      array[Block.grass.blockID] = same();
      array[Block.dirt.blockID] = same();
      array[Block.cobblestone.blockID] = same();
      array[Block.planks.blockID] = same();
      array[Block.sapling.blockID] = same();
      array[Block.bedrock.blockID] = same();
      array[Block.waterMoving.blockID] = same();
      array[Block.waterStill.blockID] = same();
      array[Block.lavaMoving.blockID] = same();
      array[Block.lavaStill.blockID] = same();
      array[Block.sand.blockID] = same();
      array[Block.gravel.blockID] = same();
      array[Block.oreGold.blockID] = stone();
      array[Block.oreIron.blockID] = stone();
      array[Block.oreCoal.blockID] = stone();
      array[Block.wood.blockID] = same();
      array[Block.leaves.blockID] = same();
      array[Block.sponge.blockID] = air();
      array[Block.glass.blockID] = same();
      array[Block.oreLapis.blockID] = stone();
      array[Block.blockLapis.blockID] = air();
      array[Block.dispenser.blockID] = air();
      array[Block.sandStone.blockID] = same();
      array[Block.music.blockID] = air();
      array[Block.bed.blockID] = same();
      array[Block.railPowered.blockID] = air();
      array[Block.railDetector.blockID] = air();
      array[Block.pistonStickyBase.blockID] = air();
      array[Block.web.blockID] = same();
      array[Block.tallGrass.blockID] = same();
      array[Block.deadBush.blockID] = same();
      array[Block.pistonBase.blockID] = air();
      array[Block.pistonExtension.blockID] = air();
      array[Block.cloth.blockID] = same();
      array[Block.pistonMoving.blockID] = air();
      array[Block.plantYellow.blockID] = same();
      array[Block.plantRed.blockID] = same();
      array[Block.mushroomBrown.blockID] = same();
      array[Block.mushroomRed.blockID] = same();
      array[Block.blockGold.blockID] = air();
      array[Block.blockIron.blockID] = air();
      array[Block.stoneDoubleSlab.blockID] = same();
      array[Block.stoneSingleSlab.blockID] = same();
      array[Block.brick.blockID] = same();
      array[Block.tnt.blockID] = air();
      array[Block.bookShelf.blockID] = air();
      array[Block.cobblestoneMossy.blockID] = same();
      array[Block.obsidian.blockID] = cobblestone();
      array[Block.torchWood.blockID] = air();
      array[Block.fire.blockID] = same();
      array[Block.mobSpawner.blockID] = air();
      array[Block.stairsWoodOak.blockID] = same();
      array[Block.chest.blockID] = air();
      array[Block.redstoneWire.blockID] = air();
      array[Block.oreDiamond.blockID] = stone();
      array[Block.blockDiamond.blockID] = air();
      array[Block.workbench.blockID] = air();
      array[Block.crops.blockID] = air();
      array[Block.tilledField.blockID] = dirt();
      array[Block.furnaceIdle.blockID] = air();
      array[Block.furnaceBurning.blockID] = air();
      array[Block.signPost.blockID] = same();
      array[Block.doorWood.blockID] = air();
      array[Block.ladder.blockID] = air();
      array[Block.rail.blockID] = air();
      array[Block.stairsCobblestone.blockID] = same();
      array[Block.signWall.blockID] = same();
      array[Block.lever.blockID] = air();
      array[Block.pressurePlateStone.blockID] = air();
      array[Block.doorIron.blockID] = air();
      array[Block.pressurePlatePlanks.blockID] = air();
      array[Block.oreRedstone.blockID] = stone();
      array[Block.oreRedstoneGlowing.blockID] = stone();
      array[Block.torchRedstoneIdle.blockID] = air();
      array[Block.torchRedstoneActive.blockID] = air();
      array[Block.stoneButton.blockID] = air();
      array[Block.snow.blockID] = same();
      array[Block.ice.blockID] = same();
      array[Block.blockSnow.blockID] = same();
      array[Block.cactus.blockID] = same();
      array[Block.blockClay.blockID] = same();
      array[Block.reed.blockID] = same();
      array[Block.jukebox.blockID] = air();
      array[Block.fence.blockID] = same();
      array[Block.pumpkin.blockID] = air();
      array[Block.netherrack.blockID] = same();
      array[Block.slowSand.blockID] = same();
      array[Block.glowStone.blockID] = same();
      array[Block.portal.blockID] = same();
      array[Block.pumpkinLantern.blockID] = air();
      array[Block.cake.blockID] = air();
      array[Block.redstoneRepeaterIdle.blockID] = air();
      array[Block.redstoneRepeaterActive.blockID] = air();
      array[Block.mantleOrCore.blockID] = same();
      array[Block.trapdoor.blockID] = air();
      array[Block.silverfish.blockID] = same();
      array[Block.stoneBrick.blockID] = same();
      array[Block.mushroomCapBrown.blockID] = same();
      array[Block.mushroomCapRed.blockID] = same();
      array[Block.fenceIron.blockID] = air();
      array[Block.thinGlass.blockID] = same();
      array[Block.melon.blockID] = air();
      array[Block.pumpkinStem.blockID] = air();
      array[Block.melonStem.blockID] = air();
      array[Block.vine.blockID] = same();
      array[Block.fenceGate.blockID] = same();
      array[Block.stairsBrick.blockID] = same();
      array[Block.stairsStoneBrick.blockID] = same();
      array[Block.mycelium.blockID] = same();
      array[Block.waterlily.blockID] = same();
      array[Block.netherBrick.blockID] = same();
      array[Block.netherFence.blockID] = same();
      array[Block.stairsNetherBrick.blockID] = same();
      array[Block.netherStalk.blockID] = air();
      array[Block.enchantmentTable.blockID] = air();
      array[Block.brewingStand.blockID] = air();
      array[Block.cauldron.blockID] = air();
      array[Block.endPortal.blockID] = same();
      array[Block.endPortalFrame.blockID] = same();
      array[Block.whiteStone.blockID] = same();
      array[Block.dragonEgg.blockID] = same();
      array[Block.redstoneLampIdle.blockID] = air();
      array[Block.redstoneLampActive.blockID] = air();
      array[Block.woodDoubleSlab.blockID] = same();
      array[Block.woodSingleSlab.blockID] = same();
      array[Block.cocoaPlant.blockID] = same();
      array[Block.stairsSandStone.blockID] = same();
      array[Block.oreEmerald.blockID] = stone();
      array[Block.enderChest.blockID] = air();
      array[Block.tripWireSource.blockID] = same();
      array[Block.tripWire.blockID] = same();
      array[Block.blockEmerald.blockID] = air();
      array[Block.stairsWoodSpruce.blockID] = same();
      array[Block.stairsWoodBirch.blockID] = same();
      array[Block.stairsWoodJungle.blockID] = same();
      array[Block.commandBlock.blockID] = air();
      array[Block.beacon.blockID] = air();
      array[Block.cobblestoneWall.blockID] = same();
      array[Block.flowerPot.blockID] = same();
      array[Block.carrot.blockID] = air();
      array[Block.potato.blockID] = air();
      array[Block.woodenButton.blockID] = air();
      array[Block.skull.blockID] = air();
      array[Block.anvil.blockID] = air();
      array[Block.chestTrapped.blockID] = air();
      array[Block.pressurePlateGold.blockID] = air();
      array[Block.pressurePlateIron.blockID] = air();
      array[Block.redstoneComparatorIdle.blockID] = air();
      array[Block.redstoneComparatorActive.blockID] = air();
      array[Block.daylightSensor.blockID] = air();
      array[Block.blockRedstone.blockID] = air();
      array[Block.oreNetherQuartz.blockID] = new int[]{Block.netherrack.blockID, 0};
      array[Block.hopperBlock.blockID] = air();
      array[Block.blockNetherQuartz.blockID] = air();
      array[Block.stairsNetherQuartz.blockID] = same();
      array[Block.railActivator.blockID] = air();
      array[Block.dropper.blockID] = air();
      array[Block.stainedClay.blockID] = same();
      array[Block.hay.blockID] = air();
      array[Block.carpet.blockID] = air();
      array[Block.hardenedClay.blockID] = same();
      array[Block.coalBlock.blockID] = air();
      array[Block.runestoneAdamantium.blockID] = air();
      array[Block.fenceAncientMetal.blockID] = air();
      array[Block.oreCopper.blockID] = stone();
      array[Block.oreSilver.blockID] = stone();
      array[Block.oreMithril.blockID] = stone();
      array[Block.oreAdamantium.blockID] = stone();
      array[Block.blockCopper.blockID] = air();
      array[Block.blockSilver.blockID] = air();
      array[Block.blockMithril.blockID] = air();
      array[Block.blockAdamantium.blockID] = air();
      array[Block.doorCopper.blockID] = air();
      array[Block.doorSilver.blockID] = air();
      array[Block.doorGold.blockID] = air();
      array[Block.doorMithril.blockID] = air();
      array[Block.doorAdamantium.blockID] = air();
      array[Block.fenceCopper.blockID] = air();
      array[Block.fenceSilver.blockID] = air();
      array[Block.fenceGold.blockID] = air();
      array[Block.fenceMithril.blockID] = air();
      array[Block.fenceAdamantium.blockID] = air();
      array[Block.furnaceClayIdle.blockID] = air();
      array[Block.furnaceClayBurning.blockID] = air();
      array[Block.furnaceSandstoneIdle.blockID] = air();
      array[Block.furnaceSandstoneBurning.blockID] = air();
      array[Block.furnaceObsidianIdle.blockID] = air();
      array[Block.furnaceObsidianBurning.blockID] = air();
      array[Block.furnaceNetherrackIdle.blockID] = air();
      array[Block.furnaceNetherrackBurning.blockID] = air();
      array[Block.obsidianDoubleSlab.blockID] = air();
      array[Block.obsidianSingleSlab.blockID] = air();
      array[Block.stairsObsidian.blockID] = air();
      array[Block.anvilCopper.blockID] = air();
      array[Block.anvilSilver.blockID] = air();
      array[Block.anvilGold.blockID] = air();
      array[Block.anvilMithril.blockID] = air();
      array[Block.anvilAdamantium.blockID] = air();
      array[Block.onions.blockID] = air();
      array[Block.cropsDead.blockID] = air();
      array[Block.carrotDead.blockID] = air();
      array[Block.potatoDead.blockID] = air();
      array[Block.onionsDead.blockID] = air();
      array[Block.chestCopper.blockID] = air();
      array[Block.chestSilver.blockID] = air();
      array[Block.chestGold.blockID] = air();
      array[Block.chestIron.blockID] = air();
      array[Block.chestMithril.blockID] = air();
      array[Block.chestAdamantium.blockID] = air();
      array[Block.enchantmentTableEmerald.blockID] = air();
      array[Block.spark.blockID] = same();
      array[Block.runestoneMithril.blockID] = air();
      array[Block.flowerPotMulti.blockID] = same();
      array[Block.bush.blockID] = air();
      array[Block.furnaceHardenedClayIdle.blockID] = air();
      array[Block.furnaceHardenedClayBurning.blockID] = air();
      array[Block.blockAncientMetal.blockID] = air();
      array[Block.doorAncientMetal.blockID] = air();
      array[Block.anvilAncientMetal.blockID] = air();
      array[Block.chestAncientMetal.blockID] = air();

      for(int i = 0; i < array.length; ++i) {
         if (array[i] == null) {
            array[i] = new int[]{0, 0};
         }
      }

      return array;
   }
}
