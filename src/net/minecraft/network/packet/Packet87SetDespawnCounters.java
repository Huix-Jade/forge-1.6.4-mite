package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet87SetDespawnCounters extends Packet {
   public int entries;
   public int[] entity_id = new int[64];
   public short[] despawn_counter = new short[64];

   public void add(int entity_id, short despawn_counter) {
      if (this.entries < 64) {
         this.entity_id[this.entries] = entity_id;
         this.despawn_counter[this.entries] = despawn_counter;
         ++this.entries;
      }
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entries = par1DataInput.readByte();

      for(int i = 0; i < this.entries; ++i) {
         this.entity_id[i] = par1DataInput.readInt();
         this.despawn_counter[i] = par1DataInput.readShort();
      }

   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeByte(this.entries);

      for(int i = 0; i < this.entries; ++i) {
         par1DataOutput.writeInt(this.entity_id[i]);
         par1DataOutput.writeShort(this.despawn_counter[i]);
      }

   }

   public void processPacket(NetHandler net_handler) {
      net_handler.handleSetDespawnCounters(this);
   }

   public int getPacketSize() {
      return 1 + this.entries * 6;
   }
}
