package net.minecraft.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class PacketComponent {
   public abstract void writeData(DataOutput var1) throws IOException;

   public abstract void readData(DataInput var1) throws IOException;

   public abstract int getSize();
}
