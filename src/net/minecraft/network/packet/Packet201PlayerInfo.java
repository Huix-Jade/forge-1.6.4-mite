package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet201PlayerInfo extends Packet {
   public String playerName;
   public boolean isConnected;
   public int ping;
   public int level;

   public Packet201PlayerInfo() {
   }

   public Packet201PlayerInfo(String par1Str, boolean par2, int par3, int level) {
      this.playerName = par1Str;
      this.isConnected = par2;
      this.ping = par3;
      this.level = level;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.playerName = readString(par1DataInput, 16);
      this.isConnected = par1DataInput.readByte() != 0;
      this.ping = par1DataInput.readShort();
      this.level = par1DataInput.readShort();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      writeString(this.playerName, par1DataOutput);
      par1DataOutput.writeByte(this.isConnected ? 1 : 0);
      par1DataOutput.writeShort(this.ping);
      par1DataOutput.writeShort(this.level);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handlePlayerInfo(this);
   }

   public int getPacketSize() {
      return this.playerName.length() + 2 + 1 + 2 + 1 + 1;
   }
}
