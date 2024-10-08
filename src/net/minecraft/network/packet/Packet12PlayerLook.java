package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet12PlayerLook extends Packet10Flying {
   public Packet12PlayerLook() {
      this.rotating = true;
   }

   public Packet12PlayerLook(float par1, float par2, boolean par3) {
      this.yaw = par1;
      this.pitch = par2;
      this.onGround = par3;
      this.rotating = true;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.yaw = par1DataInput.readFloat();
      this.pitch = par1DataInput.readFloat();
      super.readPacketData(par1DataInput);
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeFloat(this.yaw);
      par1DataOutput.writeFloat(this.pitch);
      super.writePacketData(par1DataOutput);
   }

   public int getPacketSize() {
      return 8 + super.getPacketSize();
   }
}
