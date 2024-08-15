package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet27PlayerInput extends Packet {
   private float move_strafing;
   private float move_forward;
   private boolean jumping;
   private boolean sneaking;

   public Packet27PlayerInput() {
   }

   public Packet27PlayerInput(float move_strafing, float move_forward, boolean jumping, boolean sneaking) {
      this.move_strafing = move_strafing;
      this.move_forward = move_forward;
      this.jumping = jumping;
      this.sneaking = sneaking;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.move_strafing = par1DataInput.readFloat();
      this.move_forward = par1DataInput.readFloat();
      this.jumping = par1DataInput.readBoolean();
      this.sneaking = par1DataInput.readBoolean();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeFloat(this.move_strafing);
      par1DataOutput.writeFloat(this.move_forward);
      par1DataOutput.writeBoolean(this.jumping);
      par1DataOutput.writeBoolean(this.sneaking);
   }

   public void processPacket(NetHandler par1NetHandler) {
      par1NetHandler.func_110774_a(this);
   }

   public int getPacketSize() {
      return 10;
   }

   public float getMoveStrafing() {
      return this.move_strafing;
   }

   public float getMoveForward() {
      return this.move_forward;
   }

   public boolean getJumping() {
      return this.jumping;
   }

   public boolean getSneaking() {
      return this.sneaking;
   }
}
