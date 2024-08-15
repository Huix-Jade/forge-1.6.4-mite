package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.EnumEntityState;

public class Packet38EntityStatus extends Packet {
   public int entityId;
   public EnumEntityState entity_state;

   public Packet38EntityStatus() {
   }

   public Packet38EntityStatus(int par1, EnumEntityState par2) {
      this.entityId = par1;
      this.entity_state = par2;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entityId = par1DataInput.readInt();
      this.entity_state = EnumEntityState.values()[par1DataInput.readByte()];
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entityId);
      par1DataOutput.writeByte(this.entity_state.ordinal());
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleEntityStatus(this);
   }

   public int getPacketSize() {
      return 5;
   }
}
