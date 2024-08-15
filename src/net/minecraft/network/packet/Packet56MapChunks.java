package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.CompressionResult;
import net.minecraft.mite.MITEConstant;
import net.minecraft.util.Debug;
import net.minecraft.util.DecompressionResult;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class Packet56MapChunks extends Packet {
   private int[] chunkPostX;
   private int[] chunkPosZ;
   public int[] field_73590_a;
   public int[] field_73588_b;
   private byte[] compressed_chunk_data;
   private byte[][] field_73584_f;
   private int compressed_chunk_data_length;
   private boolean skyLightSent;
   private static byte[] temp_buffer = new byte[0];
   private static boolean lock_temp_buffer;
   private static final int max_num_light_updates = 8192;
   private static final int[] update_light_coords = new int[24576];
   private static final int[] coords_to_remove;
   private static final boolean[] scratch;
   private int uncompressed_chunk_data_length;

   private static int determineCandidateLightUpdateLocations(Chunk chunk) {
      int offset = -1;
      int max_offset = update_light_coords.length - 1;

      for(int local_x = 0; local_x < 16; ++local_x) {
         for(int local_z = 0; local_z < 16; ++local_z) {
            int y = chunk.getHeightValue(local_x, local_z);

            while(true) {
               --y;
               if (y <= 0) {
                  break;
               }

               int block_id = chunk.getBlockID(local_x, y, local_z);
               if (Block.lightOpacity[block_id] < 4) {
                  ++offset;
                  update_light_coords[offset] = local_x;
                  ++offset;
                  update_light_coords[offset] = y;
                  ++offset;
                  update_light_coords[offset] = local_z;
                  if (offset >= max_offset) {
                     Debug.println("determineCandidateLightUpdateLocations: Overflow for " + chunk);
                     return (offset + 1) / 3;
                  }
               }
            }
         }
      }

      return (offset + 1) / 3;
   }

   private static int performLightUpdates(Chunk chunk, boolean allow_multiple) {
      if (chunk.has_had_lighting_checked && !allow_multiple) {
         Minecraft.setErrorMessage("performLightUpdates: chunk has already been done");
      }

      int num_candidates = determineCandidateLightUpdateLocations(chunk);
      if (num_candidates == 0) {
         return 0;
      } else {
         int num_performed = 0;
         World world = chunk.worldObj;
         Arrays.fill(scratch, false);
         int offset = -1;

         int num_removals;
         int var10001;
         for(num_removals = 0; num_removals < num_candidates; ++num_removals) {
            ++offset;
            var10001 = update_light_coords[offset];
            ++offset;
            var10001 += update_light_coords[offset] * 256;
            ++offset;
            scratch[var10001 + update_light_coords[offset] * 16] = true;
         }

         num_removals = 0;
         offset = -1;
         int offset_for_removals = -1;

         int i;
         int local_x;
         int y;
         int local_z;
         int index;
         for(i = 0; i < num_candidates; ++i) {
            ++offset;
            local_x = update_light_coords[offset];
            ++offset;
            y = update_light_coords[offset];
            ++offset;
            local_z = update_light_coords[offset];
            if (y > 0 && y < 127 && y % 16 != 0) {
               index = local_x + y * 256 + local_z * 16;
               if (scratch[index - 256] && scratch[index + 256]) {
                  ++offset_for_removals;
                  coords_to_remove[offset_for_removals] = local_x;
                  ++offset_for_removals;
                  coords_to_remove[offset_for_removals] = y;
                  ++offset_for_removals;
                  coords_to_remove[offset_for_removals] = local_z;
                  ++num_removals;
               }
            }
         }

         offset = -1;

         for(i = 0; i < num_removals; ++i) {
            ++offset;
            var10001 = coords_to_remove[offset];
            ++offset;
            var10001 += coords_to_remove[offset] * 256;
            ++offset;
            scratch[var10001 + coords_to_remove[offset] * 16] = false;
         }

         offset = -1;

         for(i = 0; i < num_candidates; ++i) {
            ++offset;
            local_x = update_light_coords[offset];
            ++offset;
            y = update_light_coords[offset];
            ++offset;
            local_z = update_light_coords[offset];
            index = local_x + y * 256 + local_z * 16;
            if (scratch[index] && (local_x <= 0 || local_x >= 15 || !scratch[index - 1] || !scratch[index + 1]) && (local_z <= 0 || local_z >= 15 || !scratch[index - 16] || !scratch[index + 16])) {
               int x = chunk.getNonLocalX(local_x);
               int z = chunk.getNonLocalZ(local_z);
               world.updateLightByType(EnumSkyBlock.Sky, x, y, z, true, chunk);
               ++num_performed;
            }
         }

         return num_performed;
      }
   }

   public Packet56MapChunks() {
   }

   public Packet56MapChunks(List par1List) {
      try {
         lockTempBuffer();
         int var2 = par1List.size();
         this.chunkPostX = new int[var2];
         this.chunkPosZ = new int[var2];
         this.field_73590_a = new int[var2];
         this.field_73588_b = new int[var2];
         this.field_73584_f = new byte[var2][];
         this.skyLightSent = !par1List.isEmpty() && !((Chunk)par1List.get(0)).worldObj.provider.hasNoSky;
         int var3 = 0;

         for(int var4 = 0; var4 < var2; ++var4) {
            Chunk var5 = (Chunk)par1List.get(var4);
            if (MITEConstant.preventLightingArtifacts() && !var5.has_had_lighting_checked) {
               Debug.setErrorMessage("Packet56MapChunks: Lighting was not checked for " + var5);
            }

            Packet51MapChunkData var6 = Packet51MapChunk.getMapChunkData(var5, true, 65535);
            if (temp_buffer.length < var3 + var6.uncompressed_data.length) {
               byte[] var7 = new byte[var3 + var6.uncompressed_data.length];
               System.arraycopy(temp_buffer, 0, var7, 0, temp_buffer.length);
               temp_buffer = var7;
            }

            System.arraycopy(var6.uncompressed_data, 0, temp_buffer, var3, var6.uncompressed_data.length);
            var3 += var6.uncompressed_data.length;
            this.chunkPostX[var4] = var5.xPosition;
            this.chunkPosZ[var4] = var5.zPosition;
            this.field_73590_a[var4] = var6.chunkExistFlag;
            this.field_73588_b[var4] = var6.chunkHasAddSectionFlag;
            this.field_73584_f[var4] = var6.uncompressed_data;
         }

         this.uncompressed_chunk_data_length = var3;
      } finally {
         unlockTempBuffer();
      }
   }

   public void compressPayload() {
      CompressionResult result = tryCompress(temp_buffer, 0, this.uncompressed_chunk_data_length, -1, this);
      this.compressed_chunk_data = result.getOutput();
      this.compressed_chunk_data_length = result.getOutputSize();
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      try {
         lockTempBuffer();
         this.uncompressed_chunk_data_length = par1DataInput.readInt();
         short var2 = par1DataInput.readShort();
         this.compressed_chunk_data_length = par1DataInput.readInt();
         this.skyLightSent = par1DataInput.readBoolean();
         this.chunkPostX = new int[var2];
         this.chunkPosZ = new int[var2];
         this.field_73590_a = new int[var2];
         this.field_73588_b = new int[var2];
         this.field_73584_f = new byte[var2][];
         if (temp_buffer.length < this.compressed_chunk_data_length) {
            temp_buffer = new byte[this.compressed_chunk_data_length];
         }

         par1DataInput.readFully(temp_buffer, 0, this.compressed_chunk_data_length);
         DecompressionResult result = decompress(temp_buffer, 0, this.compressed_chunk_data_length, this.uncompressed_chunk_data_length, this);
         byte[] var3 = result.getOutput();
         int var5 = 0;

         for(int var6 = 0; var6 < var2; ++var6) {
            this.chunkPostX[var6] = par1DataInput.readInt();
            this.chunkPosZ[var6] = par1DataInput.readInt();
            this.field_73590_a[var6] = par1DataInput.readShort();
            this.field_73588_b[var6] = par1DataInput.readShort();
            int var7 = 0;
            int var8 = 0;

            int var9;
            for(var9 = 0; var9 < 16; ++var9) {
               var7 += this.field_73590_a[var6] >> var9 & 1;
               var8 += this.field_73588_b[var6] >> var9 & 1;
            }

            var9 = 8192 * var7 + 256;
            var9 += 2048 * var8;
            if (this.skyLightSent) {
               var9 += 2048 * var7;
               var9 += 256;
            }

            this.field_73584_f[var6] = new byte[var9];
            System.arraycopy(var3, var5, this.field_73584_f[var6], 0, var9);
            var5 += var9;
         }

         if (var5 != this.uncompressed_chunk_data_length) {
            Debug.setErrorMessage("readPacketData: Bytes read discrepency " + var5 + " vs " + this.uncompressed_chunk_data_length);
         }
      } finally {
         unlockTempBuffer();
      }

   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.uncompressed_chunk_data_length);
      par1DataOutput.writeShort(this.chunkPostX.length);
      par1DataOutput.writeInt(this.compressed_chunk_data_length);
      par1DataOutput.writeBoolean(this.skyLightSent);
      par1DataOutput.write(this.compressed_chunk_data, 0, this.compressed_chunk_data_length);

      for(int var2 = 0; var2 < this.chunkPostX.length; ++var2) {
         par1DataOutput.writeInt(this.chunkPostX[var2]);
         par1DataOutput.writeInt(this.chunkPosZ[var2]);
         par1DataOutput.writeShort((short)(this.field_73590_a[var2] & '\uffff'));
         par1DataOutput.writeShort((short)(this.field_73588_b[var2] & '\uffff'));
      }

   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleMapChunks(this);
   }

   public int getPacketSize() {
      return 10 + this.compressed_chunk_data_length + 12 * this.getNumberOfChunkInPacket();
   }

   public int getChunkPosX(int par1) {
      return this.chunkPostX[par1];
   }

   public int getChunkPosZ(int par1) {
      return this.chunkPosZ[par1];
   }

   public int getNumberOfChunkInPacket() {
      return this.chunkPostX.length;
   }

   public byte[] getChunkCompressedData(int par1) {
      return this.field_73584_f[par1];
   }

   private static void lockTempBuffer() {
      if (lock_temp_buffer) {
         Minecraft.setErrorMessage("lockTempBuffer: Already locked!");
      } else {
         lock_temp_buffer = true;
      }

   }

   private static void unlockTempBuffer() {
      if (lock_temp_buffer) {
         lock_temp_buffer = false;
      } else {
         Minecraft.setErrorMessage("unlockTempBuffer: Already unlocked!");
      }

   }

   public static void checkLighting(Chunk chunk) {
      chunk.performPendingSandFallsIfPossible();
      chunk.performPendingSkylightUpdatesIfPossible();
      chunk.performPendingBlocklightUpdatesIfPossible();
      if (!chunk.has_had_lighting_checked) {
         chunk.loadNeighboringChunks(MITEConstant.considerNeighboringChunksInLightingArtifactPrevention() ? 2 : 1);
         checkLighting8(chunk);
         chunk.setChunkModified();
         chunk.has_had_lighting_checked = true;
      }
   }

   private static void checkNeighborLighting(Chunk chunk, int dx, int dz) {
      Chunk neighbor = chunk.getNeighboringChunk(dx, dz);
      neighbor.performPendingSkylightUpdatesIfPossible();
      neighbor.performPendingBlocklightUpdatesIfPossible();
      if (neighbor.hasSkylight() && !neighbor.has_had_lighting_checked) {
         neighbor.propagateSkylightOcclusion();
         neighbor.updateSkylight(true);
      }

   }

   private static void checkLighting8(Chunk chunk) {
      if (MITEConstant.considerNeighboringChunksInLightingArtifactPrevention()) {
         checkNeighborLighting(chunk, -1, -1);
         checkNeighborLighting(chunk, 1, -1);
         checkNeighborLighting(chunk, 1, 1);
         checkNeighborLighting(chunk, -1, 1);
         checkNeighborLighting(chunk, 0, -1);
         checkNeighborLighting(chunk, 1, 0);
         checkNeighborLighting(chunk, 0, 1);
         checkNeighborLighting(chunk, -1, 0);
      }

      chunk.performPendingSkylightUpdatesIfPossible();
      chunk.performPendingBlocklightUpdatesIfPossible();
      if (chunk.hasSkylight()) {
         chunk.propagateSkylightOcclusion();
         chunk.updateSkylight(true);
      }

   }

   static {
      coords_to_remove = new int[update_light_coords.length];
      scratch = new boolean['退'];
   }
}
