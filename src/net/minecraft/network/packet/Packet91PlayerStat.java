package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class Packet91PlayerStat extends Packet {
   private static final byte INTEGER = 0;
   private static final byte ZERO_OR_ONE = 1;
   private static final byte LONG = 2;
   private byte type;
   public int id;
   public long value;

   public Packet91PlayerStat() {
   }

   private Packet91PlayerStat(int id, long value) {
      StatBase stat = StatList.getStat(id);
      this.determineType(stat);
      if (this.type == 1 && value != 0L) {
         value = 1L;
      }

      this.id = id;
      this.value = value;
   }

   public Packet91PlayerStat(StatBase stat_base, long value) {
      this(stat_base.statId, value);
   }

   private byte determineType(StatBase stat_base) {
      this.type = (byte)(StatList.isEitherZeroOrOne(stat_base) ? 1 : (StatList.hasLongValue(stat_base) ? 2 : 0));
      return this.type;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.id = par1DataInput.readInt();
      StatBase stat = StatList.getStat(this.id);
      this.determineType(stat);
      this.value = this.type == 1 ? (long)par1DataInput.readByte() : (this.type == 2 ? par1DataInput.readLong() : (long)par1DataInput.readInt());
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.id);
      StatBase stat = StatList.getStat(this.id);
      if (this.type == 1) {
         par1DataOutput.writeByte(this.value == 0L ? 0 : 1);
      } else if (this.type == 2) {
         par1DataOutput.writeLong(this.value);
      } else {
         par1DataOutput.writeInt((int)this.value);
      }

   }

   public void processPacket(NetHandler net_handler) {
      net_handler.handlePlayerStat(this);
   }

   public int getPacketSize() {
      return this.type == 1 ? 5 : (this.type == 2 ? 12 : 8);
   }
}
