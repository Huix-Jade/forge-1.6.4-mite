package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.CompressionResult;
import net.minecraft.util.DecompressionResult;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class Packet51MapChunk extends Packet {
   public int xCh;
   public int zCh;
   public int yChMin;
   public int yChMax;
   private byte[] compressed_chunk_data;
   private byte[] uncompressed_chunk_data;
   public boolean includeInitialize;
   private int compressed_chunk_data_length;
   private static byte[] temp = new byte[196864];
   private static boolean lock_temp;

   public Packet51MapChunk() {
      this.isChunkDataPacket = true;
   }

   public Packet51MapChunk(Chunk par1Chunk, boolean par2, int par3) {
      this.isChunkDataPacket = true;
      this.xCh = par1Chunk.xPosition;
      this.zCh = par1Chunk.zPosition;
      this.includeInitialize = par2;
      Packet51MapChunkData var4 = getMapChunkData(par1Chunk, par2, par3);
      this.yChMax = var4.chunkHasAddSectionFlag;
      this.yChMin = var4.chunkExistFlag;
      this.uncompressed_chunk_data = var4.uncompressed_data;
   }

   public void compressPayload() {
      CompressionResult result = tryCompress(this.uncompressed_chunk_data, -1, this);
      this.compressed_chunk_data = result.getOutput();
      this.compressed_chunk_data_length = result.getOutputSize();
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      try {
         lockTemp();
         this.xCh = par1DataInput.readInt();
         this.zCh = par1DataInput.readInt();
         this.includeInitialize = par1DataInput.readBoolean();
         this.yChMin = par1DataInput.readShort();
         this.yChMax = par1DataInput.readShort();
         int uncompressed_chunk_data_length = par1DataInput.readInt();
         this.compressed_chunk_data_length = par1DataInput.readInt();
         if (temp.length < this.compressed_chunk_data_length) {
            temp = new byte[this.compressed_chunk_data_length];
         }

         par1DataInput.readFully(temp, 0, this.compressed_chunk_data_length);
         int var2 = 0;

         int var3;
         for(var3 = 0; var3 < 16; ++var3) {
            var2 += this.yChMin >> var3 & 1;
         }

         var3 = 12288 * var2;
         if (this.includeInitialize) {
            var3 += 256;
         }

         DecompressionResult result = decompress(temp, 0, this.compressed_chunk_data_length, uncompressed_chunk_data_length, this);
         this.uncompressed_chunk_data = result.getOutput();
      } finally {
         unlockTemp();
      }

   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.xCh);
      par1DataOutput.writeInt(this.zCh);
      par1DataOutput.writeBoolean(this.includeInitialize);
      par1DataOutput.writeShort((short)(this.yChMin & '\uffff'));
      par1DataOutput.writeShort((short)(this.yChMax & '\uffff'));
      par1DataOutput.writeInt(this.uncompressed_chunk_data.length);
      par1DataOutput.writeInt(this.compressed_chunk_data_length);
      par1DataOutput.write(this.compressed_chunk_data, 0, this.compressed_chunk_data_length);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleMapChunk(this);
   }

   public int getPacketSize() {
      return 21 + this.compressed_chunk_data_length;
   }

   public byte[] getUncompressedChunkData() {
      return this.uncompressed_chunk_data;
   }

   public static Packet51MapChunkData getMapChunkData(Chunk par0Chunk, boolean par1, int par2) {
      Packet51MapChunkData var17;
      try {
         lockTemp();
         int var3 = 0;
         ExtendedBlockStorage[] var4 = par0Chunk.getBlockStorageArray();
         int var5 = 0;
         Packet51MapChunkData var6 = new Packet51MapChunkData();
         byte[] var7 = temp;
         if (par1) {
            par0Chunk.sendUpdates = true;
         }

         int var8;
         for(var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (par2 & 1 << var8) != 0) {
               var6.chunkExistFlag |= 1 << var8;
               if (var4[var8].getBlockMSBArray() != null) {
                  var6.chunkHasAddSectionFlag |= 1 << var8;
                  ++var5;
               }
            }
         }

         for(var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (par2 & 1 << var8) != 0) {
               byte[] var9 = var4[var8].getBlockLSBArray();
               System.arraycopy(var9, 0, var7, var3, var9.length);
               var3 += var9.length;
            }
         }

         NibbleArray var10;
         for(var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (par2 & 1 << var8) != 0) {
               var10 = var4[var8].getMetadataArray();
               System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
               var3 += var10.data.length;
            }
         }

         for(var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (par2 & 1 << var8) != 0) {
               var10 = var4[var8].getBlocklightArray();
               System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
               var3 += var10.data.length;
            }
         }

         if (!par0Chunk.worldObj.provider.hasNoSky) {
            for(var8 = 0; var8 < var4.length; ++var8) {
               if (var4[var8] != null && (par2 & 1 << var8) != 0) {
                  var10 = var4[var8].getSkylightArray();
                  System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
                  var3 += var10.data.length;
               }
            }
         }

         if (var5 > 0) {
            for(var8 = 0; var8 < var4.length; ++var8) {
               if (var4[var8] != null && var4[var8].getBlockMSBArray() != null && (par2 & 1 << var8) != 0) {
                  var10 = var4[var8].getBlockMSBArray();
                  System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
                  var3 += var10.data.length;
               }
            }
         }

         if (par1) {
            byte[] var11 = par0Chunk.getBiomeArray();
            System.arraycopy(var11, 0, var7, var3, var11.length);
            var3 += var11.length;
         }

         if (par0Chunk.worldObj.hasSkylight()) {
            int[] skylight_bottom = par0Chunk.skylight_bottom;
            int i = 0;

            while(true) {
               if (i >= skylight_bottom.length) {
                  var3 += par0Chunk.skylight_bottom.length;
                  break;
               }

               var7[var3 + i] = (byte)skylight_bottom[i];
               ++i;
            }
         }

         if (var3 > var7.length) {
            Minecraft.setErrorMessage("getMapChunkData: var3>var7.length");
         }

         var6.uncompressed_data = new byte[var3];
         System.arraycopy(var7, 0, var6.uncompressed_data, 0, var3);
         var17 = var6;
      } finally {
         unlockTemp();
      }

      return var17;
   }

   private static void lockTemp() {
      if (lock_temp) {
         Minecraft.setErrorMessage("lockTemp: Already locked!");
      } else {
         lock_temp = true;
      }

   }

   private static void unlockTemp() {
      if (lock_temp) {
         lock_temp = false;
      } else {
         Minecraft.setErrorMessage("unlockTemp: Already unlocked!");
      }

   }
}
