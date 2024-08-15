package net.minecraft.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityRecordPlayer extends TileEntity {
   private ItemStack record;

   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      if (var1.hasKey("RecordItem")) {
         this.func_96098_a(ItemStack.loadItemStackFromNBT(var1.getCompoundTag("RecordItem")));
      } else if (var1.getInteger("Record") > 0) {
         this.func_96098_a(new ItemStack(var1.getInteger("Record"), 1, 0));
      }

   }

   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      if (this.func_96097_a() != null) {
         var1.setCompoundTag("RecordItem", this.func_96097_a().writeToNBT(new NBTTagCompound()));
         var1.setInteger("Record", this.func_96097_a().itemID);
      }

   }

   public ItemStack func_96097_a() {
      return this.record;
   }

   public void func_96098_a(ItemStack var1) {
      this.record = var1;
      this.onInventoryChanged();
   }
}
