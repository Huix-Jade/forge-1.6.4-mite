package net.minecraft.item.crafting;

public final class CompressionResult {
   private final byte[] output;
   private final int size;
   private final boolean compression_occured;

   public CompressionResult(byte[] output, int size, boolean compression_occured) {
      this.output = output;
      this.size = size;
      this.compression_occured = compression_occured;
   }

   public byte[] getOutput() {
      return this.output;
   }

   public int getOutputSize() {
      return this.size;
   }

   public boolean compressionOccurred() {
      return this.compression_occured;
   }
}
