package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;

public class TileEntitySkull extends TileEntity {
   private int skullType;
   private int skullRotation;
   private String extraType = "";

   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      var1.setByte("SkullType", (byte)(this.skullType & 255));
      var1.setByte("Rot", (byte)(this.skullRotation & 255));
      var1.setString("ExtraType", this.extraType);
   }

   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      this.skullType = var1.getByte("SkullType");
      this.skullRotation = var1.getByte("Rot");
      if (var1.hasKey("ExtraType")) {
         this.extraType = var1.getString("ExtraType");
      }

   }

   public Packet getDescriptionPacket() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.writeToNBT(var1);
      return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 4, var1);
   }

   public void setSkullType(int var1, String var2) {
      this.skullType = var1;
      this.extraType = var2;
   }

   public int getSkullType() {
      return this.skullType;
   }

   public int func_82119_b() {
      return this.skullRotation;
   }

   public void setSkullRotation(int var1) {
      this.skullRotation = var1;
   }

   public String getExtraType() {
      return this.extraType;
   }
}
