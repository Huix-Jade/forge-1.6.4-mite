package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet88UpdateStrongboxOwner extends Packet {
   public int x;
   public int y;
   public int z;
   private String owner_name;

   public Packet88UpdateStrongboxOwner() {
   }

   public Packet88UpdateStrongboxOwner(int x, int y, int z, String owner_name) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.owner_name = owner_name == null ? "" : owner_name;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.x = par1DataInput.readInt();
      this.y = par1DataInput.readInt();
      this.z = par1DataInput.readInt();
      this.owner_name = readString(par1DataInput, 32);
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.x);
      par1DataOutput.writeInt(this.y);
      par1DataOutput.writeInt(this.z);
      writeString(this.owner_name, par1DataOutput);
   }

   public void processPacket(NetHandler net_handler) {
      net_handler.handleUpdateStrongboxOwner(this);
   }

   public int getPacketSize() {
      return 12 + this.owner_name.length();
   }

   public String getOwnerName() {
      return this.owner_name.equals("") ? null : this.owner_name;
   }
}
