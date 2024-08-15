package net.minecraft.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.CompressionResult;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.DecompressionResult;

public final class PacketComponentBytes extends PacketComponent {
   private byte[] bytes_uncompressed;
   private int size_of_bytes_uncompressed;
   private boolean is_compressed;
   private byte[] bytes_compressed;
   private int size_of_bytes_compressed;
   private int compression_level;
   private Packet packet;

   public PacketComponentBytes(Packet packet) {
      this.packet = packet;
   }

   public PacketComponentBytes(byte[] bytes_uncompressed, Packet packet) {
      this((byte[])bytes_uncompressed, -1, packet);
   }

   public PacketComponentBytes(byte[] bytes_uncompressed, int compression_level, Packet packet) {
      this.bytes_uncompressed = bytes_uncompressed;
      this.size_of_bytes_uncompressed = bytes_uncompressed.length;
      this.compression_level = compression_level;
      this.packet = packet;
   }

   public PacketComponentBytes(String string, int compression_level, Packet packet) {
      this(string.getBytes(), compression_level, packet);
   }

   public boolean compress() {
      if (this.is_compressed) {
         Minecraft.setErrorMessage("compress: bytes block is already compressed");
         return false;
      } else if (this.compression_level == 0) {
         return false;
      } else {
         CompressionResult result = Packet.tryCompress(this.bytes_uncompressed, this.compression_level, this.packet);
         if (result.compressionOccurred()) {
            this.is_compressed = true;
            this.bytes_compressed = result.getOutput();
            this.size_of_bytes_compressed = result.getOutputSize();
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean decompress() {
      if (!this.is_compressed) {
         Minecraft.setErrorMessage("decompress: payload is not compressed");
         return false;
      } else {
         DecompressionResult result = Packet.decompress(this.bytes_compressed, this.size_of_bytes_uncompressed, this.packet);
         this.bytes_uncompressed = result.getOutput();
         return !(this.is_compressed = !result.decompressionOccurredAndMatchedExpectedSize());
      }
   }

   public String getBytesAsString() {
      return new String(this.bytes_uncompressed);
   }

   public byte[] getBytes() {
      return this.bytes_uncompressed;
   }

   public void readData(DataInput par1DataInput) throws IOException {
      this.size_of_bytes_uncompressed = par1DataInput.readShort();
      this.is_compressed = par1DataInput.readBoolean();
      if (this.is_compressed) {
         this.size_of_bytes_compressed = par1DataInput.readShort();
         this.bytes_compressed = new byte[this.size_of_bytes_compressed];
         par1DataInput.readFully(this.bytes_compressed);
         this.decompress();
      } else {
         this.bytes_uncompressed = new byte[this.size_of_bytes_uncompressed];
         par1DataInput.readFully(this.bytes_uncompressed);
      }

   }

   public void writeData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeShort(this.size_of_bytes_uncompressed);
      par1DataOutput.writeBoolean(this.is_compressed);
      if (this.is_compressed) {
         par1DataOutput.writeShort(this.size_of_bytes_compressed);
         par1DataOutput.write(this.bytes_compressed, 0, this.size_of_bytes_compressed);
      } else {
         par1DataOutput.write(this.bytes_uncompressed);
      }

   }

   public int getSize() {
      int size = 3;
      if (this.is_compressed) {
         size += 2 + this.size_of_bytes_compressed;
      } else {
         size += this.size_of_bytes_uncompressed;
      }

      return size;
   }
}
