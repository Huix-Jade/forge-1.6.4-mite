package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.player.PlayerCapabilities;

public class Packet202PlayerAbilities extends Packet {
   private boolean disableDamage;
   private boolean isFlying;
   private boolean allowFlying;
   private boolean isCreativeMode;
   private float flySpeed;
   private float walkSpeed;

   public Packet202PlayerAbilities() {
   }

   public Packet202PlayerAbilities(PlayerCapabilities var1) {
      this.setDisableDamage(var1.disableDamage);
      this.setFlying(var1.isFlying);
      this.setAllowFlying(var1.allowFlying);
      this.setCreativeMode(var1.isCreativeMode);
      this.setFlySpeed(var1.getFlySpeed());
      this.setWalkSpeed(var1.getWalkSpeed());
   }

   public void readPacketData(DataInput var1) throws IOException {
      byte var2 = var1.readByte();
      this.setDisableDamage((var2 & 1) > 0);
      this.setFlying((var2 & 2) > 0);
      this.setAllowFlying((var2 & 4) > 0);
      this.setCreativeMode((var2 & 8) > 0);
      this.setFlySpeed(var1.readFloat());
      this.setWalkSpeed(var1.readFloat());
   }

   public void writePacketData(DataOutput var1) throws IOException {
      byte var2 = 0;
      if (this.getDisableDamage()) {
         var2 = (byte)(var2 | 1);
      }

      if (this.getFlying()) {
         var2 = (byte)(var2 | 2);
      }

      if (this.getAllowFlying()) {
         var2 = (byte)(var2 | 4);
      }

      if (this.isCreativeMode()) {
         var2 = (byte)(var2 | 8);
      }

      var1.writeByte(var2);
      var1.writeFloat(this.flySpeed);
      var1.writeFloat(this.walkSpeed);
   }

   public void processPacket(NetHandler var1) {
      var1.handlePlayerAbilities(this);
   }

   public int getPacketSize() {
      return 2;
   }

   public boolean getDisableDamage() {
      return this.disableDamage;
   }

   public void setDisableDamage(boolean var1) {
      this.disableDamage = var1;
   }

   public boolean getFlying() {
      return this.isFlying;
   }

   public void setFlying(boolean var1) {
      this.isFlying = var1;
   }

   public boolean getAllowFlying() {
      return this.allowFlying;
   }

   public void setAllowFlying(boolean var1) {
      this.allowFlying = var1;
   }

   public boolean isCreativeMode() {
      return this.isCreativeMode;
   }

   public void setCreativeMode(boolean var1) {
      this.isCreativeMode = var1;
   }

   public float getFlySpeed() {
      return this.flySpeed;
   }

   public void setFlySpeed(float var1) {
      this.flySpeed = var1;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   public void setWalkSpeed(float var1) {
      this.walkSpeed = var1;
   }

   public boolean isRealPacket() {
      return true;
   }

   public boolean containsSameEntityIDAs(Packet var1) {
      return true;
   }
}
