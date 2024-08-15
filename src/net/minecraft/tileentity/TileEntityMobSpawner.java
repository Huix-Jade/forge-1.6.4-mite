package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;

public class TileEntityMobSpawner extends TileEntity {
   private final MobSpawnerBaseLogic field_98050_a = new TileEntityMobSpawnerLogic(this);

   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      this.field_98050_a.readFromNBT(var1);
   }

   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      this.field_98050_a.writeToNBT(var1);
   }

   public void updateEntity() {
      this.field_98050_a.updateSpawner();
      super.updateEntity();
   }

   public Packet getDescriptionPacket() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.writeToNBT(var1);
      var1.removeTag("SpawnPotentials");
      return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, var1);
   }

   public boolean receiveClientEvent(int var1, int var2) {
      return this.field_98050_a.setDelayToMin(var1) ? true : super.receiveClientEvent(var1, var2);
   }

   public MobSpawnerBaseLogic getSpawnerLogic() {
      return this.field_98050_a;
   }
}
