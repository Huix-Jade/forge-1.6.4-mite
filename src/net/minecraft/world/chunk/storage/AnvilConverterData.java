package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagList;

public class AnvilConverterData {
   public long lastUpdated;
   public boolean terrainPopulated;
   public byte[] heightmap;
   public NibbleArrayReader blockLight;
   public NibbleArrayReader skyLight;
   public NibbleArrayReader data;
   public byte[] blocks;
   public NBTTagList entities;
   public NBTTagList tileEntities;
   public NBTTagList tileTicks;
   public final int x;
   public final int z;

   public AnvilConverterData(int var1, int var2) {
      this.x = var1;
      this.z = var2;
   }
}
