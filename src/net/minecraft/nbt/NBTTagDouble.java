package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class NBTTagDouble extends NBTBase {
   public double data;

   public NBTTagDouble(String par1Str) {
      super(6, par1Str);
   }

   public NBTTagDouble(String par1Str, double par2) {
      super(6, par1Str);
      this.data = par2;
   }

   void write(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeDouble(this.data);
   }

   void load(DataInput par1DataInput, int par2) throws IOException {
      this.data = par1DataInput.readDouble();
   }

   public String toString() {
      return "" + this.data;
   }

   public NBTBase copy() {
      return new NBTTagDouble(this.getName(), this.data);
   }

   public boolean equals(Object par1Obj) {
      if (super.equals(par1Obj)) {
         NBTTagDouble var2 = (NBTTagDouble)par1Obj;
         return this.data == var2.data;
      } else {
         return false;
      }
   }

   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.data);
      return super.hashCode() ^ (int)(var1 ^ var1 >>> 32);
   }
}
