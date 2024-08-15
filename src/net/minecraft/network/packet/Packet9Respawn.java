package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldType;

public class Packet9Respawn extends Packet {
   public int respawnDimension;
   public int difficulty;
   public int worldHeight;
   public EnumGameType gameType;
   public WorldType terrainType;
   public long world_creation_time;
   public long total_world_time;

   public Packet9Respawn() {
   }

   public Packet9Respawn(int par1, byte par2, WorldType par3WorldType, int par4, EnumGameType par5EnumGameType, long world_creation_time, long total_world_time) {
      this.respawnDimension = par1;
      this.difficulty = par2;
      this.worldHeight = par4;
      this.gameType = par5EnumGameType;
      this.terrainType = par3WorldType;
      this.world_creation_time = world_creation_time;
      this.total_world_time = total_world_time;
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleRespawn(this);
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.respawnDimension = par1DataInput.readInt();
      this.difficulty = par1DataInput.readByte();
      this.gameType = EnumGameType.getByID(par1DataInput.readByte());
      this.worldHeight = par1DataInput.readShort();
      String var2 = readString(par1DataInput, 16);
      this.terrainType = WorldType.parseWorldType(var2);
      if (this.terrainType == null) {
         this.terrainType = WorldType.DEFAULT;
      }

      this.world_creation_time = par1DataInput.readLong();
      this.total_world_time = par1DataInput.readLong();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.respawnDimension);
      par1DataOutput.writeByte(this.difficulty);
      par1DataOutput.writeByte(this.gameType.getID());
      par1DataOutput.writeShort(this.worldHeight);
      writeString(this.terrainType.getWorldTypeName(), par1DataOutput);
      par1DataOutput.writeLong(this.world_creation_time);
      par1DataOutput.writeLong(this.total_world_time);
   }

   public int getPacketSize() {
      return 8 + (this.terrainType == null ? 0 : this.terrainType.getWorldTypeName().length()) + 16;
   }
}
