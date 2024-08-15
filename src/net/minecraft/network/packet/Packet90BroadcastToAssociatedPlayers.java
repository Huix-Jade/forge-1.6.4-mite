package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.logging.ILogAgent;

public class Packet90BroadcastToAssociatedPlayers extends Packet {
   int enveloped_packet_id;
   public Packet packet;
   public boolean include_sender;

   public Packet90BroadcastToAssociatedPlayers() {
   }

   public Packet90BroadcastToAssociatedPlayers(Packet packet, boolean broadcast_to_sending_player) {
      this.enveloped_packet_id = packet.getPacketId();
      this.packet = packet;
      this.include_sender = broadcast_to_sending_player;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.enveloped_packet_id = par1DataInput.readUnsignedByte();
      this.packet = Packet.getNewPacket((ILogAgent)null, this.enveloped_packet_id);
      this.packet.readPacketData(par1DataInput);
      this.include_sender = par1DataInput.readBoolean();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeByte(this.enveloped_packet_id);
      this.packet.writePacketData(par1DataOutput);
      par1DataOutput.writeBoolean(this.include_sender);
   }

   public void processPacket(NetHandler net_handler) {
      net_handler.handleBroadcastToAssociatedPlayers(this);
   }

   public int getPacketSize() {
      return 1 + this.packet.getPacketSize() + 1;
   }
}
