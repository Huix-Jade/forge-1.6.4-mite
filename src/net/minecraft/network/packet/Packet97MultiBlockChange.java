package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.PacketComponentBytes;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class Packet97MultiBlockChange extends Packet {
   public int chunk_x;
   public int chunk_z;
   public int num_blocks;
   private PacketComponentBytes bytes;

   public Packet97MultiBlockChange() {
      this.isChunkDataPacket = true;
   }

   public Packet97MultiBlockChange(int chunk_x, int chunk_z, short[] local_coords, int num_blocks, World world) {
      this.isChunkDataPacket = true;
      this.chunk_x = chunk_x;
      this.chunk_z = chunk_z;
      this.num_blocks = num_blocks;
      Chunk chunk = world.getChunkFromChunkCoords(chunk_x, chunk_z);
      byte[] bytes = new byte[num_blocks * 5];

      for(int i = 0; i < num_blocks; ++i) {
         int offset = i * 5;
         int x = local_coords[i] >> 12 & 15;
         int y = local_coords[i] & 255;
         int z = local_coords[i] >> 8 & 15;
         int block_id = chunk.getBlockID(x, y, z);
         int metadata = chunk.getBlockMetadata(x, y, z);
         bytes[offset] = (byte)x;
         bytes[offset + 1] = (byte)y;
         bytes[offset + 2] = (byte)z;
         bytes[offset + 3] = (byte)block_id;
         bytes[offset + 4] = (byte)metadata;
      }

      this.bytes = new PacketComponentBytes(bytes, this);
   }

   public void compressPayload() {
      this.bytes.compress();
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.chunk_x = par1DataInput.readInt();
      this.chunk_z = par1DataInput.readInt();
      this.num_blocks = par1DataInput.readShort() & '\uffff';
      this.bytes = new PacketComponentBytes(this);
      this.bytes.readData(par1DataInput);
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.chunk_x);
      par1DataOutput.writeInt(this.chunk_z);
      par1DataOutput.writeShort((short)this.num_blocks);
      this.bytes.writeData(par1DataOutput);
   }

   public void processPacket(NetHandler net_handler) {
      net_handler.handleMultiBlockChange(this);
   }

   public int getPacketSize() {
      return 10 + this.bytes.getSize();
   }

   public byte[] getBytes() {
      return this.bytes.getBytes();
   }
}
