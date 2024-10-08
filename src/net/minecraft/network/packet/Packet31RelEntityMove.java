package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.Entity;

public class Packet31RelEntityMove extends Packet30Entity {
   public Packet31RelEntityMove() {
   }

   public Packet31RelEntityMove(Entity entity, byte par2, byte par3, byte par4) {
      super(entity);
      this.xPosition = par2;
      this.yPosition = par3;
      this.zPosition = par4;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      super.readPacketData(par1DataInput);
      this.xPosition = par1DataInput.readByte();
      this.yPosition = par1DataInput.readByte();
      this.zPosition = par1DataInput.readByte();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      super.writePacketData(par1DataOutput);
      par1DataOutput.writeByte(this.xPosition);
      par1DataOutput.writeByte(this.yPosition);
      par1DataOutput.writeByte(this.zPosition);
   }

   public int getPacketSize() {
      return super.getPacketSize() + 3;
   }
}
