package net.minecraft.util;

public final class DecompressionResult {
   private final byte[] output;
   private final int size;
   private final boolean decompression_occured_and_matched_expected_size;

   public DecompressionResult(byte[] output, int size, boolean decompression_occured_and_matched_expected_size) {
      this.output = output;
      this.size = size;
      this.decompression_occured_and_matched_expected_size = decompression_occured_and_matched_expected_size;
   }

   public byte[] getOutput() {
      return this.output;
   }

   public int getOutputSize() {
      return this.size;
   }

   public boolean decompressionOccurredAndMatchedExpectedSize() {
      return this.decompression_occured_and_matched_expected_size;
   }
}
