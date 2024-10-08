package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.world.EnumGameType;

public class Packet70GameEvent extends Packet {
   public static final String[] clientMessage = new String[]{"tile.bed.notValid", null, null, Minecraft.inDevMode() ? "gameMode.changed" : "gameMode.cannotBeChanged"};
   public int eventType;
   public int gameMode;

   public Packet70GameEvent() {
   }

   public Packet70GameEvent(int par1, int par2) {
      if (par1 == 3 && !Minecraft.inDevMode()) {
         par2 = EnumGameType.SURVIVAL.id;
      }

      this.eventType = par1;
      this.gameMode = par2;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.eventType = par1DataInput.readByte();
      this.gameMode = par1DataInput.readByte();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeByte(this.eventType);
      par1DataOutput.writeByte(this.gameMode);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleGameEvent(this);
   }

   public int getPacketSize() {
      return 2;
   }
}
