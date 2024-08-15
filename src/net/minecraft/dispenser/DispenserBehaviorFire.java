package net.minecraft.dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemDamageResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class DispenserBehaviorFire extends BehaviorDefaultDispenseItem {
   private boolean field_96466_b = true;

   protected ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
      EnumFacing var3 = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
      World var4 = par1IBlockSource.getWorld();
      int var5 = par1IBlockSource.getXInt() + var3.getFrontOffsetX();
      int var6 = par1IBlockSource.getYInt() + var3.getFrontOffsetY();
      int var7 = par1IBlockSource.getZInt() + var3.getFrontOffsetZ();
      if (var4.isAirBlock(var5, var6, var7)) {
         var4.setBlock(var5, var6, var7, Block.fire.blockID);
         ItemDamageResult result = par2ItemStack.tryDamageItem(var4, 1, false);
         if (result != null && result.itemWasDestroyed()) {
            par2ItemStack.stackSize = 0;
         }
      } else if (var4.getBlockId(var5, var6, var7) == Block.tnt.blockID) {
         BlockTNT var10000 = Block.tnt;
         BlockTNT.primeTnt(var4, var5, var6, var7, 1, (EntityLivingBase)null);
         var4.setBlockToAir(var5, var6, var7);
      } else {
         this.field_96466_b = false;
      }

      return par2ItemStack;
   }

   protected void playDispenseSound(IBlockSource par1IBlockSource) {
      if (this.field_96466_b) {
         par1IBlockSource.getWorld().playAuxSFX(1000, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), 0);
      } else {
         par1IBlockSource.getWorld().playAuxSFX(1001, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), 0);
      }

   }
}
