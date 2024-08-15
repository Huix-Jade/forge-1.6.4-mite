package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.Entity;

public class Packet83EntityTeleportCompact extends Packet34EntityTeleport {
   public Packet83EntityTeleportCompact() {
   }

   public Packet83EntityTeleportCompact(Entity entity) {
      super(entity);
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.entity_id = par1DataInput.readInt();
      this.scaled_pos_x = par1DataInput.readShort();
      this.scaled_pos_y = par1DataInput.readShort();
      this.scaled_pos_z = par1DataInput.readShort();
      this.scaled_yaw = par1DataInput.readByte();
      this.scaled_pitch = par1DataInput.readByte();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeInt(this.entity_id);
      par1DataOutput.writeShort(this.scaled_pos_x);
      par1DataOutput.writeShort(this.scaled_pos_y);
      par1DataOutput.writeShort(this.scaled_pos_z);
      par1DataOutput.write(this.scaled_yaw);
      par1DataOutput.write(this.scaled_pitch);
   }

   public int getPacketSize() {
      return 12;
   }
}
