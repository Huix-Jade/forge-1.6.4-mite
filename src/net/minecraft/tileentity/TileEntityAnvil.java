package net.minecraft.tileentity;

import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEntityAnvil extends TileEntity {
   public int damage;

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readFromNBT(par1NBTTagCompound);
      this.damage = par1NBTTagCompound.getInteger("damage");
   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("damage", this.damage);
   }

   public String getUnlocalizedInvName() {
      return "container.repair";
   }

   public void addDamage(World world, int x, int y, int z, int amount) {
      if (world.isRemote) {
         Minecraft.setErrorMessage("addDamage: why adding damage to anvil on client?");
      }

      this.damage += amount;
      int damage_stage = ((BlockAnvil)world.getBlock(this.xCoord, this.yCoord, this.zCoord)).getDamageStage(this.damage);
      if (damage_stage == 3) {
         world.destroyBlock((new BlockBreakInfo(world, x, y, z)).setDroppedSelf(), false);
      } else {
         int metadata = world.getBlockMetadata(x, y, z) & 3;
         world.setBlockMetadataWithNotify(x, y, z, metadata | damage_stage << 2, 3);
      }
   }
}
