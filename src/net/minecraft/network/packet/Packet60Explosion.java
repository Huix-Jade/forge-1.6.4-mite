package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

public class Packet60Explosion extends Packet {
   public double explosionX;
   public double explosionY;
   public double explosionZ;
   public float explosion_size_vs_blocks;
   public float explosion_size_vs_living_entities;
   public List chunkPositionRecords;
   private float playerVelocityX;
   private float playerVelocityY;
   private float playerVelocityZ;

   public Packet60Explosion() {
   }

   public Packet60Explosion(double posX, double posY, double posZ, float explosion_size_vs_blocks, float explosion_size_vs_living_entities, List par8List, Vec3 par9Vec3) {
      this.explosionX = posX;
      this.explosionY = posY;
      this.explosionZ = posZ;
      this.explosion_size_vs_blocks = explosion_size_vs_blocks;
      this.explosion_size_vs_living_entities = explosion_size_vs_living_entities;
      this.chunkPositionRecords = new ArrayList(par8List);
      if (par9Vec3 != null) {
         this.playerVelocityX = (float)par9Vec3.xCoord;
         this.playerVelocityY = (float)par9Vec3.yCoord;
         this.playerVelocityZ = (float)par9Vec3.zCoord;
      }

   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.explosionX = par1DataInput.readDouble();
      this.explosionY = par1DataInput.readDouble();
      this.explosionZ = par1DataInput.readDouble();
      this.explosion_size_vs_blocks = par1DataInput.readFloat();
      this.explosion_size_vs_living_entities = par1DataInput.readFloat();
      int var2 = par1DataInput.readInt();
      this.chunkPositionRecords = new ArrayList(var2);
      int var3 = (int)this.explosionX;
      int var4 = (int)this.explosionY;
      int var5 = (int)this.explosionZ;

      for(int var6 = 0; var6 < var2; ++var6) {
         int var7 = par1DataInput.readByte() + var3;
         int var8 = par1DataInput.readByte() + var4;
         int var9 = par1DataInput.readByte() + var5;
         this.chunkPositionRecords.add(new ChunkPosition(var7, var8, var9));
      }

      this.playerVelocityX = par1DataInput.readFloat();
      this.playerVelocityY = par1DataInput.readFloat();
      this.playerVelocityZ = par1DataInput.readFloat();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeDouble(this.explosionX);
      par1DataOutput.writeDouble(this.explosionY);
      par1DataOutput.writeDouble(this.explosionZ);
      par1DataOutput.writeFloat(this.explosion_size_vs_blocks);
      par1DataOutput.writeFloat(this.explosion_size_vs_living_entities);
      par1DataOutput.writeInt(this.chunkPositionRecords.size());
      int var2 = (int)this.explosionX;
      int var3 = (int)this.explosionY;
      int var4 = (int)this.explosionZ;
      Iterator var5 = this.chunkPositionRecords.iterator();

      while(var5.hasNext()) {
         ChunkPosition var6 = (ChunkPosition)var5.next();
         int var7 = var6.x - var2;
         int var8 = var6.y - var3;
         int var9 = var6.z - var4;
         par1DataOutput.writeByte(var7);
         par1DataOutput.writeByte(var8);
         par1DataOutput.writeByte(var9);
      }

      par1DataOutput.writeFloat(this.playerVelocityX);
      par1DataOutput.writeFloat(this.playerVelocityY);
      par1DataOutput.writeFloat(this.playerVelocityZ);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleExplosion(this);
   }

   public int getPacketSize() {
      return 36 + this.chunkPositionRecords.size() * 3 + 3;
   }

   public float getPlayerVelocityX() {
      return this.playerVelocityX;
   }

   public float getPlayerVelocityY() {
      return this.playerVelocityY;
   }

   public float getPlayerVelocityZ() {
      return this.playerVelocityZ;
   }
}
