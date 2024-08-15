package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet62LevelSound extends Packet {
   private String soundName;
   private int effectX;
   private int effectY = Integer.MAX_VALUE;
   private int effectZ;
   private float volume;
   private int pitch;

   public Packet62LevelSound() {
   }

   public Packet62LevelSound(String var1, double var2, double var4, double var6, float var8, float var9) {
      this.soundName = var1;
      this.effectX = (int)(var2 * 8.0);
      this.effectY = (int)(var4 * 8.0);
      this.effectZ = (int)(var6 * 8.0);
      this.volume = var8;
      this.pitch = (int)(var9 * 63.0F);
      if (this.pitch < 0) {
         this.pitch = 0;
      }

      if (this.pitch > 255) {
         this.pitch = 255;
      }

   }

   public void readPacketData(DataInput var1) throws IOException {
      this.soundName = readString(var1, 256);
      this.effectX = var1.readInt();
      this.effectY = var1.readInt();
      this.effectZ = var1.readInt();
      this.volume = var1.readFloat();
      this.pitch = var1.readUnsignedByte();
   }

   public void writePacketData(DataOutput var1) throws IOException {
      writeString(this.soundName, var1);
      var1.writeInt(this.effectX);
      var1.writeInt(this.effectY);
      var1.writeInt(this.effectZ);
      var1.writeFloat(this.volume);
      var1.writeByte(this.pitch);
   }

   public String getSoundName() {
      return this.soundName;
   }

   public double getEffectX() {
      return (double)((float)this.effectX / 8.0F);
   }

   public double getEffectY() {
      return (double)((float)this.effectY / 8.0F);
   }

   public double getEffectZ() {
      return (double)((float)this.effectZ / 8.0F);
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return (float)this.pitch / 63.0F;
   }

   public void processPacket(NetHandler var1) {
      var1.handleLevelSound(this);
   }

   public int getPacketSize() {
      return 24;
   }
}
