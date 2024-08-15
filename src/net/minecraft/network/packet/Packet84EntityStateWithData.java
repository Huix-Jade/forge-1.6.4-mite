package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.EnumEntityState;

public class Packet84EntityStateWithData extends Packet38EntityStatus {
   public int data;

   public Packet84EntityStateWithData() {
   }

   public Packet84EntityStateWithData(int entity_id, EnumEntityState entity_state, int data) {
      super(entity_id, entity_state);
      this.data = data;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      super.readPacketData(par1DataInput);
      this.data = par1DataInput.readInt();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      super.writePacketData(par1DataOutput);
      par1DataOutput.writeInt(this.data);
   }

   public int getPacketSize() {
      return 9;
   }
}
