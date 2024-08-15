package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet88UpdateStrongboxOwner;
import net.minecraft.util.EnumChestType;

public class TileEntityStrongbox extends TileEntityChest {
   public String owner_name;

   public TileEntityStrongbox() {
   }

   public TileEntityStrongbox(EnumChestType chest_type, Block block) {
      super(chest_type, block);
   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("owner_name")) {
         this.owner_name = par1NBTTagCompound.getString("owner_name");
      }

   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeToNBT(par1NBTTagCompound);
      if (this.owner_name != null) {
         par1NBTTagCompound.setString("owner_name", this.owner_name);
      }

   }

   public Packet getDescriptionPacket() {
      return new Packet88UpdateStrongboxOwner(this.xCoord, this.yCoord, this.zCoord, this.owner_name);
   }

   public void setOwner(EntityPlayer player) {
      this.owner_name = player.username;
   }

   public boolean isOwner(EntityPlayer player) {
      return player.username.equals(this.owner_name);
   }

   public String getUnlocalizedInvName() {
      return "container.strongbox." + this.getBlockMaterial().name;
   }
}
