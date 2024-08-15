package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class NBTTagEnd extends NBTBase {
   public NBTTagEnd() {
      super(0, (String)null);
   }

   void load(DataInput par1DataInput, int par2) throws IOException {
   }

   void write(DataOutput par1DataOutput) throws IOException {
   }

   public String toString() {
      return "END";
   }

   public NBTBase copy() {
      return new NBTTagEnd();
   }
}
