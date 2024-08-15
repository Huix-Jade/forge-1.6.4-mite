package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemVessel;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public final class DispenserBehaviorEmptyBucket extends BehaviorDefaultDispenseItem {
   public ItemBucket item_bucket;
   private final BehaviorDefaultDispenseItem defaultDispenserItemBehavior = new BehaviorDefaultDispenseItem();

   public DispenserBehaviorEmptyBucket(ItemBucket item_bucket) {
      this.item_bucket = item_bucket;
   }

   public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
      EnumFacing var3 = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
      World var4 = par1IBlockSource.getWorld();
      int var5 = par1IBlockSource.getXInt() + var3.getFrontOffsetX();
      int var6 = par1IBlockSource.getYInt() + var3.getFrontOffsetY();
      int var7 = par1IBlockSource.getZInt() + var3.getFrontOffsetZ();
      Material var8 = var4.getBlockMaterial(var5, var6, var7);
      var4.getBlockMetadata(var5, var6, var7);
      ItemVessel var10;
      if (var8 == Material.water) {
         var10 = this.item_bucket.getPeerForContents(Material.water);
      } else {
         if (var8 != Material.lava) {
            return super.dispenseStack(par1IBlockSource, par2ItemStack);
         }

         var10 = this.item_bucket.getPeerForContents(Material.lava);
      }

      if (var8 == Material.lava) {
         World world = par1IBlockSource.getWorld();
         if (world.rand.nextFloat() < this.item_bucket.getChanceOfMeltingWhenFilledWithLava()) {
            world.blockFX(EnumBlockFX.item_consumed_by_lava, var5, var6, var7);
            --par2ItemStack.stackSize;
            this.suppress_dispense_particles = true;
            return par2ItemStack;
         }
      }

      var4.setBlockToAir(var5, var6, var7);
      if (--par2ItemStack.stackSize == 0) {
         par2ItemStack.itemID = var10.itemID;
         par2ItemStack.stackSize = 1;
      } else if (((TileEntityDispenser)par1IBlockSource.getBlockTileEntity()).addItem(new ItemStack(var10)) < 0) {
         this.defaultDispenserItemBehavior.dispense(par1IBlockSource, new ItemStack(var10));
      }

      this.suppress_dispense_particles = true;
      return par2ItemStack;
   }
}
