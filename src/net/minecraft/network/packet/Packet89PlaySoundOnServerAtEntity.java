package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.entity.Entity;

public class Packet89PlaySoundOnServerAtEntity extends Packet {
   public enum_sound sound;
   public int entity_id;
   public float volume;
   public float pitch;

   public Packet89PlaySoundOnServerAtEntity() {
   }

   public Packet89PlaySoundOnServerAtEntity(enum_sound sound, Entity entity, float volume, float pitch) {
      this.sound = sound;
      this.entity_id = entity.entityId;
      this.volume = volume;
      this.pitch = pitch;
   }

   public void readPacketData(DataInput par1DataInput) throws IOException {
      this.sound = Packet89PlaySoundOnServerAtEntity.enum_sound.values()[par1DataInput.readByte()];
      this.entity_id = par1DataInput.readInt();
      this.volume = par1DataInput.readFloat();
      this.pitch = par1DataInput.readFloat();
   }

   public void writePacketData(DataOutput par1DataOutput) throws IOException {
      par1DataOutput.writeByte(this.sound.ordinal());
      par1DataOutput.writeInt(this.entity_id);
      par1DataOutput.writeFloat(this.volume);
      par1DataOutput.writeFloat(this.pitch);
   }

   public void processPacket(NetHandler net_handler) {
      net_handler.handlePlaySoundOnServerAtEntity(this);
   }

   public int getPacketSize() {
      return 13;
   }

   public static enum enum_sound {
      boat_bump;
   }
}
