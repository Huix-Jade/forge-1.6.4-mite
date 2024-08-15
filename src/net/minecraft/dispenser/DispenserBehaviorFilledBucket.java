package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public final class DispenserBehaviorFilledBucket extends BehaviorDefaultDispenseItem {
   public ItemBucket item_bucket;
   private final BehaviorDefaultDispenseItem defaultDispenserItemBehavior = new BehaviorDefaultDispenseItem();

   public DispenserBehaviorFilledBucket(ItemBucket item_bucket) {
      this.item_bucket = item_bucket;
   }

   public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
      ItemBucket var3 = (ItemBucket)par2ItemStack.getItem();
      int var4 = par1IBlockSource.getXInt();
      int var5 = par1IBlockSource.getYInt();
      int var6 = par1IBlockSource.getZInt();
      EnumFacing var7 = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
      if (var3.tryPlaceContainedLiquid(par1IBlockSource.getWorld(), (EntityPlayer)null, var4 + var7.getFrontOffsetX(), var5 + var7.getFrontOffsetY(), var6 + var7.getFrontOffsetZ(), false)) {
         this.suppress_dispense_particles = true;
         par2ItemStack.itemID = this.item_bucket.getEmptyVessel().itemID;
         par2ItemStack.stackSize = 1;
         return par2ItemStack;
      } else {
         return this.defaultDispenserItemBehavior.dispense(par1IBlockSource, par2ItemStack);
      }
   }
}
