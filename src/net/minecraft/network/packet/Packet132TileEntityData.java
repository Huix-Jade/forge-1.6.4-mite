package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public class Packet132TileEntityData extends Packet {
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public int actionType;
   public NBTTagCompound data;

   public Packet132TileEntityData() {
      this.isChunkDataPacket = true;
   }

   public Packet132TileEntityData(int var1, int var2, int var3, int var4, NBTTagCompound var5) {
      this.isChunkDataPacket = true;
      this.xPosition = var1;
      this.yPosition = var2;
      this.zPosition = var3;
      this.actionType = var4;
      this.data = var5;
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.xPosition = var1.readInt();
      this.yPosition = var1.readShort();
      this.zPosition = var1.readInt();
      this.actionType = var1.readByte();
      this.data = readNBTTagCompound(var1);
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.writeInt(this.xPosition);
      var1.writeShort(this.yPosition);
      var1.writeInt(this.zPosition);
      var1.writeByte((byte)this.actionType);
      writeNBTTagCompound(this.data, var1);
   }

   public void processPacket(NetHandler var1) {
      var1.handleTileEntityData(this);
   }

   public int getPacketSize() {
      return 25;
   }
}
