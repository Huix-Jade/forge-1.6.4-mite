package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public final class NBTTagByteArray extends NBTBase {
   public byte[] byteArray;

   public NBTTagByteArray(String par1Str) {
      super(7, par1Str);
   }

   public NBTTagByteArray(String par1Str, byte[] par2ArrayOfByte) {
      super(7, par1Str);
      this.byteArray = par2ArrayOfByte;
   }

   void write(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.byteArray.length);
      par1DataOutput.write(this.byteArray);
   }

   void load(DataInput par1DataInput, int par2) throws IOException {
      int var3 = par1DataInput.readInt();
      this.byteArray = new byte[var3];
      par1DataInput.readFully(this.byteArray);
   }

   public String toString() {
      return "[" + this.byteArray.length + " bytes]";
   }

   public NBTBase copy() {
      byte[] var1 = new byte[this.byteArray.length];
      System.arraycopy(this.byteArray, 0, var1, 0, this.byteArray.length);
      return new NBTTagByteArray(this.getName(), var1);
   }

   public boolean equals(Object par1Obj) {
      return super.equals(par1Obj) ? Arrays.equals(this.byteArray, ((NBTTagByteArray)par1Obj).byteArray) : false;
   }

   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.byteArray);
   }
}
