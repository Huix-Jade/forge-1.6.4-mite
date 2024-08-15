package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet10Flying extends Packet {
   public double xPosition;
   public double yPosition;
   public double zPosition;
   public double stance;
   public float yaw;
   public float pitch;
   public boolean onGround;
   public boolean moving;
   public boolean rotating;

   public Packet10Flying() {
   }

   public Packet10Flying(boolean var1) {
      this.onGround = var1;
   }

   public void processPacket(NetHandler var1) {
      var1.handleFlying(this);
   }

   public void readPacketData(DataInput var1) throws IOException {
      this.onGround = var1.readUnsignedByte() != 0;
   }

   public void writePacketData(DataOutput var1) throws IOException {
      var1.write(this.onGround ? 1 : 0);
   }

   public int getPacketSize() {
      return 1;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet var1) {
      return true;
   }
}
