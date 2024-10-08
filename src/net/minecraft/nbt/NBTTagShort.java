package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class NBTTagShort extends NBTBase {
   public short data;

   public NBTTagShort(String par1Str) {
      super(2, par1Str);
   }

   public NBTTagShort(String par1Str, short par2) {
      super(2, par1Str);
      this.data = par2;
   }

   void write(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeShort(this.data);
   }

   void load(DataInput par1DataInput, int par2) throws IOException {
      this.data = par1DataInput.readShort();
   }

   public String toString() {
      return "" + this.data;
   }

   public NBTBase copy() {
      return new NBTTagShort(this.getName(), this.data);
   }

   public boolean equals(Object par1Obj) {
      if (super.equals(par1Obj)) {
         NBTTagShort var2 = (NBTTagShort)par1Obj;
         return this.data == var2.data;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return super.hashCode() ^ this.data;
   }
}
