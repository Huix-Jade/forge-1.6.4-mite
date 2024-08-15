package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.EnumFace;

public class Packet14BlockDig extends Packet {
   public int xPosition;
   public int yPosition;
   public int zPosition;
   public EnumFace face;
   public int status;

   public Packet14BlockDig() {
   }

   public Packet14BlockDig(int par1, int par2, int par3, int par4, EnumFace face) {
      this.status = par1;
      this.xPosition = par2;
      this.yPosition = par3;
      this.zPosition = par4;
      this.face = face;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.status = par1DataInput.readUnsignedByte();
      this.xPosition = par1DataInput.readInt();
      this.yPosition = par1DataInput.readUnsignedByte();
      this.zPosition = par1DataInput.readInt();
      int face_ordinal = par1DataInput.readUnsignedByte();
      this.face = EnumFace.isValidOrdinal(face_ordinal) ? EnumFace.get(face_ordinal) : null;
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.write(this.status);
      par1DataOutput.writeInt(this.xPosition);
      par1DataOutput.write(this.yPosition);
      par1DataOutput.writeInt(this.zPosition);
      par1DataOutput.write(this.face == null ? -1 : this.face.ordinal());
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.handleBlockDig(this);
   }

   public int getPacketSize() {
      return 11;
   }
}
