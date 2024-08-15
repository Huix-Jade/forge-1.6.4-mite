package net.minecraft.world.chunk;

public class NibbleArray {
   public final byte[] data;

   public NibbleArray(int par1) {
      this.data = new byte[par1 >> 1];
   }

   public NibbleArray(byte[] par1ArrayOfByte) {
      this.data = par1ArrayOfByte;
   }

   public int get(int par1, int par2, int par3) {
      int var4 = par2 << 8 | par3 << 4 | par1;
      return (var4 & 1) == 0 ? this.data[var4 >> 1] & 15 : this.data[var4 >> 1] >> 4 & 15;
   }

   public int get(int xz_index, int y) {
      int var4 = y << 8 | xz_index;
      return (var4 & 1) == 0 ? this.data[var4 >> 1] & 15 : this.data[var4 >> 1] >> 4 & 15;
   }

   public void set(int par1, int par2, int par3, int par4) {
      int var5 = par2 << 8 | par3 << 4 | par1;
      int var6 = var5 >> 1;
      if ((var5 & 1) == 0) {
         this.data[var6] = (byte)(this.data[var6] & 240 | par4);
      } else {
         this.data[var6] = (byte)(this.data[var6] & 15 | par4 << 4);
      }

   }

   public void set(int xz_index, int y, int value) {
      int var5 = y << 8 | xz_index;
      int var6 = var5 >> 1;
      if ((var5 & 1) == 0) {
         this.data[var6] = (byte)(this.data[var6] & 240 | value);
      } else {
         this.data[var6] = (byte)(this.data[var6] & 15 | value << 4);
      }

   }
}
