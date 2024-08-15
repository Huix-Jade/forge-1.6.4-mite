package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntity implements IInventory {
   private ItemStack[] dispenserContents = new ItemStack[9];
   private Random dispenserRandom = new Random();

   public int getSizeInventory() {
      return 9;
   }

   public ItemStack getStackInSlot(int par1) {
      return this.dispenserContents[par1];
   }

   public ItemStack decrStackSize(int par1, int par2) {
      if (this.dispenserContents[par1] != null) {
         ItemStack var3;
         if (this.dispenserContents[par1].stackSize <= par2) {
            var3 = this.dispenserContents[par1];
            this.dispenserContents[par1] = null;
            this.onInventoryChanged();
            return var3;
         } else {
            var3 = this.dispenserContents[par1].splitStack(par2);
            if (this.dispenserContents[par1].stackSize == 0) {
               this.dispenserContents[par1] = null;
            }

            this.onInventoryChanged();
            return var3;
         }
      } else {
         return null;
      }
   }

   public ItemStack getStackInSlotOnClosing(int par1) {
      if (this.dispenserContents[par1] != null) {
         ItemStack var2 = this.dispenserContents[par1];
         this.dispenserContents[par1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public int getRandomStackFromInventory() {
      int var1 = -1;
      int var2 = 1;

      for(int var3 = 0; var3 < this.dispenserContents.length; ++var3) {
         if (this.dispenserContents[var3] != null && this.dispenserRandom.nextInt(var2++) == 0) {
            var1 = var3;
         }
      }

      return var1;
   }

   public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
      this.dispenserContents[par1] = par2ItemStack;
      if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
         par2ItemStack.stackSize = this.getInventoryStackLimit();
      }

      this.onInventoryChanged();
   }

   public int addItem(ItemStack par1ItemStack) {
      for(int var2 = 0; var2 < this.dispenserContents.length; ++var2) {
         if (this.dispenserContents[var2] == null || this.dispenserContents[var2].itemID == 0) {
            this.setInventorySlotContents(var2, par1ItemStack);
            return var2;
         }
      }

      return -1;
   }

   public String getUnlocalizedInvName() {
      return "container.dispenser";
   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readFromNBT(par1NBTTagCompound);
      NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
      this.dispenserContents = new ItemStack[this.getSizeInventory()];

      for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
         NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
         int var5 = var4.getByte("Slot") & 255;
         if (var5 >= 0 && var5 < this.dispenserContents.length) {
            this.dispenserContents[var5] = ItemStack.loadItemStackFromNBT(var4);
         }
      }

   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeToNBT(par1NBTTagCompound);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.dispenserContents.length; ++var3) {
         if (this.dispenserContents[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("Slot", (byte)var3);
            this.dispenserContents[var3].writeToNBT(var4);
            var2.appendTag(var4);
         }
      }

      par1NBTTagCompound.setTag("Items", var2);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
      return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
   }

   public void openChest() {
   }

   public void closeChest() {
   }

   public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
      return true;
   }

   public void destroyInventory() {
      ItemStack[] item_stacks = this.dispenserContents;

      for(int i = 0; i < item_stacks.length; ++i) {
         item_stacks[i] = null;
      }

   }
}
